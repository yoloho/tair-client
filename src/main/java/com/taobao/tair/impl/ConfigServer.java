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
	private static final Log log = LogFactory.getLog(ConfigServer.class);
	private static final int MURMURHASH_M = 0x5bd1e995;
	private String groupName = null;
	private int configVersion = 0;
	private AtomicLong retrieveLastTime = new AtomicLong(0);
	private int lastConfigServerIndex = 0;
	private static int aliveConfigServerIndex = 0;

	private List<String> configServerList = new ArrayList<String>();

	private List<Long> serverList;
	private PacketStreamer pstream;

	private int bucketCount = 0;
	private int copyCount = 0;
	
	private Set<Long> aliveNodes;

	public ConfigServer(String groupName, List<String> configServerList,
			PacketStreamer pstream) {
		this.groupName = groupName;
		this.pstream = pstream;

		for (String host : configServerList)
			this.configServerList.add(host.trim());
	}

	public long getServer(byte[] keyByte, boolean isRead) {
		long addr = 0;
		long hash = murMurHash(keyByte); // cast to int is safe
		log.debug("hashcode: " + hash + ", bucket count: " + bucketCount);
		if ((serverList != null) && (serverList.size() > 0)) {
			hash %= bucketCount;
			log.debug("bucket: " + hash);
			long s = serverList.get((int)hash);
			log.debug("oroginal target server: " + TairUtil.idToAddress(s) + " alive server: " + aliveNodes);
			if (aliveNodes.contains(s))
				addr = s;
		}
		
		if (addr == 0 && isRead) {
			int i = 0;
			for (i=1; i<copyCount; i++) {
				int index = ((int)hash) + i * bucketCount;
				if (index >= serverList.size())
					break;
				long s = serverList.get(index);
				log.debug("read operation try: " + TairUtil.idToAddress(s));
				if (aliveNodes.contains(s)) {
					addr = s;
					break;
				}
			}
		}

		return addr;
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
					this.serverList = r.getServerList();
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
		packet.setConfigVersion(version);

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

			configVersion = r.getConfigVersion();
						
			aliveNodes = r.getAliveNodes();	
			if (aliveNodes.isEmpty())
				throw new IllegalArgumentException("fatal error, no node is alive");
					
			for (Long id : aliveNodes) {
				log.info("alive node: " + TairUtil.idToAddress(id));
			}

			if ((r.getServerList() != null) && (r.getServerList().size() > 0)) {
				this.serverList = r.getServerList();
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
