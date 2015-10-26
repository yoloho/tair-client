package com.taobao.tair.common;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;

import com.taobao.tair.TairManager;
import com.taobao.tair.impl.DefaultTairManager;

public class BaseTest {

	 protected TairManager tairManager = null;

	    @Before
	    public void setUp() throws Exception {
	    	DefaultTairManager tm = new DefaultTairManager();
	    	
	    	List<String> cs = new ArrayList<String>();
	    	cs.add("10.232.4.20:5198");
	    	cs.add("10.232.4.21:5198");	
	    	tm.setConfigServerList(cs);
	    	tm.setGroupName("group_1");
	    	tm.setTimeout(80000);
	    	tm.setCompressionThreshold(1100000);//…Ë÷√—πÀıœﬁ÷∆
	    	tm.setCharset("UTF-8");
	    	tm.init();
	    	
	    	tairManager = tm;
	    }
}
