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
	protected DataEntry keyStart;
	protected DataEntry keyEnd;

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
		keyStart.encodeMeta(byteBuffer);
		keyEnd.encodeMeta(byteBuffer);

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

	public DataEntry getKeyStart() {
		return keyStart;
	}

	public void setKeyStart(DataEntry keyStart) {
		this.keyStart = keyStart;
	}

	public DataEntry getKeyEnd() {
		return keyEnd;
	}

	public void setKeyEnd(DataEntry keyEnd) {
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