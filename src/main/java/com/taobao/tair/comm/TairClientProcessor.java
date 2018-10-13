/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.comm;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;

public class TairClientProcessor extends IoHandlerAdapter{

	private static final Log LOGGER = LogFactory.getLog(TairClientProcessor.class);
	
	private TairClient client=null;
	
	private TairClientFactory factory=null;
	
	private String key=null;
	
	public void setClient(TairClient client){
		this.client=client;
	}
	
	public void setFactory(TairClientFactory factory,String targetUrl){
		this.factory=factory;
		key=targetUrl;
	}
	
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		TairResponse response=(TairResponse)message;
		Integer requestId=response.getRequestId();
		if(client.isCallbackTask(requestId)){
			client.putCallbackResponse(requestId, response.getResponse());
		}
		else{
			client.putResponse(requestId, response.getResponse());
		}
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		if (LOGGER.isWarnEnabled())
			LOGGER.warn("connection exception occured", cause);
		
		if(!(cause instanceof IOException)){
			session.close();
		}
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
	    //这里为了处理超时回收场景中主动关闭的情况
	    Object val = session.getAttribute("removed");
	    if (val == null) {
    		factory.removeClient(key);
	    }
	}
	
}
