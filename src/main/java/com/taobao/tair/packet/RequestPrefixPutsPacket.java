/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.packet;

import java.util.List;

import com.taobao.tair.DataAddCountEntry;
import com.taobao.tair.DataEntry;
import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.KeyCountPack;
import com.taobao.tair.etc.KeyValuePack;
import com.taobao.tair.etc.TairConstant;

public class RequestPrefixPutsPacket extends BasePacket {
	protected short namespace;
	protected short version;
	protected int expired;
	protected Object pKey;

	protected List<KeyValuePack> keyList;
	protected List<KeyCountPack> keyCountList;

	public List<KeyCountPack> getKeyCountList() {
		return keyCountList;
	}

	public void setKeyCountList(List<KeyCountPack> keyCountList) {
		this.keyCountList = keyCountList;
	}

	public List<KeyValuePack> getKeyList() {
		return keyList;
	}

	public void setKeyList(List<KeyValuePack> keyList) {
		this.keyList = keyList;
	}

	public RequestPrefixPutsPacket(Transcoder transcoder) {
		super(transcoder);
		this.pcode = TairConstant.TAIR_REQ_PREFIX_PUTS_PACKET;
	}

	public Object getPkey() {
		return pKey;
	}
	
	public void setPkey(Object pkey) {
		this.pKey = pkey;
	}
	/**b
	 * encode
	 */
	public int encode(){
		writePacketBegin(keyList.size() * 2000);
		// body
		byteBuffer.put((byte) 0);
		byteBuffer.putShort(namespace);
		try {
			//pkey
			int count = 0;
			DataEntry.encode(byteBuffer, pKey, transcoder);
			if (keyList != null) {
				count += keyList.size();
			}
			if (keyCountList != null) {
				count += keyCountList.size();
			}
			byteBuffer.putInt(count);
			if (keyList != null) {
				for (KeyValuePack b : keyList) {
					if (b.getKey().toString().length() > TairConstant.TAIR_KEY_MAX_LENTH) {
						return 1;
					}
					if (b.getValue().toString().length() > TairConstant.TAIR_VALUE_MAX_LENGTH) {
						return 2;
					}
					if (b.getExpire() < 0) {
						return 4;
					}
					DataEntry key = new DataEntry(b.getKey(), b.getValue());
					key.setPkey(pKey);
					key.setVersion(b.getVersion());
					key.setEdate(b.getExpire());
					key.encode(byteBuffer, transcoder);
				}
			}
			if (keyCountList != null) {
				for (KeyCountPack countPack : keyCountList) {
					if (countPack.getKey().toString().length() > TairConstant.TAIR_KEY_MAX_LENTH) {
						return 1;
					}
					if (countPack.getExpire() < 0) {
						return 4;
					}
					DataEntry key = new DataEntry(countPack.getKey(), new DataAddCountEntry(countPack.getCount()));
					key.setPkey(pKey);
					key.setValueFlag((byte)1);
					key.setVersion(countPack.getVersion());
					key.setEdate(countPack.getExpire());
					key.encode(byteBuffer, transcoder);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			return 3; // serialize error
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
