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
    public static final int TAIR_REQ_INVALID_PACKET = 12;
    public static final int TAIR_REQ_INCDEC_PACKET = 11;
    public static final int TAIR_REQ_GET_RANGE_PACKET = 18;

    // response
    public static final int TAIR_RESP_RETURN_PACKET = 101;
    public static final int TAIR_RESP_GET_PACKET    = 102;
    public static final int TAIR_RESP_INCDEC_PACKET    = 105;

    // config server
    public static final int TAIR_REQ_GET_GROUP_NEW_PACKET  = 1002;
    public static final int TAIR_RESP_GET_GROUP_NEW_PACKET = 1102;
    public static final int TAIR_REQ_QUERY_INFO_PACKET = 1009;
    public static final int TAIR_RESP_QUERY_INFO_PACKET = 1106;
    
    // items
    public static final int TAIR_REQ_ADDITEMS_PACKET = 1400;
    public static final int TAIR_REQ_GETITEMS_PACKET = 1401;
    public static final int TAIR_REQ_REMOVEITEMS_PACKET = 1402;
    public static final int TAIR_REQ_GETANDREMOVEITEMS_PACKET = 1403;
    public static final int TAIR_REQ_GETITEMSCOUNT_PACKET = 1404;
    public static final int TAIR_RESP_GETITEMS_PACKET = 1405;
    
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
   
       
}
