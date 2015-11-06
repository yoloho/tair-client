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

public class ResponsePrefixGetsPacket extends BasePacket {
	protected int             configVersion;
    protected List<DataEntry> entryList;
    protected int resultCode;
    protected List<DataEntry> failedKeyList;

    public ResponsePrefixGetsPacket(Transcoder transcoder) {
        super(transcoder);
        this.pcode = TairConstant.TAIR_RESP_PREFIX_GETS_PACKET;
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

        //pkey
        DataEntry pkey = new DataEntry();
        pkey.decodeValue(byteBuffer, transcoder);
        
        //nsucc
        int    count   = byteBuffer.getInt();
        this.entryList = new ArrayList<DataEntry>(count);

		for (int i = 0; i < count; i++) {
			DataEntry de = new DataEntry();
			de.decodeKeyValue(byteBuffer, transcoder);
			int code = byteBuffer.getInt();
			de.setReturnCode(code);
			this.entryList.add(de);
		}
		//nfail
		int failcount = byteBuffer.getInt();
		if (failcount > 0) {
			failedKeyList = new ArrayList<DataEntry>(failcount);
			for (int i = 0; i < failcount; i++) {
				DataEntry de = new DataEntry();
				de.decodeKey(byteBuffer, transcoder);
				int code = byteBuffer.getInt();
				de.setReturnCode(code);
			}
		}

        return true;
    }

    public List<DataEntry> getEntryList() {
        return entryList;
    }

    public void setEntryList(List<DataEntry> entryList) {
        this.entryList = entryList;
    }

    public List<DataEntry> getFailedKeyList() {
		return failedKeyList;
	}

	public void setFailedKeyList(List<DataEntry> failedKeyList) {
		this.failedKeyList = failedKeyList;
	}

	/**
     * 
     * @return the configVersion
     */
    public int getConfigVersion() {
        return configVersion;
    }

    /**
     * 
     * @param configVersion the configVersion to set
     */
    public void setConfigVersion(int configVersion) {
        this.configVersion = configVersion;
    }

	public int getResultCode() {
		return resultCode;
	}
    
}
