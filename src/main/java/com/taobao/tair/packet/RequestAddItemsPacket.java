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

public class RequestAddItemsPacket extends RequestPutPacket {

	protected int maxCount;
	protected List<? extends Object> data;
	private int totalDataSize;

	public RequestAddItemsPacket(Transcoder transcoder) {
		super(transcoder);
		this.pcode = TairConstant.TAIR_REQ_ADDITEMS_PACKET;
	}

	public int encode() {
		byte[] keyByte = transcoder.encode(key);
		List<byte[]> dataByte = null;
		
		try {
			dataByte = encodeItemValue();
		} catch (IllegalArgumentException e) {
			return 4; // item value to large
		}

		if (dataByte == null || keyByte == null)
			return 3; // serialize failed
		
		if (keyByte.length >= TairConstant.TAIR_KEY_MAX_LENTH) {
			return 1;
		}

		if (totalDataSize >= TairConstant.TAIR_VALUE_MAX_LENGTH) {
			return 2;
		}
    
		totalDataSize += (4 + dataByte.size() * 2);
		writePacketBegin(keyByte.length + totalDataSize);

		// body
		byteBuffer.put((byte) 0);
		byteBuffer.putShort(namespace);
		byteBuffer.putShort(version);
		byteBuffer.putInt(expired);
		
		fillMetas();
		// XXX It should be verified that ttl(expire) doesn't needed here in meta header
		DataEntry.encodeMetaEmpty(byteBuffer);
		byteBuffer.putInt(keyByte.length);
		byteBuffer.put(keyByte);

		fillMetas();
		DataEntry.encodeMetaEmpty(byteBuffer);
		byteBuffer.putInt(totalDataSize);
		byteBuffer.putInt(dataByte.size());
		for (int i=0; i<dataByte.size(); ++i) {
			byte[] bb = dataByte.get(i);
			byteBuffer.putShort((short)bb.length);
			byteBuffer.put(bb);
		}
		
		byteBuffer.putInt(maxCount);

		writePacketEnd();

		return 0;
	}
	
	public List<byte[]> encodeItemValue() {
		if (data != null && data.size() > 0) {
			List<byte[]> bytes = new ArrayList<byte[]>(data.size());
			for (Object obj : data) {
				if (obj == null) return null;
				byte[] b = transcoder.encode(obj);
				if (b.length >= TairConstant.TAIR_ITEM_MAX_SIZE) {
					bytes = null;
					throw new IllegalArgumentException();
				}
				totalDataSize += b.length;
				bytes.add(b);
			}
			return bytes;
		}
		return null;
	}

	public boolean decode() {
		throw new UnsupportedOperationException();
	}
	
	public void setData(List<? extends Object> data) {
		this.data = data;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}
}
