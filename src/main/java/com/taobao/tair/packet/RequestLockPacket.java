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

public class RequestLockPacket extends BasePacket {
	protected short namespace;
	protected Object pkey = null;
	protected Object key = null;
	protected Object value = null;
	protected int lockType = TairConstant.TAIR_LOCK_NONE;

	public RequestLockPacket(Transcoder transcoder) {
		super(transcoder);
		this.pcode = TairConstant.TAIR_REQ_LOCK_PACKET;
	}

	/**
	 * encode
	 */
	public int encode() {
		writePacketBegin(3000);

		// body
		byteBuffer.putShort(namespace);
		byteBuffer.putInt(lockType);
		DataEntry entry = new DataEntry(key, value);
		entry.setPkey(pkey);
		if (lockType == TairConstant.TAIR_LOCK_PUT_AND_LOCK_VALUE) {
			if (value == null) {
				return 4;
			}
		} else {
			entry.setValue(null);
		}
		try {
			entry.encode(byteBuffer, transcoder);
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

	public Object getPkey() {
		return pkey;
	}

	public void setPkey(Object pkey) {
		this.pkey = pkey;
	}

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public int getLockType() {
		return lockType;
	}

	public void setLockType(int lockType) {
		this.lockType = lockType;
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
