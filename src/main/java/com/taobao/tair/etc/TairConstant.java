/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.etc;

public class TairConstant {
    // packet flag
    public static final int TAIR_PACKET_FLAG = 0x6d426454;
    
    // packet code
    // request
    public static final int TAIR_REQ_PUT_PACKET    = 1;
    public static final int TAIR_REQ_GET_PACKET    = 2;
    public static final int TAIR_REQ_REMOVE_PACKET = 3;
    public static final int TAIR_REQ_REMOVE_AREA   = 4;
    public static final int TAIR_REQ_PING_PACKET = 6;
    public static final int TAIR_REQ_INCDEC_PACKET = 11;
    public static final int TAIR_REQ_LOCK_PACKET = 14;
    public static final int TAIR_REQ_MPUT_PACKET = 15;
    public static final int TAIR_REQ_OP_CMD_PACKET   = 16;
    public static final int TAIR_RESP_OP_CMD_PACKET   = 17;
    public static final int TAIR_REQ_GET_RANGE_PACKET = 18;
    public static final int TAIR_RESP_GET_RANGE_PACKET = 19;
    
    public static final int TAIR_REQ_HIDE_PACKET = 20;
    public static final int TAIR_REQ_HIDE_BY_PROXY_PACKET = 21;
    public static final int TAIR_REQ_GET_HIDDEN_PACKET = 22;
    public static final int TAIR_REQ_INVALID_PACKET = 23;
    public static final int TAIR_REQ_PREFIX_PUTS_PACKET = 24;
    public static final int TAIR_REQ_PREFIX_REMOVES_PACKET = 25;
    public static final int TAIR_REQ_PREFIX_INCDEC_PACKET = 26;
    public static final int TAIR_RESP_PREFIX_INCDEC_PACKET = 27;
    public static final int TAIR_RESP_MRETURN_PACKET = 28;
    public static final int TAIR_REQ_PREFIX_GETS_PACKET = 29;
    public static final int TAIR_RESP_PREFIX_GETS_PACKET = 30;
    public static final int TAIR_REQ_PREFIX_HIDES_PACKET = 31;
    public static final int TAIR_REQ_PREFIX_INVALIDS_PACKET = 32;
    public static final int TAIR_REQ_PREFIX_HIDES_BY_PROXY_PACKET = 33;
    public static final int TAIR_REQ_PREFIX_GET_HIDDENS_PACKET = 34;

    public static final int TAIR_REQ_SIMPLE_GET_PACKET = 36;
    public static final int TAIR_RESP_SIMPLE_GET_PACKET = 37;

    // response
    public static final int TAIR_RESP_RETURN_PACKET = 101;
    public static final int TAIR_RESP_GET_PACKET    = 102;
    public static final int TAIR_RESP_INCDEC_PACKET    = 105;

    // config server
    public static final int TAIR_REQ_GET_GROUP_NEW_PACKET  = 1002;
    public static final int TAIR_RESP_GET_GROUP_NEW_PACKET = 1102;
    public static final int TAIR_REQ_QUERY_INFO_PACKET = 1009;
    public static final int TAIR_RESP_QUERY_INFO_PACKET = 1106;
    
    public static final int TAIR_RESP_FEEDBACK_PACKET = 1113;
    
    // items
    public static final int TAIR_REQ_ADDITEMS_PACKET = 1400;
    public static final int TAIR_REQ_GETITEMS_PACKET = 1401;
    public static final int TAIR_REQ_REMOVEITEMS_PACKET = 1402;
    public static final int TAIR_REQ_GETANDREMOVEITEMS_PACKET = 1403;
    public static final int TAIR_REQ_GETITEMSCOUNT_PACKET = 1404;
    public static final int TAIR_RESP_GETITEMS_PACKET = 1405;
    
    public static final int TAIR_REQ_GET_EXPIRE_PACKET = 1600;
    public static final int TAIR_RESP_GET_EXPIRE_EACKET = 1601;
    public static final int TAIR_REQ_PUT_MODIFY_DATE_PACKET = 1700;
    public static final int TAIR_REQ_GET_MODIFY_DATE_PACKET = 1702;
    public static final int TAIR_RESP_GET_MODIFY_DATE_PACKET = 1703;

    public static final int TAIR_REQ_INC_DEC_BOUNDED_PACKET = 1704;
    public static final int TAIR_RESP_INC_DEC_BOUNDED_PACKET = 1705;
    
    public static final int TAIR_REQ_PREFIX_INCDEC_BOUNDED_PACKET = 1706;
    public static final int TAIR_RESP_PREFIX_INCDEC_BOUNDED_PACKET = 1707;
    
    // serialize type
    public static final int TAIR_STYPE_INT = 1;
    public static final int TAIR_STYPE_STRING = 2;
    public static final int TAIR_STYPE_BOOL = 3;
    public static final int TAIR_STYPE_LONG = 4;
    public static final int TAIR_STYPE_DATE = 5;
    public static final int TAIR_STYPE_BYTE = 6;
    public static final int TAIR_STYPE_FLOAT = 7;
    public static final int TAIR_STYPE_DOUBLE = 8;
    public static final int TAIR_STYPE_BYTEARRAY = 9;
    public static final int TAIR_STYPE_SERIALIZE = 10;
    public static final int TAIR_STYPE_INCDATA = 11;
    
    public static final int TAIR_PACKET_HEADER_SIZE = 16;
    public static final int TAIR_PACKET_HEADER_BLPOS = 12;
    
    // buffer size
    public static final int INOUT_BUFFER_SIZE = 8192;
    public static final int DEFAULT_TIMEOUT = 2000;
    public static final int DEFAULT_WAIT_THREAD = 100;
    
    // etc
    public static final int TAIR_DEFAULT_COMPRESSION_THRESHOLD = 8192;
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final int TAIR_KEY_MAX_LENTH = 1024; // 1KB
    public static final int TAIR_VALUE_MAX_LENGTH =1000000;
    public static final int TAIR_ITEM_MAX_SIZE = 32 * 1024; // 32KB
    
    public static final int TAIR_MAX_COUNT = 1024;
    public static final int TAIR_MALLOC_MAX = 1 << 20; // 1MB
    
    public static final int NAMESPACE_MAX = 1023;
    
    public static final int ITEM_ALL = 10000001;
    
    public static final String INVALUD_SERVERLIST_KEY = "invalidate_server";
    
    public static final int Q_AREA_CAPACITY  = 1;
    public static final int Q_MIG_INFO  = 2;
    public static final int Q_DATA_SEVER_INFO  = 3;
    public static final int Q_GROUP_INFO  = 4;
    public static final int Q_STAT_INFO  = 5;
    
    public static final short TAIR_GET_RANGE_ALL = 1;
    public static final short TAIR_GET_RANGE_ONLY_VALUE = 2;
    public static final short TAIR_GET_RANGE_ONLY_KEY = 3;
    
    public static final short TAIR_LOCK_NONE = 0;
    public static final short TAIR_LOCK_STATUS = 1;
    public static final short TAIR_LOCK_VALUE = 2;
    public static final short TAIR_LOCK_VALUE_UNLOCK = 3;
    public static final short TAIR_LOCK_PUT_AND_LOCK_VALUE = 4;
       
}
