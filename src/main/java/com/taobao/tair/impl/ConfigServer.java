/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.tair.comm.ResponseListener;
import com.taobao.tair.comm.TairClient;
import com.taobao.tair.comm.TairClientFactory;
import com.taobao.tair.etc.TairClientException;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.etc.TairUtil;
import com.taobao.tair.packet.BasePacket;
import com.taobao.tair.packet.PacketStreamer;
import com.taobao.tair.packet.RequestGetGroupPacket;
import com.taobao.tair.packet.ResponseGetGroupPacket;
import com.taobao.tair.packet.RequestQueryInfoPacket;
import com.taobao.tair.packet.ResponseQueryInfoPacket;

public class ConfigServer implements ResponseListener {
    private static class Server {
        private List<Long> addrList;
        private List<Long> addrPreferedList;
        public Server(int copyCount) {
            addrList = new ArrayList<>(copyCount);
            addrPreferedList = new ArrayList<>(copyCount);
        }
        public void addServer(long addr, long localAddress) {
            if ((addr & 0xFFFF) == (localAddress & 0xFFFF)) {
                addrPreferedList.add(addr);
            } else if ((addr & 0xFF) == (localAddress & 0xFF)) {
                addrPreferedList.add(addr);
            }
            addrList.add(addr);
        }
        public long getAddr(boolean isRead, Set<Long> aliveNodes) {
            if (addrList.size() == 0) {
                return 0;
            }
            long addr = 0;
            if (!isRead) {
                //对于写场景，返回桶分配表每一组copy的第一个地址
                addr = addrList.get(0);
                if (aliveNodes.contains(addr)) {
                    return addr;
                }
            }
            //对于读取场景，优先从优先列表中随机选取，之后做fallback
            ThreadLocalRandom random = ThreadLocalRandom.current();
            if (addrPreferedList.size() > 0) {
                int idx = random.nextInt(addrPreferedList.size());
                addr = addrPreferedList.get(idx);
                if (aliveNodes.contains(addr)) {
                    return addr;
                }
            }
            //fallback, 随机，loadbalance
            {
                int idx = random.nextInt(addrList.size());
                addr = addrList.get(idx);
                if (aliveNodes.contains(addr)) {
                    return addr;
                }
            }
            //fallback last, 顺序取，三层逻辑层层概率降低，应该不太会发生
            for (int i = 0; i < addrList.size(); i++) {
                addr = addrList.get(i);
                if (aliveNodes.contains(addr)) {
                    return addr;
                }
            }
            return 0;
        }
    }
    
	private static final Log log = LogFactory.getLog(ConfigServer.class);
	private static final int MURMURHASH_M = 0x5bd1e995;
	private String groupName = null;
	private int configVersion = 0;
	private AtomicLong retrieveLastTime = new AtomicLong(0);
	private int lastConfigServerIndex = 0;
	private static int aliveConfigServerIndex = 0;

	private List<String> configServerList = new ArrayList<String>();

	private List<Server> serverList;
	private PacketStreamer pstream;

	private int bucketCount = 0;
	private int copyCount = 0;
	
	private Set<Long> aliveNodes;
	private long localAddress = 0;

	public ConfigServer(String groupName, List<String> configServerList,
			PacketStreamer pstream) {
		this.groupName = groupName;
		this.pstream = pstream;

		for (String host : configServerList)
			this.configServerList.add(host.trim());
	}
	
	/**
	 * 根据拉下来的配置组合新的服务器列表数组
	 * 
	 * @param list
	 * @return
	 */
	private List<Server> generateServerList(List<Long> list) {
	    List<Server> serverList = new ArrayList<>(bucketCount);
	    for (int bucket = 0; bucket < bucketCount; bucket++) {
	        Server server = new Server(copyCount);
	        for (int copy = 0; copy < copyCount; copy++) {
	            int idx = (copy * bucketCount) + bucket;
	            if (idx >= list.size()) {
	                continue;
	            }
	            server.addServer(list.get(idx), localAddress);
	        }
	        serverList.add(server);
        }
	    return serverList;
	}

    /**
     * 根据key和场景（读/写）选取服务器
     * <p>
     * 每个bucket均有一个首选server，如果首选alive，那么全部的读写请求均优先使用它。<br>
     * 但这样，对于跨机房的场景，会导致随机的跨机房访问（对于写请求由于服务器端的位置优先算法必然跨机房）
     * 并且也不利于负载均衡（主要是读请求）<br>
     * 那么这里尝试利用mask（地址相似度？）来进行选取，失败时将会做fallback
     * 
     * @param keyByte
     * @param isRead
     * @return
     */
    public long getServer(byte[] keyByte, boolean isRead) {
        long hash = murMurHash(keyByte);
        int bucket = (int) (hash % bucketCount);
        log.debug("hashcode: " + hash + ", bucket count: " + bucketCount);
        //避免copyOnWrite，先拷贝引用
        List<Server> servers = serverList;
        if (servers != null && servers.size() > bucket) {
            Server server = servers.get(bucket);
            if (server != null) {
                return server.getAddr(isRead, aliveNodes);
            }
        }
        return 0;
    }
    
	public boolean retrieveConfigure() {
		retrieveLastTime.set(System.currentTimeMillis());

		RequestGetGroupPacket packet = new RequestGetGroupPacket(null);

		packet.setGroupName(groupName);
		packet.setConfigVersion(configVersion);

		lastConfigServerIndex = aliveConfigServerIndex;
		
		boolean initSuccess = false;

		for (int i = 0; i < configServerList.size(); i++) {
			int index = lastConfigServerIndex % configServerList.size();
			String addr = configServerList.get(index);

			log.info("init configs from configserver: " + addr);
			
			BasePacket returnPacket = null;
			try {
				TairClient client = TairClientFactory.getInstance().get(addr,
						TairConstant.DEFAULT_TIMEOUT, pstream);
				localAddress = client.getLocalAddr();
				returnPacket = (BasePacket) client.invoke(packet,
						TairConstant.DEFAULT_TIMEOUT);
			} catch (Exception e) {
				log.error("get config failed from: " + addr, e);
			}

			if ((returnPacket != null)
					&& returnPacket instanceof ResponseGetGroupPacket) {
				ResponseGetGroupPacket r = (ResponseGetGroupPacket) returnPacket;

				configVersion = r.getConfigVersion();
				bucketCount = r.getBucketCount();
				copyCount = r.getCopyCount();
				aliveNodes = r.getAliveNodes();
				
				if (aliveNodes == null || aliveNodes.isEmpty()) {
					log.error("fatal error, no datanode is alive");
					continue;
				}
				
				if (log.isInfoEnabled()) {
					for (Long id : aliveNodes) {
						log.info("alive datanode: " + TairUtil.idToAddress(id));
					}
				}
				
				if (bucketCount <= 0 || copyCount <= 0)
					throw new IllegalArgumentException("bucket count or copy count can not be 0");



				if ((r.getServerList() != null)
						&& (r.getServerList().size() > 0)) {
					this.serverList = generateServerList(r.getServerList());
					aliveConfigServerIndex = lastConfigServerIndex;
					if (log.isDebugEnabled()) {
						for (int idx = 0; idx < r.getServerList().size(); idx++) {
							log.debug("+++ " + idx + " => "
									+ TairUtil.idToAddress(r.getServerList().get(idx)));
						}
					}
					if ((this.serverList.size() % bucketCount) != 0) {
						log.error("server size % bucket number != 0, server size: "
										+ this.serverList.size()
										+ ", bucket number"
										+ bucketCount
										+ ", copyCount: " + copyCount);
					} else {
						log.warn("configuration inited with version: " + configVersion
								+ ", bucket count: " + bucketCount + ", copyCount: "
								+ copyCount);
						initSuccess = true;
						break;
					}
				} else {
					log.warn("server list from config server is null or size is 0");
				}

			} else {
				log.error("retrive from config server " + addr
						+ " failed, result: " + returnPacket);
			}

			lastConfigServerIndex++;
		}

		return initSuccess;
	}
    
	
	public Map<String, String>retrieveStat(int qtype, String groupName, long serverId) {

		RequestQueryInfoPacket packet = new RequestQueryInfoPacket(null);
       
		packet.setGroupName(groupName);
		packet.setQtype(qtype);
		packet.setServerId(serverId);
		Map <String, String> statInfo = null;

		for (int i = 0; i < configServerList.size(); i++) {
			int index = lastConfigServerIndex % configServerList.size();
			String addr = configServerList.get(index);

			BasePacket returnPacket = null;
			try {
				TairClient client = TairClientFactory.getInstance().get(addr,
						TairConstant.DEFAULT_TIMEOUT, pstream);
				returnPacket = (BasePacket) client.invoke(packet,
						TairConstant.DEFAULT_TIMEOUT);
			} catch (Exception e) {
				log.error("get stat failed " + addr, e);
			}

			if ((returnPacket != null)
					&& returnPacket instanceof ResponseQueryInfoPacket) {
				ResponseQueryInfoPacket r = (ResponseQueryInfoPacket) returnPacket;
				statInfo = r.getKv();
	         
				break;
			} else {
				log.error("retrive stat from config server " + addr
						+ " failed, result: " + returnPacket);
			}

		}

		return statInfo;
	}
	
	public void checkConfigVersion(int version) {
		if (version == configVersion) {
			return;
		}

		if (retrieveLastTime.get() > (System.currentTimeMillis() - 5000)) {
			log.debug("last check time is less than 5 seconds, need not sync");
			return;
		}

		retrieveLastTime.set(System.currentTimeMillis());

		RequestGetGroupPacket packet = new RequestGetGroupPacket(null);

		packet.setGroupName(groupName);
		// version = 0 force update
		packet.setConfigVersion(version > 0 ? configVersion : 0);

		for (int i = 0; i < configServerList.size(); i++) {
			int index = lastConfigServerIndex % configServerList.size();

			String host = configServerList.get(index);
			try {
				TairClient client = TairClientFactory.getInstance().get(host,
						TairConstant.DEFAULT_TIMEOUT, pstream);
				client.invokeAsync(packet, TairConstant.DEFAULT_TIMEOUT, this);
				break;
			} catch (TairClientException e) {
				log.error("get client failed", e);
			}
			lastConfigServerIndex++;
		}
	}

	public void responseReceived(Object packet) {

		if ((packet != null) && packet instanceof ResponseGetGroupPacket) {
			ResponseGetGroupPacket r = (ResponseGetGroupPacket) packet;
			r.decode();			

			// comment out by ruohai, for bug http://code.taobao.org/trac/tair-client-java/ticket/1
//			if (configVersion >= r.getConfigVersion() && (configVersion - r.getConfigVersion()) < 1000) {
//				log.debug("sync configure returned, but local version is not older than configserver, local version: "
//								+ configVersion + ", configserver version: " + r.getConfigVersion());
//				return;
//			}

			log.warn("configuration synced, oldversion: " + configVersion
					+ ", new verion: " + r.getConfigVersion());

			if (r.getAliveNodes().isEmpty()) {
			    throw new IllegalArgumentException("fatal error, no node is alive");
			}
						
			configVersion = r.getConfigVersion();
			
			aliveNodes = r.getAliveNodes();	
					
			for (Long id : aliveNodes) {
				log.info("alive node: " + TairUtil.idToAddress(id));
			}

			if ((r.getServerList() != null) && (r.getServerList().size() > 0)) {
				this.serverList = generateServerList(r.getServerList());
				if (log.isDebugEnabled()) {
					for (int idx = 0; idx < r.getServerList().size(); idx++) {
						log.debug("+++ " + idx + " => "
								+ TairUtil.idToAddress(r.getServerList().get(idx)));
					}
				}
			}
		}

	}

	public void exceptionCaught(TairClientException exception) {
		log.error("do async request failed", exception);

	}

	private long murMurHash(byte[] key) {
		int len = key.length;
		int h = 97 ^ len;
		int index = 0;

		while (len >= 4) {
			int k = (key[index] & 0xff) | ((key[index + 1] << 8) & 0xff00)
					| ((key[index + 2] << 16) & 0xff0000)
					| (key[index + 3] << 24);

			k *= MURMURHASH_M;
			k ^= (k >>> 24);
			k *= MURMURHASH_M;
			h *= MURMURHASH_M;
			h ^= k;
			index += 4;
			len -= 4;
		}

		switch (len) {
		case 3:
			h ^= (key[index + 2] << 16);

		case 2:
			h ^= (key[index + 1] << 8);

		case 1:
			h ^= key[index];
			h *= MURMURHASH_M;
		}

		h ^= (h >>> 13);
		h *= MURMURHASH_M;
		h ^= (h >>> 15);
		return ((long) h & 0xffffffffL);
	}
}
