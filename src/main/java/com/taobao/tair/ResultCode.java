/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair;

import com.taobao.tair.etc.TairConstant;

/**
 * resutlcode
 */
public class ResultCode {
	public static final ResultCode SUCCESS = new ResultCode(0, "success");
	public static final ResultCode DATANOTEXSITS = new ResultCode(1, "data not exist");
	public static final ResultCode PARTITEM = new ResultCode(2, "partly items");
	public static final ResultCode CONNERROR = new ResultCode(-1, "connection error or timeout");
	public static final ResultCode UNKNOW = new ResultCode(-2, "unkonw error");
	public static final ResultCode SERVERERROR = new ResultCode(-3999, "server error");	
	public static final ResultCode VERERROR = new ResultCode(-3997, "version error");
	public static final ResultCode TYPENOTMATCH = new ResultCode(-3996, "type not match");
	public static final ResultCode PLUGINERROR = new ResultCode(-3995, "plugin error");
	public static final ResultCode SERIALIZEERROR = new ResultCode(-3994, "serialize error");
	public static final ResultCode ITEMEMPRY = new ResultCode(-3993, "item empty");
	public static final ResultCode OUTOFRANGE = new ResultCode(-3992, "item out of range");
	public static final ResultCode ITEMSIZEERROR = new ResultCode(-3991, "item size error");
	public static final ResultCode SENDFAILED = new ResultCode(-3990, "send packet failed");
	public static final ResultCode TIMEOUT = new ResultCode(-3989, "timeout");
	public static final ResultCode DATAEXPIRED = new ResultCode(-3988, "data expired");
	public static final ResultCode SERVERCANTWORK = new ResultCode(-3987, "server can not work");
	public static final ResultCode WRITENOTONMASTER = new ResultCode(-3986, "write not on master");
	public static final ResultCode DUPLICATEBUSY = new ResultCode(-3985, "duplicate busy");
	public static final ResultCode MIGRATEBUSY = new ResultCode(-3984, "migrate busy");
	public static final ResultCode PARTSUCC = new ResultCode(-3983, "partly success");
	public static final ResultCode INVALIDARG = new ResultCode(-3982, "invalid argument");
	public static final ResultCode CANNT_OVERRIDE = new ResultCode(-3981, "can not override");
	public static final ResultCode NSUNALLOC = new ResultCode(-3977, "namespace unallocated");
	public static final ResultCode KEYTOLARGE = new ResultCode(-5, "key length error");
	public static final ResultCode VALUETOLARGE = new ResultCode(-6, "value length error");
	public static final ResultCode NSERROR = new ResultCode(-7, "namsepace range error, should between 0 ~ 1023");
	public static final ResultCode ITEMTOLARGE = new ResultCode(-8, "item length error, shoud less than " + TairConstant.TAIR_ITEM_MAX_SIZE);


	/**
	 * return the ResultCode object of the code.
	 * <p>
	 * If add new ResultCode, remember add case here. 
	 */
public static ResultCode valueOf(int code) {
		switch (code) {
		case 0:
			return SUCCESS;
		case 1:
			return DATANOTEXSITS;
		case 2:
			return PARTITEM;
		case -5:
			return KEYTOLARGE;
		case -6:
			return VALUETOLARGE;
		case -7:
			return NSERROR;
		case -10:
			return PARTSUCC;
		case -3999:
			return SERVERERROR;
		case -3998:
			return DATANOTEXSITS;
		case -3997:
			return VERERROR;
		case -3996:
			return TYPENOTMATCH;
		case -3995:
			return PLUGINERROR;
		case -3994:
			return SERIALIZEERROR;
		case -3993:
			return ITEMEMPRY;
		case -3992:
			return OUTOFRANGE;
		case -3991:
			return ITEMSIZEERROR;
		case -3990:
			return SENDFAILED;
		case -3989:
			return TIMEOUT;
		case -3988:
			return DATAEXPIRED;
		case -3987:
			return SERVERCANTWORK;
		case -3986:
			return WRITENOTONMASTER;
		case -3985:
			return DUPLICATEBUSY;
		case -3984:
			return MIGRATEBUSY;
		case -3983:
			return PARTSUCC;
		case -3982:
			return INVALIDARG;
		case -3981:
			return CANNT_OVERRIDE;
		case -3978:
			return PARTITEM;
		case -3977:
			return NSUNALLOC;
		default:
			return UNKNOW;
		}
	}
	
	private int code;
	private String message;

	public ResultCode(int code) {
		this.code = code;
	}

	public ResultCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

	/**
	 * @return the inner code of this object
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @return the description of this object
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * whether the request is success.
	 * <p>
	 * if the target is not exist, this method return true. 
	 */
	public boolean isSuccess() {
		return code >= 0;
	}

	@Override
	public String toString() {
		return "code=" + code + ", msg=" + message;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj != null && (obj instanceof ResultCode)) {
			ResultCode rc = (ResultCode)obj;
			return rc.getCode() == this.code;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.code;
	}
}
