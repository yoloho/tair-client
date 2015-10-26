package com.taobao.tair.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Test;

import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;

public class TairManager_01_x_get_Test extends BaseTest {
	private int namespace = 1;
	
	@Test
	public void test_101_data_not_exist_qa() {
		//data1 area equals key different
		String key1 = "101_key_1";
		String value1 = "101_data_1";
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.put(namespace, key1, value1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
        //data2 area different key equals
        String key = "101_key_2";
        String value2 = "101_data_2";
        tairManager.delete(namespace+1, key);
        resultCode = tairManager.put(namespace+1, key, value2);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
        //data to test
        String value = "101_data_3";
        tairManager.delete(namespace, key);
        
        //verify get function
        Result<DataEntry> result = tairManager.get(namespace, key);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
		assertEquals(null, ((DataEntry) result.getValue()));	

		//verfy othre datas are not changed
		result = tairManager.get(namespace, key1);
        assertTrue(result.isSuccess());
		assertEquals(value1, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key1, ((DataEntry) result.getValue()).getKey());
		result = tairManager.get(namespace+1, key);
        assertTrue(result.isSuccess());
		assertEquals(value2, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
        //clear data
		tairManager.delete(namespace, key1);
		tairManager.delete(namespace+1, key);
	}
	@Test
	public void test_102_data_not_exist_namespace_equals_negative_qa() {
	
		int namespace = -1;
		String key = "102_key_1";
		tairManager.delete(namespace, key);
		Result<DataEntry> result = tairManager.get(namespace, key);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, result.getRc());
	    assertEquals(null, ((DataEntry) result.getValue()));	    
		
        //clear data
		tairManager.delete(namespace, key);
        
	}

	@Test
	public void test_103_data_not_exist_namespace_equals_0_qa() {
		
		int namespace = 0;
		String key = "103_key_1";
		tairManager.delete(namespace, key);
		Result<DataEntry> result = tairManager.get(namespace, key);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
	    assertEquals(null, ((DataEntry) result.getValue()));	    
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	
	@Test
	public void test_104_data_not_exist_namespace_equals_1023_qa() {
		
		int namespace = 1023;
		String key = "104_key_1";
		tairManager.delete(namespace, key);
		Result<DataEntry> result = tairManager.get(namespace, key);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
	    assertEquals(null, ((DataEntry) result.getValue()));	    
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_105_data_not_exist_namespace_equals_1024_qa() {
	
		int namespace = 1024;
		String key = "105_key_1";
		tairManager.delete(namespace, key);
		Result<DataEntry> result = tairManager.get(namespace, key);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, result.getRc());
	    assertEquals(null, ((DataEntry) result.getValue()));	    
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_105_2_data_not_exist_namespace_equals_65536_qa() {
	
		int namespace = 65536;
		String key = "105_key_1";
		tairManager.delete(namespace, key);
		Result<DataEntry> result = tairManager.get(namespace, key);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, result.getRc());
	    assertEquals(null, ((DataEntry) result.getValue()));	    
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_106_data_not_exist_key_equals_null_qa() {

		String key = null;
		try{
			Result<DataEntry> result = tairManager.get(namespace, key);
			Assert.fail("should throw an IllegalArgumentException");
		}catch(IllegalArgumentException e){
			Assert.assertEquals("key,value can not be null", e.getMessage());
		}
        
	}
	@Test
	public void test_106_1_data_not_exist_key_equals_empty_string_qa() {

		String key = "";
		tairManager.delete(namespace, key);
		Result<DataEntry> result = tairManager.get(namespace, key);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
	    assertEquals(null, ((DataEntry) result.getValue()));	    
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	
	@Test
	public void test_107_data_exist_data_not_expired_namespace_equals_1023_qa() {
		int namespace = 1023;
		String key = "107_key_1_ÓÃÀý";
		String value = "107_data_1_ÓÃÀý";
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.put(namespace, key, value, 0, 3);
		Assert.assertTrue(resultCode.isSuccess());
		Result<DataEntry> result = tairManager.get(namespace, key);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    assertEquals(value, ((DataEntry) result.getValue()).getValue());	    
	    assertEquals(key, ((DataEntry) result.getValue()).getKey());	    
	    assertEquals(1, ((DataEntry) result.getValue()).getVersion());	    
        //clear data
		tairManager.delete(namespace, key);
        
	}
	
	@Test
	public void test_108_data_exist_data_expired_namespace_equals_1023_qa() throws InterruptedException {
		int namespace = 1023;
		String key = "108_key_1";
		String value = "108_data_1";
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.put(namespace, key, value, 0, 1);
		Assert.assertTrue(resultCode.isSuccess());
		Thread.sleep(2000);
		Result<DataEntry> result = tairManager.get(namespace, key);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATAEXPIRED, result.getRc());
	    	    
        //clear data
		tairManager.delete(namespace, key);
        
	}
}
