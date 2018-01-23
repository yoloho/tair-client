/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair;

import java.io.Serializable;
import java.nio.ByteBuffer;

import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.etc.TairUtil;

/**
 * data entry object, includes key, value and meta infomations
 */
@SuppressWarnings("unused")
public class DataEntry implements Serializable {

	private static final long serialVersionUID = -3492001385938512871L;
	private static byte[] DEFAULT_DATA = new byte[29];
	private Object pkey;
	private Object key;
	private Object value;
	private boolean has_merged = false;
	private boolean raw_value = false;
	private int return_code = 0;

	private byte[] pkeyData;
	private byte[] keyData;
	private byte[] valueData;

	// meta data

	private int magic;
	private int checkSum;
	private int keySize;
	private int version;
	private int padSize;
	private int valueSize;
	private byte flag;
	private int cdate;
	private int mdate;
	private int edate;
	
	private byte value_flag;

	public DataEntry() {
	}

	public DataEntry(Object value) {
		this.value = value;
	}

	public DataEntry(Object key, Object value) {
		this.key = key;
		this.value = value;
	}

	public DataEntry(Object key, Object value, int version) {
		this.key = key;
		this.value = value;
		this.version = version;
	}

	public Object getPkey() {
		return pkey;
	}

	public void setPkey(Object pkey) {
		this.pkey = pkey;
		if (pkey != null) {
			has_merged = true;
		} else {
			has_merged = false;
		}
	}
	
	public int getEdate() {
		return edate;
	}

	public void setEdate(int edate) {
		this.edate = edate;
	}

	public boolean isRawValue() {
		return raw_value;
	}

	public void setRawValue(boolean raw_value) {
		this.raw_value = raw_value;
	}

	public int getReturnCode() {
		return return_code;
	}

	public void setReturnCode(int return_code) {
		this.return_code = return_code;
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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public void decodeMeta(ByteBuffer bytes) {
		magic = bytes.getShort();
		checkSum = bytes.getShort();
		keySize = bytes.getShort();
		version = bytes.getShort();
		padSize = bytes.getInt();		
		valueSize = bytes.getInt();
		flag = bytes.get();
		cdate = bytes.getInt();
		mdate = bytes.getInt();
		edate = bytes.getInt();
	}

	public static void encodeMeta(ByteBuffer bytes, int flag) {
		bytes.put(DEFAULT_DATA);
		if (flag != 0) {
			// put flag implicitly
			bytes.put(bytes.position() - 13, (byte)flag);
		}
	}

	public static void encodeMeta(ByteBuffer bytes, DataEntry de) {
		bytes.putShort((short)de.magic);
		bytes.putShort((short)de.checkSum);
		bytes.putShort((short)de.keySize);
		bytes.putShort((short)de.version);
		bytes.putInt(de.padSize);
		bytes.putInt(de.valueSize);
		bytes.put((byte)de.flag);
		bytes.putInt(de.cdate);
		bytes.putInt(de.mdate);
		bytes.putInt(de.edate);
	}

	public int size(Transcoder transcoder) {
		if (has_merged) {
			pkeyData = transcoder.encode(pkey);
		}
		keyData = transcoder.encode(key);
		valueData = transcoder.encode(value, raw_value);
		return pkeyData == null ? keyData.length + valueData.length : pkeyData.length + keyData.length + valueData.length ;
	}
	
	public int encode(ByteBuffer bytes, Transcoder transcoder) {
		if (key != null) {
			fillMetas(bytes);
			encodeMeta(bytes);
			if (keyData == null) {
				keyData = transcoder.encode(key);
			}
			if (has_merged) {
				if (pkeyData == null) {
					pkeyData = transcoder.encode(pkey);
				}
				if (keyData.length + pkeyData.length > TairConstant.TAIR_KEY_MAX_LENTH) {
					return 1;
				}
				bytes.putInt((keyData.length + pkeyData.length) | pkeyData.length << 22);
				bytes.put(pkeyData);
			} else {
				bytes.putInt(keyData.length);
			}
			bytes.put(keyData);
		}
		if (value != null) {
			fillMetas(bytes);
			encodeMeta(bytes);
			if (value_flag > 0) {
				int pos = bytes.position();
				bytes.put(pos - 13, value_flag);
			}
			if (valueData == null) {
				valueData = transcoder.encode(value, raw_value);
			}
			if (valueData.length > TairConstant.TAIR_VALUE_MAX_LENGTH) {
				return 2;
			}
			bytes.putInt(valueData.length);
			bytes.put(valueData);
		}
		return 0;
	}
	
	public static int encode(ByteBuffer bytes, Object key, Transcoder transcoder) {
		fillMetas(bytes);
		encodeMeta(bytes);
		byte[] data = transcoder.encode(key);
		bytes.putInt(data.length);
		bytes.put(data);
		return data.length;
	}
	
	public void decodeKey(ByteBuffer bytes, Transcoder transcoder) {
		Object key = "";
		removeMetas(bytes);
		decodeMeta(bytes);
		int size = bytes.getInt();
		if (size > 0) {
			int prefix_size = size >> 22;
			size &= 0x3fffff;
			if (prefix_size > 0) {
				Object pkey = transcoder.decode(bytes.array(), bytes.position(), prefix_size);
				setPkey(pkey);
				bytes.position(bytes.position() + prefix_size);
			}
			key = transcoder.decode(bytes.array(), bytes.position(), size);
			bytes.position(bytes.position() + size);
		}
		setKey(key);
	}
	
	public void decodeValue(ByteBuffer bytes, Transcoder transcoder) {
		Object val = "";
		removeMetas(bytes);
		decodeMeta(bytes);
		int size = bytes.getInt();
		if (size > 0) {
			val = transcoder.decode(bytes.array(), bytes.position(), size);
			bytes.position(bytes.position() + size);
		}
		setValue(val);
	}
	
	public void decodeKeyValue(ByteBuffer bytes, Transcoder transcoder) {
		Object key = "";
		Object val = "";
		removeMetas(bytes);
		decodeMeta(bytes);
		int size = bytes.getInt();
		if (size > 0) {
			int prefix_size = size >> 22;
			size &= 0x3fffff;
			if (prefix_size > 0) {
				Object pkey = transcoder.decode(bytes.array(), bytes.position(), prefix_size);
				setPkey(pkey);
				bytes.position(bytes.position() + prefix_size);
			}
			key = transcoder.decode(bytes.array(), bytes.position(), size - prefix_size);
			bytes.position(bytes.position() + size - prefix_size);
		}
		setKey(key);
		removeMetas(bytes);
		decodeMeta(bytes);
		size = bytes.getInt();
		if (size > 0) {
			val = transcoder.decode(bytes.array(), bytes.position(), size);
			bytes.position(bytes.position() + size);
		}
		setValue(val);
	}
	
	
	
	public byte getValueFlag() {
		return value_flag;
	}

	public void setValueFlag(byte flag) {
		this.value_flag = flag;
	}

	public static void encodeMeta(ByteBuffer bytes) {
		bytes.put(DEFAULT_DATA);
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (pkey != null) {
			sb.append("pkey: ").append(pkey).append("\n\t");
		}
		sb.append("key: ").append(key).append("\n\t");
		sb.append("value: ").append(value);
		sb.append(", version: ").append(version).append("\n\t");
		sb.append("flag: ").append(flag).append("\n\t");
		sb.append("cdate: ").append(TairUtil.formatDate(cdate)).append("\n\t");
		sb.append("mdate: ").append(TairUtil.formatDate(mdate)).append("\n\t");
		sb.append("edate: ").append(edate > 0 ? TairUtil.formatDate(edate) : "NEVER").append("\n");
		return sb.toString();
	}
	
	protected static void fillMetas(ByteBuffer byteBuffer) {
    	byte[] data = new byte[7];
		byteBuffer.put(data);
	}
	
	protected static void removeMetas(ByteBuffer byteBuffer) {
		byteBuffer.get(); // isMerged
		byteBuffer.getInt(); // area
		byteBuffer.getShort(); // serverFlag
	}

}
