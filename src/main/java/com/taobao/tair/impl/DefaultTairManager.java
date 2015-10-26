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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.taobao.tair.packet.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;
import com.taobao.tair.TairManager;
import com.taobao.tair.comm.DefaultTranscoder;
import com.taobao.tair.comm.MultiSender;
import com.taobao.tair.comm.TairClient;
import com.taobao.tair.comm.TairClientFactory;
import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairClientException;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.etc.TairUtil;
import com.taobao.tair.json.Json;

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
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return ResultCode.NSERROR;
		}

		RequestRemovePacket packet = new RequestRemovePacket(transcoder);

		packet.setNamespace((short) namespace);
		packet.addKey(key);

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
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return new Result<DataEntry>(ResultCode.NSERROR);
		}

		RequestGetPacket packet = new RequestGetPacket(transcoder);

		packet.setNamespace((short) namespace);
		packet.addKey(key);

		int ec = packet.encode();

		if (ec == 1) {
			return new Result<DataEntry>(ResultCode.KEYTOLARGE);
		}

		ResultCode rc = ResultCode.CONNERROR;
		BasePacket returnPacket = sendRequest(key, packet, true);

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
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return new Result<Integer>(ResultCode.NSERROR);
		}
		
		if (expireTime < 0)
			return new Result<Integer>(ResultCode.INVALIDARG);

		RequestIncDecPacket packet = new RequestIncDecPacket(transcoder);

		packet.setNamespace((short) namespace);
		packet.setKey(key);
		packet.setCount(value);
		packet.setInitValue(defaultValue);
		packet.setExpireTime(expireTime);

		int ec = packet.encode();

		if (ec == 1) {
			return new Result<Integer>(ResultCode.KEYTOLARGE);
		}

		ResultCode rc = ResultCode.CONNERROR;
		BasePacket returnPacket = sendRequest(key, packet);

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
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return ResultCode.NSERROR;
		}
		
		if (expireTime < 0)
			return ResultCode.INVALIDARG;

		RequestPutPacket packet = new RequestPutPacket(transcoder);

		packet.setNamespace((short) namespace);
		packet.setKey(key);
		packet.setData(value);
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
		BasePacket returnPacket = sendRequest(key, packet);

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

	public Result<List<DataEntry>> getRange(int namespace, Serializable prefix, Serializable keyStart, Serializable keyEnd, int offset, int limit, boolean reverse) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return new Result<List<DataEntry>>(ResultCode.NSERROR);
		}

		RequestGetRangePacket packet = new RequestGetRangePacket(transcoder);
		packet.setNamespace((short)namespace);

		DataEntry prefixEntry = new DataEntry();
		prefixEntry.setKey(prefix);
		DataEntry startEntry = new DataEntry();
		startEntry.setKey(keyStart);
		DataEntry endEntry = new DataEntry();
		endEntry.setKey(keyEnd);

		packet.setKeyStart(startEntry);
		packet.setKeyEnd(endEntry);

		if(limit == 0){
			limit = TairConstant.TAIR_MAX_COUNT;
		}
		packet.setLimit(limit);
		packet.setOffset(offset);
		return null;
	}


/*
	int tair_client_impl::get_range(int area, const data_entry &pkey, const data_entry &start_key, const data_entry &end_key,
	                                int offset, int limit, vector<data_entry *> &values,short type)
	{
		if ( area < 0 || area >= TAIR_MAX_AREA_COUNT) {
			return TAIR_RETURN_INVALID_ARGUMENT;
		}

		if (limit < 0 || offset < 0){
			return TAIR_RETURN_INVALID_ARGUMENT;
		}

		data_entry merge_skey, merge_ekey;
		merge_key(pkey, start_key, merge_skey);
		merge_key(pkey, end_key, merge_ekey);

		if (!key_entry_check(merge_skey) || !key_entry_check(merge_ekey)) {
			return TAIR_RETURN_ITEMSIZE_ERROR;
		}
		vector<uint64_t> server_list;
		if (!get_server_id(merge_skey, server_list)) {
			TBSYS_LOG(WARN, "no dataserver available");
			return TAIR_RETURN_FAILED;
		}
		if (limit == 0)
			limit = RANGE_DEFAULT_LIMIT;

		request_get_range *packet = new request_get_range();
		packet->area = area;
		packet->cmd = type;
		packet->offset = offset;
		packet->limit = limit;
		packet->key_start.set_data(merge_skey.get_data(), merge_skey.get_size());
		packet->key_start.set_prefix_size(merge_skey.get_prefix_size());
		packet->key_end.set_data(merge_ekey.get_data(), merge_ekey.get_size());
		packet->key_end.set_prefix_size(merge_ekey.get_prefix_size());

		int ret = TAIR_RETURN_SEND_FAILED;
		base_packet *tpacket = 0;
		response_get_range *resp = 0;

		wait_object *cwo = this_wait_object_manager->create_wait_object();
		if((ret = send_request(server_list[0],packet,cwo->get_id())) < 0){
			delete packet;
			this_wait_object_manager->destroy_wait_object(cwo);
			TBSYS_LOG(ERROR, "get_range failure: %s %d",get_error_msg(ret), ret);
			return ret;
		}

		if((ret = get_response(cwo,1,tpacket)) < 0){
			this_wait_object_manager->destroy_wait_object(cwo);
			TBSYS_LOG(ERROR, "get_range get_response failure: %s %d ",get_error_msg(ret), ret);
			return ret;
		}

		if(tpacket == 0 || tpacket->getPCode() != TAIR_RESP_GET_RANGE_PACKET){
			TBSYS_LOG(ERROR, "get_range response packet error. pcode: %d", tpacket->getPCode());
			ret = TAIR_RETURN_FAILED;
		}
		else {
			resp = (response_get_range*)tpacket;
			new_config_version = resp->config_version;
			ret = resp->get_code();
			if (ret != TAIR_RETURN_SUCCESS){
				if(ret == TAIR_RETURN_SERVER_CAN_NOT_WORK || ret == TAIR_RETURN_WRITE_NOT_ON_MASTER) {
					//update server table immediately
					send_fail_count = UPDATE_SERVER_TABLE_INTERVAL;
				}
			}
			else {
				for(size_t i = 0; i < resp->key_data_vector->size(); i++) {
					data_entry *data = new data_entry(*((*resp->key_data_vector)[i]));
					values.push_back(data);
				}
				if (resp->get_hasnext()){
					ret = TAIR_HAS_MORE_DATA;
				}
			}
		}

		this_wait_object_manager->destroy_wait_object(cwo);
		return ret;
	}*/
	public Result<List<DataEntry>> getRangeOnlyValue(int namespace, Serializable prefix, Serializable key_start, Serializable key_end, int offset, int limit, boolean reverse) {
		return null;
	}

	public Result<List<DataEntry>> getRangeOnlyKey(int namespace, Serializable prefix, Serializable key_start, Serializable key_end, int offset, int limit, boolean reverse) {
		return null;
	}

	public Result<List<DataEntry>> getRange(int namespace, Serializable prefix, Serializable key_start, Serializable key_end, int offset, int limit) {
		return null;
	}

	public Result<List<DataEntry>> getRangeOnlyValue(int namespace, Serializable prefix, Serializable key_start, Serializable key_end, int offset, int limit) {
		return null;
	}

	public Result<List<DataEntry>> getRangeOnlyKey(int namespace, Serializable prefix, Serializable key_start, Serializable key_end, int offset, int limit) {
		return null;
	}

	public Result<List<DataEntry>> delRange(int namespace, Serializable prefix, Serializable key_start, Serializable key_end, int offset, int limit, boolean reverse) {
		return null;
	}

	// items impl
	public ResultCode addItems(int namespace, Serializable key,
			List<? extends Object> items, int maxCount, int version,
			int expireTime) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return ResultCode.NSERROR;
		}
		
		if (maxCount <= 0 || expireTime < 0) {
			return ResultCode.INVALIDARG;
		}

		RequestAddItemsPacket packet = new RequestAddItemsPacket(transcoder);

		packet.setNamespace((short) namespace);
		packet.setKey(key);
		packet.setData(items);
		packet.setVersion((short) version);
		packet.setExpired(expireTime);
		packet.setMaxCount(maxCount);

		int ec = packet.encode();

		if (ec == 1) {
			return ResultCode.KEYTOLARGE;
		} else if (ec == 2) {
			return ResultCode.VALUETOLARGE;
		} else if (ec == 3) {
			return ResultCode.SERIALIZEERROR;
		} else if (ec == 4) {
			return ResultCode.ITEMTOLARGE;
		}

		ResultCode rc = ResultCode.CONNERROR;
		BasePacket returnPacket = sendRequest(key, packet);

		if ((returnPacket != null) && returnPacket instanceof ReturnPacket) {
			ReturnPacket r = (ReturnPacket) returnPacket;

			if (log.isDebugEnabled()) {
				log.debug("get return packet: " + returnPacket + ", code="
						+ r.getCode() + ", msg=" + r.getMsg());
			}

			if (r.getCode() == 0) {
				rc = ResultCode.SUCCESS;
			} else if (r.getCode() == 2) {
				rc = ResultCode.VERERROR;
			} else {
				rc = ResultCode.valueOf(r.getCode());
			}

			configServer.checkConfigVersion(r.getConfigVersion());
		}

		return rc;
	}

	public Result<DataEntry> getAndRemove(int namespace,
			Serializable key, int offset, int count) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return new Result<DataEntry>(ResultCode.NSERROR);
		}
		
		if (count <= 0) {
			return new Result<DataEntry>(ResultCode.INVALIDARG);
		}
		
		RequestGetAndRemoveItemsPacket packet = new RequestGetAndRemoveItemsPacket(transcoder);

		packet.setNamespace((short) namespace);
		packet.addKey(key);
		packet.setCount(count);
		packet.setOffset(offset);
		packet.setType(Json.ELEMENT_TYPE_INVALID);

		int ec = packet.encode();

		if (ec == 1) {
			return new Result<DataEntry>(ResultCode.KEYTOLARGE);
		} else if (ec == 2) {
			return new Result<DataEntry>(ResultCode.VALUETOLARGE);
		}

		ResultCode rc = ResultCode.CONNERROR;
		BasePacket returnPacket = sendRequest(key, packet);

		DataEntry entry = null;
		
		if ((returnPacket != null) && returnPacket instanceof ResponseGetItemsPacket) {
			ResponseGetItemsPacket r = (ResponseGetItemsPacket) returnPacket;
			
			
			List<DataEntry> entryList = r.getEntryList();

			rc = ResultCode.valueOf(r.getResultCode());
			
			if ((rc.isSuccess() || rc.equals(ResultCode.PARTSUCC)) && entryList.size() > 0) {
				entry = entryList.get(0);
				try {
					entry.setValue(decodeItem((byte[])entry.getValue()));
				} catch (Throwable e1) {
					log.error("ITEM SERIALIZEERROR", e1);
					rc = ResultCode.SERIALIZEERROR;
				}
			}

			configServer.checkConfigVersion(r.getConfigVersion());
		}
		
		return new Result<DataEntry>(rc, entry);
	}

	public Result<DataEntry> getItems(int namespace,
			Serializable key, int offset, int count) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return new Result<DataEntry>(ResultCode.NSERROR);
		}
		
		if (count <= 0) {
			return new Result<DataEntry>(ResultCode.INVALIDARG);
		}
		
		RequestGetItemsPacket packet = new RequestGetItemsPacket(transcoder);

		packet.setNamespace((short) namespace);
		packet.addKey(key);
		packet.setCount(count);
		packet.setOffset(offset);
		packet.setType(Json.ELEMENT_TYPE_INVALID);

		int ec = packet.encode();

		if (ec == 1) {
			return new Result<DataEntry>(ResultCode.KEYTOLARGE);
		} else if (ec == 2) {
			return new Result<DataEntry>(ResultCode.VALUETOLARGE);
		}

		ResultCode rc = ResultCode.CONNERROR;
		BasePacket returnPacket = sendRequest(key, packet);

		DataEntry entry = null;
		
		if ((returnPacket != null) && returnPacket instanceof ResponseGetItemsPacket) {
			ResponseGetItemsPacket r = (ResponseGetItemsPacket) returnPacket;
			
			
			List<DataEntry> entryList = r.getEntryList();

			rc = ResultCode.valueOf(r.getResultCode());

			if (rc.isSuccess() && entryList.size() > 0) {
				entry = entryList.get(0);
				try {
					entry.setValue(decodeItem((byte[])entry.getValue()));
				} catch (Throwable e1) {
					log.error("ITEM SERIALIZEERROR", e1);
					rc = ResultCode.SERIALIZEERROR;
				}
			}
			
			configServer.checkConfigVersion(r.getConfigVersion());
		} 
		
		return new Result<DataEntry>(rc, entry);
	}

	public ResultCode removeItems(int namespace, Serializable key, int offset,
			int count) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return ResultCode.NSERROR;
		}
		
		if (count <= 0) {
			return ResultCode.INVALIDARG;
		}
		
		RequestRemoveItemsPacket packet = new RequestRemoveItemsPacket(transcoder);

		packet.setNamespace((short) namespace);
		packet.addKey(key);
		packet.setCount(count);
		packet.setOffset(offset);

		int ec = packet.encode();

		if (ec == 1) {
			return ResultCode.KEYTOLARGE;
		} else if (ec == 2) {
			return ResultCode.VALUETOLARGE;
		}

		ResultCode rc = ResultCode.CONNERROR;
		BasePacket returnPacket = sendRequest(key, packet);

		if ((returnPacket != null) && returnPacket instanceof ReturnPacket) {
			ReturnPacket r = (ReturnPacket) returnPacket;
			
			rc = ResultCode.valueOf(r.getCode());

			configServer.checkConfigVersion(r.getConfigVersion());
		} 

		return rc;
	}
	
	public Result<Integer> getItemCount(int namespace, Serializable key) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return new Result<Integer>(ResultCode.NSERROR);
		}
		
		RequestGetItemsCountPacket packet = new RequestGetItemsCountPacket(transcoder);

		packet.setNamespace((short) namespace);
		packet.addKey(key);

		int ec = packet.encode();

		if (ec == 1) {
			return new Result<Integer>(ResultCode.KEYTOLARGE);
		} else if (ec == 2) {
			return new Result<Integer>(ResultCode.VALUETOLARGE);
		}

		ResultCode rc = ResultCode.SUCCESS;
		BasePacket returnPacket = sendRequest(key, packet);
		
		int count = 0;
		if ((returnPacket != null) && returnPacket instanceof ReturnPacket) {
			ReturnPacket r = (ReturnPacket) returnPacket;
			
			count = ((ReturnPacket) returnPacket).getCode();
			if (count < 0) {
				rc = ResultCode.valueOf(count);
				count = 0;
			}

			configServer.checkConfigVersion(r.getConfigVersion());
		}

		return new Result<Integer>(rc, count);
	}
	
	List<Object> decodeItem(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		
		int count = buffer.getInt();
		List<Object> results = new ArrayList<Object>(count);
		short dataLength = 0;
		
		for(int i=0; i<count; i++) {
			dataLength = buffer.getShort();
			byte[] data = new byte[dataLength];
			buffer.get(data);
			results.add(transcoder.decode(data));
		}
		return results;
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
}
