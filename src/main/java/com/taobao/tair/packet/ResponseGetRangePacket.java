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
import java.util.List;

import com.taobao.tair.DataEntry;
import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;

public class ResponseGetRangePacket extends BasePacket {
	protected int configVersion;
	protected List<DataEntry> entryList;
	protected List<DataEntry> proxiedKeyList;
	protected int resultCode;
	protected short cmd;
	protected short flag;

	public ResponseGetRangePacket(Transcoder transcoder) {
		super(transcoder);
		this.pcode = TairConstant.TAIR_RESP_GET_RANGE_PACKET;
	}

	/**
	 * encode
	 */
	public int encode() {
		throw new UnsupportedOperationException();
	}

	/**
	 * decode
	 */
	public boolean decode() {
		this.configVersion = byteBuffer.getInt();
		resultCode = byteBuffer.getInt();
		cmd = byteBuffer.getShort();
		int count = byteBuffer.getInt();
		flag = byteBuffer.getShort();

		this.entryList = new ArrayList<DataEntry>(count);
		if (cmd == TairConstant.TAIR_GET_RANGE_ALL) {
			for (int i = 0; i < count; i += 2) {
				DataEntry de = new DataEntry();
				de.decodeKeyValue(byteBuffer, transcoder);
				this.entryList.add(de);
			}
		} else if (cmd == TairConstant.TAIR_GET_RANGE_ONLY_KEY) {
			for (int i = 0; i < count; i ++) {
				DataEntry de = new DataEntry();
				de.decodeKey(byteBuffer, transcoder);
				this.entryList.add(de);
			}
		} else if (cmd == TairConstant.TAIR_GET_RANGE_ONLY_VALUE) {
			for (int i = 0; i < count; i ++) {
				DataEntry de = new DataEntry();
				de.decodeValue(byteBuffer, transcoder);
				this.entryList.add(de);
			}
		}

		if (count > 1) {
			byteBuffer.getInt();
		}

		return true;
	}

	public short getFlag() {
		return flag;
	}

	public void setFlag(short flag) {
		this.flag = flag;
	}

	public short getCmd() {
		return cmd;
	}

	public void setCmd(short cmd) {
		this.cmd = cmd;
	}

	public List<DataEntry> getEntryList() {
		return entryList;
	}

	public void setEntryList(List<DataEntry> entryList) {
		this.entryList = entryList;
	}

	/**
	 * @return the configVersion
	 */
	public int getConfigVersion() {
		return configVersion;
	}

	/**
	 * @param configVersion the configVersion to set
	 */
	public void setConfigVersion(int configVersion) {
		this.configVersion = configVersion;
	}

	public int getResultCode() {
		return resultCode;
	}

}
