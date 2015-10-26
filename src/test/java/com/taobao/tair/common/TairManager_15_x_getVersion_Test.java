package com.taobao.tair.common;

import junit.framework.Assert;

import org.junit.Test;

public class TairManager_15_x_getVersion_Test extends BaseTest {
	private int namespace = 1;
	
	@Test
	public void test_1501_getVersion_qa() {
		String result = tairManager.getVersion();
        Assert.assertEquals("TairClient 2.3.1", result);
	}
	
}
