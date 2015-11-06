/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.packet;

import com.taobao.tair.DataEntry;
import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;

public class RequestPutPacket extends BasePacket {
	protected short namespace;
	protected short version;
	protected int expired;
	protected Object key;
	protected Object data;
	protected Object pkey;
	protected byte flag;
	protected boolean is_raw;

	public RequestPutPacket(Transcoder transcoder) {
		super(transcoder);
		this.pcode = TairConstant.TAIR_REQ_PUT_PACKET;
	}

	/**
	 * encode
	 */
	public int encode(){
		writePacketBegin(2000);

		// body
		byteBuffer.put((byte) 0);
		byteBuffer.putShort(namespace);
		byteBuffer.putShort(version);
		byteBuffer.putInt(expired);

		DataEntry entry = new DataEntry(key, data);
		entry.setPkey(pkey);
		entry.setValueFlag(flag);
		entry.setRawValue(is_raw);
		try {
			int rc = entry.encode(byteBuffer, transcoder);
			if (rc != 0) return rc;
		} catch (Throwable e) {
			return 3;
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

	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * @return the expired
	 */
	public int getExpired() {
		return expired;
	}

	/**
	 * @param expired
	 *            the expired to set
	 */
	public void setExpired(int expired) {
		this.expired = expired;
	}

	public boolean isIsRaw() {
		return is_raw;
	}

	public void setIsRaw(boolean is_raw) {
		this.is_raw = is_raw;
	}

	/**
	 * @return the key
	 */
	public Object getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(Object key) {
		this.key = key;
	}

	public byte getFlag() {
		return flag;
	}

	public void setFlag(byte flag) {
		this.flag = flag;
	}

	/**
	 * @return the namespace
	 */
	public short getNamespace() {
		return namespace;
	}

	/**
	 * @param namespace
	 *            the namespace to set
	 */
	public void setNamespace(short namespace) {
		this.namespace = namespace;
	}

	public Object getPkey() {
		return pkey;
	}

	public void setPkey(Object pkey) {
		this.pkey = pkey;
	}

	/**
	 * @return the version
	 */
	public short getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(short version) {
		this.version = version;
	}

}
