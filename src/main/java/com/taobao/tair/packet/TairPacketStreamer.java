/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.packet;

import java.nio.ByteBuffer;

import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;

public class TairPacketStreamer implements PacketStreamer {
    private Transcoder transcoder = null;

    public TairPacketStreamer(Transcoder transcoder) {
        this.transcoder = transcoder;
    }

	public BasePacket decodePacket(int pcode, byte[] data) {
		BasePacket packet = createPacket(pcode);
		
		if (packet != null) {
			packet.setLen(data.length);
			packet.setByteBuffer(ByteBuffer.wrap(data));
		}
		return packet;
	}

    private BasePacket createPacket(int pcode) {
        BasePacket packet = null;

        switch (pcode) {
            case TairConstant.TAIR_RESP_RETURN_PACKET:
                packet = new ReturnPacket(transcoder);
                break;

            case TairConstant.TAIR_RESP_GET_PACKET:
                packet = new ResponseGetPacket(transcoder);
                break;
                
            case TairConstant.TAIR_RESP_GET_RANGE_PACKET:
            	packet = new ResponseGetRangePacket(transcoder);
            	break;

            case TairConstant.TAIR_RESP_INCDEC_PACKET:
                packet = new ResponseIncDecPacket(transcoder);
                break;
            case TairConstant.TAIR_RESP_GET_GROUP_NEW_PACKET:
            	packet = new ResponseGetGroupPacket(null);
            	break;
            case TairConstant.TAIR_RESP_GETITEMS_PACKET:
            	packet = new ResponseGetItemsPacket(transcoder);
            	break;
            case TairConstant.TAIR_RESP_QUERY_INFO_PACKET:
            	packet = new ResponseQueryInfoPacket(transcoder);
            	break;
            case TairConstant.TAIR_RESP_MRETURN_PACKET:
            	packet = new MultiReturnPacket(transcoder);
            	break;
            case TairConstant.TAIR_RESP_PREFIX_GETS_PACKET:
            	packet = new ResponsePrefixGetsPacket(transcoder);
            	break;

            default:
            	throw new IllegalArgumentException("unkonw return packet, pcode: " + pcode);
        }

        if ((packet != null) && (packet.getPcode() != pcode)) {
            packet = null;
        }

        return packet;
    }


}
