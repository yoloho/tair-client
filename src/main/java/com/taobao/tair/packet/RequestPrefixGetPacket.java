/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.packet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.taobao.tair.DataEntry;
import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;

public class RequestPrefixGetPacket extends BasePacket {
	protected short namespace;
	protected Object pkey;
	protected Set<Object> keyList = new HashSet<Object>();

	public RequestPrefixGetPacket(Transcoder transcoder) {
		super(transcoder);
		this.pcode = TairConstant.TAIR_REQ_PREFIX_GETS_PACKET;
	}

	/**
	 * encode
	 */
	public int encode() {
		int capacity = 0;
		List<byte[]> list = new ArrayList<byte[]>();

		byte[] pkey_data = transcoder.encode(pkey);
		capacity += 40;
		capacity += pkey_data.length;
		for (Object key : keyList) {
			byte[] keyByte = transcoder.encode(key);

			if (keyByte.length >= TairConstant.TAIR_KEY_MAX_LENTH) {
				return 1;
			}

			list.add(keyByte);
			capacity += 40;
			capacity += pkey_data.length;
			capacity += keyByte.length;
		}

		writePacketBegin(capacity);

		// body
		byteBuffer.put((byte) 0);
		byteBuffer.putShort(namespace);
		byteBuffer.putInt(list.size());

		for (byte[] keyByte : list) {
			fillMetas();
			DataEntry.encodeMetaEmpty(byteBuffer);
			byteBuffer.putInt((keyByte.length + pkey_data.length) | pkey_data.length << 22);
			byteBuffer.put(pkey_data);
			byteBuffer.put(keyByte);
		}
		writePacketEnd();

		return 0;
	}

	/**
	 * decode
	 */
	public boolean decode() {
		throw new UnsupportedOperationException();
	}

	public Object getPkey() {
		return pkey;
	}

	public void setPkey(Object pkey) {
		this.pkey = pkey;
	}

	public boolean addKey(Object key) {
		return this.keyList.add(key);
	}

	/**
	 * 
	 * @return the keyList
	 */
	public Set<Object> getKeyList() {
		return keyList;
	}

	/**
	 * 
	 * @param keyList
	 *            the keyList to set
	 */
	public void setKeyList(Set<Object> keyList) {
		this.keyList = keyList;
	}

	/**
	 * 
	 * @return the namespace
	 */
	public short getNamespace() {
		return namespace;
	}

	/**
	 * 
	 * @param namespace
	 *            the namespace to set
	 */
	public void setNamespace(short namespace) {
		this.namespace = namespace;
	}
}
