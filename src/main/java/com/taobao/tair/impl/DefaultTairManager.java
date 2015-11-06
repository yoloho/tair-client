/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.tair.CallMode;
import com.taobao.tair.DataAddCountEntry;
import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;
import com.taobao.tair.TairCallback;
import com.taobao.tair.TairManager;
import com.taobao.tair.comm.DefaultTranscoder;
import com.taobao.tair.comm.MultiSender;
import com.taobao.tair.comm.TairClient;
import com.taobao.tair.comm.TairClientFactory;
import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.CounterPack;
import com.taobao.tair.etc.KeyCountPack;
import com.taobao.tair.etc.KeyValuePack;
import com.taobao.tair.etc.TairClientException;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.etc.TairUtil;
import com.taobao.tair.packet.BasePacket;
import com.taobao.tair.packet.MultiReturnPacket;
import com.taobao.tair.packet.RequestCommandCollection;
import com.taobao.tair.packet.RequestGetPacket;
import com.taobao.tair.packet.RequestGetRangePacket;
import com.taobao.tair.packet.RequestIncDecPacket;
import com.taobao.tair.packet.RequestLockPacket;
import com.taobao.tair.packet.RequestPrefixGetPacket;
import com.taobao.tair.packet.RequestPrefixPutsPacket;
import com.taobao.tair.packet.RequestPrefixRemovesPacket;
import com.taobao.tair.packet.RequestPutPacket;
import com.taobao.tair.packet.RequestRemovePacket;
import com.taobao.tair.packet.ResponseGetPacket;
import com.taobao.tair.packet.ResponseGetRangePacket;
import com.taobao.tair.packet.ResponseIncDecPacket;
import com.taobao.tair.packet.ResponsePrefixGetsPacket;
import com.taobao.tair.packet.ReturnPacket;
import com.taobao.tair.packet.TairPacketStreamer;

public class DefaultTairManager implements TairManager {
	private static final Log log = LogFactory.getLog(DefaultTairManager.class);
	private static final String clientVersion = "TairClient 2.3.1";
	private List<String> configServerList = null;
	private String groupName = null;
	private ConfigServer configServer = null;
	private MultiSender multiSender = null;
	private int timeout = TairConstant.DEFAULT_TIMEOUT;
	private int maxWaitThread = TairConstant.DEFAULT_WAIT_THREAD;
	private TairPacketStreamer packetStreamer = null;
	private Transcoder transcoder = null;
	private int compressionThreshold = 0;
	private String charset = null;
	private String name = null;
	private AtomicInteger failCounter = new AtomicInteger(0);

	public DefaultTairManager() {
		this("DefaultTairManager");
	}

	public DefaultTairManager(String name) {
		this.name = name;
	}

	public void init() {
		transcoder = new DefaultTranscoder(compressionThreshold, charset);
		packetStreamer = new TairPacketStreamer(transcoder);
		configServer = new ConfigServer(groupName, configServerList,
				packetStreamer);
		if (!configServer.retrieveConfigure()) {
			throw new RuntimeException("init config failed");
		}
		multiSender = new MultiSender(packetStreamer);
		log.warn(name + " [" + getVersion() + "] started...");
	}

	private TairClient getClient(Object key, boolean isRead) {
		long address = configServer.getServer(transcoder.encode(key), isRead);
		
		if (address == 0)
			return null;

		String host = TairUtil.idToAddress(address);
		if (host != null) {
			try {
				return TairClientFactory.getInstance().get(host, timeout,
						packetStreamer);
			} catch (TairClientException e) {
				log.error("getClient failed " + host, e);
			}
		}

		return null;
	}

	private BasePacket sendRequest(Object key, BasePacket packet) {
		return sendRequest(key, packet, false);
	}

	private BasePacket sendRequest(Object key, BasePacket packet, boolean isRead) {
		TairClient client = getClient(key, isRead);

		if (client == null) {
			int value = failCounter.incrementAndGet();

			if (value > 100) {
				configServer.checkConfigVersion(0);
				failCounter.set(0);
				log.warn("connection failed happened 100 times, sync configuration");
			}

			log.warn("conn is null ");
			return null;
		}

		long startTime = System.currentTimeMillis();
		BasePacket returnPacket = null;
		try {
			returnPacket = (BasePacket) client.invoke(packet, timeout);
		} catch (TairClientException e) {
			log.error("send request to " + client + " failed " + e);
		}
		long endTime = System.currentTimeMillis();

		if (returnPacket == null) {

			if (failCounter.incrementAndGet() > 100) {
				configServer.checkConfigVersion(0);
				failCounter.set(0);
				log.warn("connection failed happened 100 times, sync configuration");
			}

			return null;
		} else {
			if (log.isDebugEnabled()) {
				log.debug("key=" + key + ", timeout: " + timeout + ", used: "
						+ (endTime - startTime) + " (ms), client: " + client);
			}
		}

		return returnPacket;
	}

	public Result<Integer> decr(int namespace, Serializable key, int value,
			int defaultValue, int expireTime) {
		if (value < 0)
			return new Result<Integer>(ResultCode.ITEMSIZEERROR);
		
		return addCount(namespace, key, -value, defaultValue, expireTime);
	}

	public ResultCode delete(int namespace, Serializable key) {
		return delete(namespace, null, key);
	}
	public ResultCode delete(int namespace, Serializable pkey, Serializable key) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return ResultCode.NSERROR;
		}

		RequestRemovePacket packet = new RequestRemovePacket(transcoder);

		packet.setNamespace((short) namespace);
		packet.addKey(key);
		packet.setPkey(pkey);

		int ec = packet.encode();

		if (ec == 1) {
			return ResultCode.KEYTOLARGE;
		}

		ResultCode rc = ResultCode.CONNERROR;
		BasePacket returnPacket = sendRequest(key, packet);

		if ((returnPacket != null) && returnPacket instanceof ReturnPacket) {
			rc = ResultCode.valueOf(((ReturnPacket) returnPacket).getCode());
		
		}

		return rc;
	}

	public ResultCode invalid(int namespace, Serializable key) {
		return delete(namespace, key);
	}

	public ResultCode minvalid(int namespace, List<? extends Object> keys) {
		return mdelete(namespace, keys);
	}

	public Result<DataEntry> get(int namespace, Serializable key) {
		return get(namespace, null, key);
	}
	private Result<DataEntry> get(int namespace, Serializable pkey, Serializable key) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return new Result<DataEntry>(ResultCode.NSERROR);
		}

		RequestGetPacket packet = new RequestGetPacket(transcoder);

		packet.setNamespace((short) namespace);
		packet.addKey(key);
		packet.setPkey(pkey);

		int ec = packet.encode();

		if (ec == 1) {
			return new Result<DataEntry>(ResultCode.KEYTOLARGE);
		}

		ResultCode rc = ResultCode.CONNERROR;
		BasePacket returnPacket = sendRequest(pkey == null ? key : pkey, packet, true);

		if ((returnPacket != null) && returnPacket instanceof ResponseGetPacket) {
			ResponseGetPacket r = (ResponseGetPacket) returnPacket;

			DataEntry resultObject = null;

			List<DataEntry> entryList = r.getEntryList();

			rc = ResultCode.valueOf(r.getResultCode());
			
			if (rc == ResultCode.SUCCESS && entryList.size() > 0)
				resultObject = entryList.get(0);

			configServer.checkConfigVersion(r.getConfigVersion());

			return new Result<DataEntry>(rc, resultObject);
		}

		return new Result<DataEntry>(rc);
	}

	public String getVersion() {
		return clientVersion;
	}

	public Result<Integer> incr(int namespace, Serializable key, int value,
			int defaultValue, int expireTime) {
		if (value < 0)
			return new Result<Integer>(ResultCode.ITEMSIZEERROR);
		
		return addCount(namespace, key, value, defaultValue, expireTime);		
	}
	
	private Result<Integer> addCount(int namespace, Serializable key, int value,
			int defaultValue, int expireTime) {
		return addCount(namespace, null, key, value, defaultValue, expireTime);
	}
	
	private Result<Integer> addCount(int namespace, Serializable pkey, Serializable key, int value,
			int defaultValue, int expireTime) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return new Result<Integer>(ResultCode.NSERROR);
		}
		
		if (expireTime < 0)
			return new Result<Integer>(ResultCode.INVALIDARG);

		RequestIncDecPacket packet = new RequestIncDecPacket(transcoder);

		packet.setNamespace((short) namespace);
		packet.setKey(key);
		packet.setPkey(pkey);
		packet.setCount(value);
		packet.setInitValue(defaultValue);
		packet.setExpireTime(expireTime);

		int ec = packet.encode();

		if (ec == 1) {
			return new Result<Integer>(ResultCode.KEYTOLARGE);
		}

		ResultCode rc = ResultCode.CONNERROR;
		BasePacket returnPacket = sendRequest(pkey == null ? key : pkey, packet);

		if ((returnPacket != null)) {
			if (returnPacket instanceof ResponseIncDecPacket) {
				ResponseIncDecPacket r = (ResponseIncDecPacket) returnPacket;

				rc = ResultCode.SUCCESS;

				configServer.checkConfigVersion(r.getConfigVersion());

				return new Result<Integer>(rc, r.getValue());
			} else if (returnPacket instanceof ReturnPacket) {
				ReturnPacket rp = (ReturnPacket) returnPacket;
				rc = ResultCode.valueOf(rp.getCode());
				configServer.checkConfigVersion(rp.getConfigVersion());
			}

		}
		
		return new Result<Integer>(rc);
	}

	public ResultCode mdelete(int namespace, List<? extends Object> keys) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return ResultCode.NSERROR;
		}

		RequestCommandCollection rcc = new RequestCommandCollection();

		for (Object key : keys) {
			long address = configServer
					.getServer(transcoder.encode(key), false);

			if (address == 0) {
				continue;
			}

			RequestRemovePacket packet = (RequestRemovePacket) rcc
					.findRequest(address);

			if (packet == null) {
				packet = new RequestRemovePacket(transcoder);
				packet.setNamespace((short) namespace);
				packet.addKey(key);
				rcc.addRequest(address, packet);
			} else {
				packet.addKey(key);
			}
		}

		int reqSize = 0;

		for (BasePacket p : rcc.getRequestCommandMap().values()) {
			RequestGetPacket rp = (RequestGetPacket) p;

			reqSize += rp.getKeyList().size();

			// check key size
			int ec = rp.encode();

			if (ec == 1) {
				log.error("key too larget: ");
				return ResultCode.KEYTOLARGE;
			}
		}

		ResultCode rc = ResultCode.CONNERROR;
		boolean ret = multiSender.sendRequest(rcc, timeout);

		if (ret) {
			int maxConfigVersion = 0;

			rc = ResultCode.SUCCESS;

			for (BasePacket rp : rcc.getResultList()) {
				if (rp instanceof ReturnPacket) {
					ReturnPacket returnPacket = (ReturnPacket) rp;
					returnPacket.decode();

					if (returnPacket.getConfigVersion() > maxConfigVersion) {
						maxConfigVersion = returnPacket.getConfigVersion();
					}

					ResultCode drc = ResultCode.valueOf(returnPacket.getCode());
					if (drc.isSuccess() == false) {
						log.debug("mdelete not return success, result code: " + ResultCode.valueOf(returnPacket.getCode()));
						rc = ResultCode.PARTSUCC;
					}
				}
			}

			configServer.checkConfigVersion(maxConfigVersion);
		}

		return rc;
	}

	public Result<List<DataEntry>> mget(int namespace,
			List<? extends Object> keys) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return new Result<List<DataEntry>>(ResultCode.NSERROR);
		}

		RequestCommandCollection rcc = new RequestCommandCollection();

		for (Object key : keys) {
			long address = configServer.getServer(transcoder.encode(key), true);

			if (address == 0) {
				continue;
			}

			RequestGetPacket packet = (RequestGetPacket) rcc
					.findRequest(address);

			if (packet == null) {
				packet = new RequestGetPacket(transcoder);
				packet.setNamespace((short) namespace);
				packet.addKey(key);
				rcc.addRequest(address, packet);
			} else {
				packet.addKey(key);
			}
		}

		int reqSize = 0;

		for (BasePacket p : rcc.getRequestCommandMap().values()) {
			RequestGetPacket rp = (RequestGetPacket) p;

			// calculate uniq key number
			reqSize += rp.getKeyList().size();

			// check key size
			int ec = rp.encode();

			if (ec == 1) {
				log.error("key too larget: ");
				return new Result<List<DataEntry>>(ResultCode.KEYTOLARGE);
			}
		}

		boolean ret = multiSender.sendRequest(rcc, timeout);

		if (!ret) {
			return new Result<List<DataEntry>>(ResultCode.CONNERROR);
		}

		List<DataEntry> results = new ArrayList<DataEntry>();

		ResultCode rc = ResultCode.SUCCESS;
		ResponseGetPacket resp = null;

		int maxConfigVersion = 0;
		ResultCode trc = ResultCode.SUCCESS; // single response rc
		for (BasePacket bp : rcc.getResultList()) {
			if (bp instanceof ResponseGetPacket) {
				resp = (ResponseGetPacket) bp;
				resp.decode();
				trc = ResultCode.valueOf(resp.getResultCode());
				if (!trc.isSuccess() && !trc.equals(ResultCode.PARTSUCC)) {
					// error occure
					rc = ResultCode.valueOf(resp.getResultCode());
					//results = null;
				} else {
					results.addAll(resp.getEntryList());
				}

				// calculate max config version
				if (resp.getConfigVersion() > maxConfigVersion) {
					maxConfigVersion = resp.getConfigVersion();
				}
			} else {
				log.warn("receive wrong packet type: " + bp);
			}
		}

		configServer.checkConfigVersion(maxConfigVersion);
		
		if (results.size() == 0 && rc.equals(ResultCode.SUCCESS)) {
			rc = ResultCode.DATANOTEXSITS;
		}
		if (results.size() > 0 && results.size() != reqSize) {
			rc = ResultCode.PARTSUCC;
		}

		return new Result<List<DataEntry>>(rc, results);
	}

	public ResultCode put(int namespace, Serializable key, Serializable value) {
		return put(namespace, key, value, 0, 0);
	}

	public ResultCode put(int namespace, Serializable key, Serializable value,
			int version) {
		return put(namespace, key, value, version, 0);
	}
	
	public ResultCode put(int namespace, Serializable key, Serializable value,
			int version, int expireTime) {
		return put(namespace, null, key, value, version, expireTime);
	}

	public ResultCode put(int namespace, Serializable pkey, Serializable key, Serializable value,
			int version, int expireTime) {
		return put(namespace, pkey, key, value, version, expireTime, (byte)0);
	}
	
	private ResultCode put(int namespace, Serializable pkey, Serializable key, Serializable value,
			int version, int expireTime, byte flag) {
		return put(namespace, pkey, key, value, version, expireTime, flag, false);
	}
	private ResultCode put(int namespace, Serializable pkey, Serializable key, Serializable value,
			int version, int expireTime, byte flag, boolean is_raw) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return ResultCode.NSERROR;
		}
		
		if (expireTime < 0)
			return ResultCode.INVALIDARG;

		RequestPutPacket packet = new RequestPutPacket(transcoder);

		packet.setNamespace((short) namespace);
		packet.setKey(key);
		packet.setPkey(pkey);
		packet.setData(value);
		packet.setFlag(flag);
		packet.setVersion((short) version);
		packet.setExpired(expireTime);

		int ec = packet.encode();

		if (ec == 1) {
			return ResultCode.KEYTOLARGE;
		} else if (ec == 2) {
			return ResultCode.VALUETOLARGE;
		} else if (ec == 3) {
			return ResultCode.SERIALIZEERROR;
		}

		ResultCode rc = ResultCode.CONNERROR;
		BasePacket returnPacket = sendRequest(pkey != null ? pkey : key, packet);

		if ((returnPacket != null) && returnPacket instanceof ReturnPacket) {
			ReturnPacket r = (ReturnPacket) returnPacket;

			if (log.isDebugEnabled()) {
				log.debug("get return packet: " + returnPacket + ", code="
						+ r.getCode() + ", msg=" + r.getMsg());
			}

			rc = ResultCode.valueOf(r.getCode());

			configServer.checkConfigVersion(r.getConfigVersion());
		}

		return rc;
	}

	public Result<List<DataEntry>> getRange(int namespace, Serializable prefix, Serializable keyStart, Serializable keyEnd, int offset, int limit) {
		return getRange(namespace, prefix, keyStart, keyEnd, offset, limit, TairConstant.TAIR_GET_RANGE_ALL);
	}
	public Result<List<DataEntry>> getRange(int namespace, Serializable prefix, Serializable keyStart, Serializable keyEnd, int offset, int limit, short type) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return new Result<List<DataEntry>>(ResultCode.NSERROR);
		}

		RequestGetRangePacket packet = new RequestGetRangePacket(transcoder);
		packet.setNamespace((short)namespace);
		packet.setCmd(type);
		packet.setKeyStart(keyStart);
		packet.setKeyEnd(keyEnd);
		packet.setPkey(prefix);

		if(limit == 0){
			limit = TairConstant.TAIR_MAX_COUNT;
		}
		packet.setLimit(limit);
		packet.setOffset(offset);
		int ec = packet.encode();
		if (ec == 1) {
			return new Result<List<DataEntry>>(ResultCode.KEYTOLARGE);
		} else if (ec == 2) {
			return new Result<List<DataEntry>>(ResultCode.VALUETOLARGE);
		} else if (ec == 3) {
			return new Result<List<DataEntry>>(ResultCode.SERIALIZEERROR);
		}

		ResultCode rc = ResultCode.CONNERROR;
		BasePacket returnPacket = sendRequest(prefix, packet, true);

		if ((returnPacket != null) && returnPacket instanceof ResponseGetRangePacket) {
			ResponseGetRangePacket r = (ResponseGetRangePacket) returnPacket;

			List<DataEntry> entryList = r.getEntryList();
			rc = ResultCode.valueOf(r.getResultCode());
			configServer.checkConfigVersion(r.getConfigVersion());

			return new Result<List<DataEntry>>(rc, entryList);
		}

		return new Result<List<DataEntry>>(rc);
	}


	public Result<List<DataEntry>> getRangeOnlyValue(int namespace, Serializable prefix, Serializable key_start, Serializable key_end, int offset, int limit) {
		return getRange(namespace, prefix, key_start, key_end, offset, limit, TairConstant.TAIR_GET_RANGE_ONLY_VALUE);
	}

	public Result<List<DataEntry>> getRangeOnlyKey(int namespace, Serializable prefix, Serializable key_start, Serializable key_end, int offset, int limit) {
		return getRange(namespace, prefix, key_start, key_end, offset, limit, TairConstant.TAIR_GET_RANGE_ONLY_KEY);
	}

	public Result<List<DataEntry>> delRange(int namespace, Serializable prefix, Serializable key_start, Serializable key_end, int offset, int limit, boolean reverse) {
		return null;
	}
   
	public Map<String,String> getStat(int qtype, String groupName, long serverId){
		Map<String, String> temp = configServer.retrieveStat(qtype, groupName, serverId);
		return temp;
	}
	
	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public int getCompressionThreshold() {
		return compressionThreshold;
	}

	public void setCompressionThreshold(int compressionThreshold) {
		if (compressionThreshold <= TairConstant.TAIR_KEY_MAX_LENTH) {
			log.warn("compress threshold can not smaller than ["
					+ TairConstant.TAIR_KEY_MAX_LENTH + "], you provided:["
					+ compressionThreshold + "]");
		} else {
			this.compressionThreshold = compressionThreshold;
		}
	}

	public List<String> getConfigServerList() {
		return configServerList;
	}

	public void setConfigServerList(List<String> configServerList) {
		this.configServerList = configServerList;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getMaxWaitThread() {
		return maxWaitThread;
	}

	public void setMaxWaitThread(int maxWaitThread) {
		this.maxWaitThread = maxWaitThread;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String toString() {
		return name + " " + getVersion();
	}

	public Result<DataEntry> get(int namespace, Serializable key, int expireTime) {
		Result<DataEntry> result = get(namespace, key);
		if (result.isSuccess()) {
			put(namespace, key, (Serializable)result.getValue().getValue(), result.getValue().getVersion(), expireTime);
		}
		return result;
	}

	public ResultCode putAsync(int namespace, Serializable key, Serializable value, int version, int expireTime,
			boolean fillCache, TairCallback cb) {
		throw new UnsupportedOperationException();
	}

	public ResultCode put(int namespace, Serializable key, Serializable value, int version, int expireTime, boolean fillCache) {
		throw new UnsupportedOperationException();
	}

	public ResultCode invalid(int namespace, Serializable key, CallMode callMode) {
		throw new UnsupportedOperationException();
	}

	public ResultCode hide(int namespace, Serializable key) {
		throw new UnsupportedOperationException();
	}

	public ResultCode hideByProxy(int namespace, Serializable key) {
		throw new UnsupportedOperationException();
	}

	public ResultCode hideByProxy(int namespace, Serializable key, CallMode callMode) {
		throw new UnsupportedOperationException();
	}

	public Result<DataEntry> getHidden(int namespace, Serializable key) {
		throw new UnsupportedOperationException();
	}

	public ResultCode prefixPut(int namespace, Serializable pkey, Serializable skey, Serializable value) {
		return prefixPut(namespace, pkey, skey, value, 0);
	}

	public ResultCode prefixPut(int namespace, Serializable pkey, Serializable skey, Serializable value, int version) {
		return prefixPut(namespace, pkey, skey, value, version, 0);
	}

	public ResultCode prefixPut(int namespace, Serializable pkey, Serializable sKey, Serializable value, int version,
			int expireTime) {
		return put(namespace, pkey, sKey, value, version, expireTime);
	}

	public Result<Map<Object, ResultCode>> prefixPuts(int namespace, Serializable pkey, List<KeyValuePack> keyValuePacks) {
		return prefixPuts(namespace, pkey, keyValuePacks, null);
	}

	public Result<Map<Object, ResultCode>> prefixPuts(int namespace, Serializable pkey, List<KeyValuePack> keyValuePacks,
			List<KeyCountPack> keyCountPacks) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return new Result<Map<Object,ResultCode>>(ResultCode.NSERROR);
		}
		RequestPrefixPutsPacket packet = new RequestPrefixPutsPacket(transcoder);
		packet.setNamespace((short) namespace);
		packet.setPkey(pkey);
		packet.setKeyList(keyValuePacks);
		packet.setKeyCountList(keyCountPacks);
		int ec = packet.encode();
		if (ec == 1) {
			return new Result<Map<Object,ResultCode>>(ResultCode.KEYTOLARGE);
		} else if (ec == 2) {
			return new Result<Map<Object,ResultCode>>(ResultCode.VALUETOLARGE);
		} else if (ec == 3) {
			return new Result<Map<Object,ResultCode>>(ResultCode.SERIALIZEERROR);
		} else if (ec == 4) {
			return new Result<Map<Object,ResultCode>>(ResultCode.INVALIDARG);
		}
		ResultCode rc = ResultCode.CONNERROR;
		BasePacket returnPacket = sendRequest(pkey, packet);

		if ((returnPacket != null) && returnPacket instanceof MultiReturnPacket) {
			MultiReturnPacket r = (MultiReturnPacket) returnPacket;
			if (log.isDebugEnabled()) {
				log.debug("get multi return packet: " + returnPacket + ", code="
						+ r.getCode() + ", msg=" + r.getMsg());
			}

			rc = ResultCode.valueOf(r.getCode());

			configServer.checkConfigVersion(r.getConfigVersion());
		}

		return new Result<Map<Object,ResultCode>>(rc);
	}

	public Result<DataEntry> prefixGet(int namespace, Serializable pkey, Serializable skey) {
		return get(namespace, pkey, skey);
	}

	public Result<Map<Object, Result<DataEntry>>> prefixGets(int namespace, Serializable pkey,
			List<? extends Serializable> skeyList) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return new Result<Map<Object,Result<DataEntry>>>(ResultCode.NSERROR);
		}

		RequestPrefixGetPacket packet = new RequestPrefixGetPacket(transcoder);

		packet.setNamespace((short) namespace);
		packet.setPkey(pkey);
		for (Serializable serializable : skeyList) {
			packet.addKey(serializable);
		}

		int ec = packet.encode();
		if (ec == 1) {
			return new Result<Map<Object,Result<DataEntry>>>(ResultCode.KEYTOLARGE);
		}

		ResultCode rc = ResultCode.CONNERROR;
		BasePacket returnPacket = sendRequest(pkey, packet, true);

		if ((returnPacket != null) && returnPacket instanceof ResponsePrefixGetsPacket) {
			ResponsePrefixGetsPacket r = (ResponsePrefixGetsPacket) returnPacket;
			Map<Object,Result<DataEntry>> resultObject = new HashMap<Object, Result<DataEntry>>();
			List<DataEntry> entryList = r.getEntryList();
			rc = ResultCode.valueOf(r.getResultCode());
			Result<Map<Object, Result<DataEntry>>> result = new Result<Map<Object,Result<DataEntry>>>(rc, resultObject);
			if (entryList != null && entryList.size() > 0) {
				for (int i = 0; i < entryList.size(); i++) {
					resultObject.put(entryList.get(i).getKey(), new Result<DataEntry>(ResultCode.valueOf(entryList.get(i).getReturnCode()), entryList.get(i)));
				}
			}
			List<DataEntry> failedList = r.getFailedKeyList();
			if (failedList != null && failedList.size() > 0) {
				for (int i = 0; i < failedList.size(); i++) {
					resultObject.put(failedList.get(i).getKey(), new Result<DataEntry>(ResultCode.valueOf(failedList.get(i).getReturnCode()), null));
				}
			}
			configServer.checkConfigVersion(r.getConfigVersion());
			return result;
		}

		return new Result<Map<Object,Result<DataEntry>>>(rc);
	}

	public ResultCode prefixDelete(int namespace, Serializable pkey, Serializable sKey) {
		return delete(namespace, pkey, sKey);
	}

	public ResultCode prefixDeletes(int namespace, Serializable pkey, List<? extends Serializable> skeys) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return ResultCode.NSERROR;
		}

		RequestPrefixRemovesPacket packet = new RequestPrefixRemovesPacket(transcoder);

		packet.setNamespace((short) namespace);
		packet.setPkey(pkey);
		for (Serializable key : skeys) {
			packet.addKey(key);
		}

		int ec = packet.encode();

		if (ec == 1) {
			return ResultCode.KEYTOLARGE;
		}

		ResultCode rc = ResultCode.CONNERROR;
		BasePacket returnPacket = sendRequest(pkey, packet);

		if ((returnPacket != null) && returnPacket instanceof ReturnPacket) {
			rc = ResultCode.valueOf(((ReturnPacket) returnPacket).getCode());
		}

		return rc;
	}

	public Result<Integer> prefixIncr(int namespace, Serializable pkey, Serializable skey, int value, int defaultValue,
			int expireTime) {
		return addCount(namespace, pkey, skey, value, defaultValue, expireTime);
	}

	public Result<Integer> prefixIncr(int namespace, Serializable pkey, Serializable skey, int value, int defaultValue,
			int expireTime, int lowBound, int upperBound) {
		throw new UnsupportedOperationException();
	}

	public Result<Map<Object, Result<Integer>>> prefixIncrs(int namespace, Serializable pkey, List<CounterPack> packList) {
		Map<Object, Result<Integer>> result = new HashMap<Object, Result<Integer>>();
		ResultCode rc = ResultCode.INVALIDARG;
		if (packList != null && packList.size() > 0) {
			for (CounterPack counterPack : packList) {
				Result<Integer> r = prefixIncr(namespace, pkey, (Serializable) counterPack.getKey(), counterPack.getCount(), counterPack.getInitValue(), counterPack.getExpire());
				result.put(counterPack.getKey(), r);
				rc = ResultCode.valueOf(rc.getCode());
			}
		}
		return new Result<>(rc, result);
	}

	public Result<Map<Object, Result<Integer>>> prefixIncrs(int namespace, Serializable pkey, List<CounterPack> packList,
			int lowBound, int upperBound) {
		throw new UnsupportedOperationException();
	}

	public Result<Integer> prefixDecr(int namespace, Serializable pkey, Serializable skey, int value, int defaultValue,
			int expireTime) {
		return addCount(namespace, pkey, skey, -value, defaultValue, expireTime);
	}

	public Result<Integer> prefixDecr(int namespace, Serializable pkey, Serializable skey, int value, int defaultValue,
			int expireTime, int lowBound, int upperBound) {
		throw new UnsupportedOperationException();
	}

	public Result<Map<Object, Result<Integer>>> prefixDecrs(int namespace, Serializable pkey, List<CounterPack> packList) {
		Map<Object, Result<Integer>> result = new HashMap<Object, Result<Integer>>();
		ResultCode rc = ResultCode.INVALIDARG;
		if (packList != null && packList.size() > 0) {
			for (CounterPack counterPack : packList) {
				Result<Integer> r = prefixDecr(namespace, pkey, (Serializable) counterPack.getKey(), -counterPack.getCount(), counterPack.getInitValue(), counterPack.getExpire());
				result.put(counterPack.getKey(), r);
				rc = ResultCode.valueOf(rc.getCode());
			}
		}
		return new Result<>(rc, result);
	}

	public Result<Map<Object, Result<Integer>>> prefixDecrs(int namespace, Serializable pkey, List<CounterPack> packList,
			int lowBound, int upperBound) {
		throw new UnsupportedOperationException();
	}

	public ResultCode prefixSetCount(int namespace, Serializable pkey, Serializable skey, int count) {
		return prefixSetCount(namespace, pkey, skey, count, 0, 0);
	}

	public ResultCode prefixSetCount(int namespace, Serializable pkey, Serializable skey, int count, int version, int expireTime) {
		return put(namespace, pkey, skey, new DataAddCountEntry(count), version, expireTime, (byte)1);
	}

	public Result<Map<Object, ResultCode>> prefixSetCounts(int namespace, Serializable pkey, List<KeyCountPack> keyCountPacks) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResultCode prefixHide(int namespace, Serializable pkey, Serializable skey) {
		throw new UnsupportedOperationException();
	}

	public Result<DataEntry> prefixGetHidden(int namespace, Serializable pkey, Serializable skey) {
		throw new UnsupportedOperationException();
	}

	public Result<Map<Object, Result<DataEntry>>> prefixGetHiddens(int namespace, Serializable pkey,
			List<? extends Serializable> skeys) {
		throw new UnsupportedOperationException();
	}

	public Result<Map<Object, ResultCode>> prefixHides(int namespace, Serializable pkey, List<? extends Serializable> skeys) {
		throw new UnsupportedOperationException();
	}

	public ResultCode prefixInvalid(int namespace, Serializable pkey, Serializable skey, CallMode callMode) {
		throw new UnsupportedOperationException();
	}

	public ResultCode prefixHideByProxy(int namespace, Serializable pkey, Serializable skey, CallMode callMode) {
		throw new UnsupportedOperationException();
	}

	public Result<Map<Object, ResultCode>> prefixHidesByProxy(int namespace, Serializable pkey,
			List<? extends Serializable> skeys, CallMode callMode) {
		throw new UnsupportedOperationException();
	}

	public Result<Map<Object, ResultCode>> prefixInvalids(int namespace, Serializable pkey, List<? extends Serializable> skeys,
			CallMode callMode) {
		throw new UnsupportedOperationException();
	}

	public Result<Map<Object, Map<Object, Result<DataEntry>>>> mprefixGetHiddens(int namespace,
			Map<? extends Serializable, ? extends List<? extends Serializable>> pkeySkeyListMap) {
		throw new UnsupportedOperationException();
	}

	public ResultCode setCount(int namespace, Serializable key, int count) {
		return setCount(namespace, key, count, 0, 0);
	}

	public ResultCode setCount(int namespace, Serializable key, int count, int version, int expireTime) {
		return put(namespace, null, key, new DataAddCountEntry(count), version, expireTime, (byte)1);
	}
	
	public ResultCode lock(int namespace, Serializable key) {
		return lock(namespace, null, key);
	}
	
	public ResultCode lock(int namespace, Serializable prefix, Serializable key) {
		return lock(namespace, prefix, key, TairConstant.TAIR_LOCK_VALUE);
	}

	private ResultCode lock(int namespace, Serializable prefix, Serializable key, int lockType) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return ResultCode.NSERROR;
		}

		RequestLockPacket packet = new RequestLockPacket(transcoder);
		packet.setNamespace((short)namespace);
		packet.setLockType(lockType);
		packet.setPkey(prefix);
		packet.setKey(key);

		int ec = packet.encode();
		if (ec == 4) {
			return ResultCode.INVALIDARG;
		} else if (ec == 3) {
			return ResultCode.SERIALIZEERROR;
		}

		ResultCode rc = ResultCode.CONNERROR;
		BasePacket returnPacket = sendRequest(prefix == null ? key : prefix, packet, true);

		if ((returnPacket != null) && returnPacket instanceof ReturnPacket) {
			ReturnPacket r = (ReturnPacket) returnPacket;
			configServer.checkConfigVersion(r.getConfigVersion());
			return ResultCode.valueOf(r.getCode());
		}

		return rc;
	}

	public ResultCode unlock(int namespace, Serializable key) {
		return unlock(namespace, null, key);
	}
	
	public ResultCode unlock(int namespace, Serializable prefix, Serializable key) {
		return lock(namespace, prefix, key, TairConstant.TAIR_LOCK_VALUE_UNLOCK);
	}

	public Result<List<Object>> mlock(int namespace, List<? extends Object> keys) {
		return mlock(namespace, keys, null);
	}

	public Result<List<Object>> mlock(int namespace, List<? extends Object> keys, Map<Object, ResultCode> failKeysMap) {
		return mlock(namespace, keys, failKeysMap, TairConstant.TAIR_LOCK_VALUE);
	}
	private Result<List<Object>> mlock(int namespace, List<? extends Object> keys, Map<Object, ResultCode> failKeysMap, int lockType) {
		ArrayList<Object> succList = new ArrayList<Object>();
		for (Object key : keys) {
			ResultCode result = lock(namespace, null, (Serializable)key, lockType);
			if (!result.isSuccess() && failKeysMap != null) {
				failKeysMap.put(key, result);
			} else {
				succList.add(key);
			}
		}
		return new Result<List<Object>>(ResultCode.SUCCESS, succList);
	}

	@Override
	public Result<List<Object>> munlock(int namespace, List<? extends Object> keys) {
		return munlock(namespace, keys, null);
	}

	public Result<List<Object>> munlock(int namespace, List<? extends Object> keys, Map<Object, ResultCode> failKeysMap) {
		return mlock(namespace, keys, failKeysMap, TairConstant.TAIR_LOCK_VALUE_UNLOCK);
	}

	public ResultCode append(int namespace, byte[] key, byte[] value) {
		throw new UnsupportedOperationException();
	}

	public void setMaxFailCount(int failCount) {
		throw new UnsupportedOperationException();
	}

	public int getMaxFailCount() {
		throw new UnsupportedOperationException();
	}
}
