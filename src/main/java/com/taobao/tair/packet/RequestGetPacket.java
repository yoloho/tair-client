/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.packet;

import java.util.HashSet;
import java.util.Set;

import com.taobao.tair.DataEntry;
import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;

public class RequestGetPacket extends BasePacket {
	protected short namespace;
	protected Object pkey = null;
	protected Set<Object> keyList = new HashSet<Object>();

	public RequestGetPacket(Transcoder transcoder) {
		super(transcoder);
		this.pcode = TairConstant.TAIR_REQ_GET_PACKET;
	}

	/**
	 * encode
	 */
	public int encode() {
		writePacketBegin(1060 * keyList.size());

		// body
		byteBuffer.put((byte) 0);
		byteBuffer.putShort(namespace);
		byteBuffer.putInt(keyList.size());

		for (Object key : keyList) {
			DataEntry entry = new DataEntry(key, null);
			entry.setPkey(pkey);
			int rc;
			try {
				rc = entry.encode(byteBuffer, transcoder);
			} catch (Throwable e) {
				return 3;
			}
			if (rc != 0) {
				return rc;
			}
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

	public Object getPkey() {
		return pkey;
	}

	public void setPkey(Object pkey) {
		this.pkey = pkey;
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
