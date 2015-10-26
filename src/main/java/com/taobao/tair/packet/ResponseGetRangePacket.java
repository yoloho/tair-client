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

import java.util.ArrayList;
import java.util.List;

public class ResponseGetRangePacket extends BasePacket {
	protected int configVersion;
	protected List<DataEntry> entryList;
	protected List<DataEntry> proxiedKeyList;
	protected int resultCode;

	public ResponseGetRangePacket(Transcoder transcoder) {
		super(transcoder);
		this.pcode = TairConstant.TAIR_RESP_GET_PACKET;
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
		short cmd = byteBuffer.getShort();
		int count = byteBuffer.getInt();
		short flag = byteBuffer.getShort();

		int size = 0;
		Object key = null;
		Object value = null;

		this.entryList = new ArrayList<DataEntry>(count);

		for (int i = 0; i < count; i++) {
			DataEntry de = new DataEntry();
			removeMetas();
			de.decodeMeta(byteBuffer);

			size = byteBuffer.getInt();

			if (size > 0) {
				key = transcoder.decode(byteBuffer.array(), byteBuffer
						.position(), size);
				byteBuffer.position(byteBuffer.position() + size);
			}
			de.setKey(key);
			this.entryList.add(de);
		}

		if (count > 1) {
			byteBuffer.getInt();
		}

		return true;
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
