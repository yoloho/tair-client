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

public class RequestGetRangePacket extends BasePacket {
	protected short namespace;
	protected short cmd = 0;
	protected int offset = 0;
	protected int limit = 0;
	protected Object keyStart;
	protected Object keyEnd;
	protected Object pkey;

	public Object getPkey() {
		return pkey;
	}

	public void setPkey(Object pkey) {
		this.pkey = pkey;
	}

	public RequestGetRangePacket(Transcoder transcoder) {
		super(transcoder);
		this.pcode = TairConstant.TAIR_REQ_GET_RANGE_PACKET;
	}

	/**
	 * encode
	 */
	public int encode() {
		writePacketBegin(0);
		// body
		byteBuffer.put((byte) 0);
		byteBuffer.putShort(cmd);
		byteBuffer.putShort(namespace);
		byteBuffer.putInt(offset);
		byteBuffer.putInt(limit);
		try {
			DataEntry skey = new DataEntry(keyStart, null);
			skey.setPkey(pkey);
			int rc = skey.encode(byteBuffer, transcoder);
			if (rc != 0) {
				return rc;
			}
			DataEntry ekey = new DataEntry(keyEnd, null);
			ekey.setPkey(pkey);
			rc = ekey.encode(byteBuffer, transcoder);
			if (rc != 0) {
				return rc;
			}
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

	public short getCmd() {
		return cmd;
	}

	public void setCmd(short cmd) {
		this.cmd = cmd;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public Object getKeyStart() {
		return keyStart;
	}

	public void setKeyStart(Object keyStart) {
		this.keyStart = keyStart;
	}

	public Object getKeyEnd() {
		return keyEnd;
	}

	public void setKeyEnd(Object keyEnd) {
		this.keyEnd = keyEnd;
	}

	/**
	 * @return the namespace
	 */
	public short getNamespace() {
		return namespace;
	}

	/**
	 * @param namespace the namespace to set
	 */
	public void setNamespace(short namespace) {
		this.namespace = namespace;
	}
}