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

import com.taobao.tair.etc.TairUtil;

/**
 * data entry object, includes key, value and meta infomations
 */
@SuppressWarnings("unused")
public class DataEntry implements Serializable {

	private static final long serialVersionUID = -3492001385938512871L;
	private static byte[] DEFAULT_DATA = new byte[29];
	private Object key;
	private Object value;

	// meta data

	private int magic;
	private int checkSum;
	private int keySize;
	private int version;
	private int padSize;
	private int valueSize;
	private int flag;
	private int cdate;
	private int mdate;
	private int edate;

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

	public static void encodeMeta(ByteBuffer bytes) {
		bytes.put(DEFAULT_DATA);
	}
	public static void merge_key(DataEntry prefixKey, DataEntry orignalKey, DataEntry returnEntry) {
//		int pkey_size = prefixKey
//		int skey_size = skey.get_size();
//		char *buf = (char *)malloc(pkey_size + skey_size);
//		memcpy(buf, pkey.get_data(), pkey_size);
//		memcpy(buf + pkey_size, skey.get_data(), skey_size);
//		mkey.set_alloced_data(buf, pkey_size + skey_size);
//		mkey.set_prefix_size(pkey_size);
	}
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("key: ").append(key);
		sb.append(", value: ").append(value);
		sb.append(", version: ").append(version).append("\n\t");
		sb.append("cdate: ").append(TairUtil.formatDate(cdate)).append("\n\t");
		sb.append("mdate: ").append(TairUtil.formatDate(mdate)).append("\n\t");
		sb.append("edate: ").append(edate > 0 ? TairUtil.formatDate(edate) : "NEVER").append("\n");
		return sb.toString();
	}

}
