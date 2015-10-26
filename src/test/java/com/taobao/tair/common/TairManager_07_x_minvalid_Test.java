package com.taobao.tair.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;

public class TairManager_07_x_minvalid_Test extends BaseTest {
	private int namespace = 1;
	@Test
	public void test_501_data_not_exist_qa() {
		//data1 area equals key different
		String key1 = "501_key_1";
		String value1 = "501_data_1";
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.put(namespace, key1, value1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
        //data2 area different key equals
        String key = "501_key_2";
        String value2 = "50data_2";
        tairManager.delete(namespace+1, key);
        resultCode = tairManager.put(namespace+1, key, value2);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
        //data to test no this data
        List keys = new ArrayList();
        keys.add(key);
        tairManager.minvalid(namespace, keys);
        resultCode = tairManager.delete(namespace, key);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,resultCode);
       
		//verfy othre datas are not changed
        Result<DataEntry> result = tairManager.get(namespace, key1);
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
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_502_data_not_exist_namespace_equals_negative_qa() {
	
		int namespace = -1;
		String key = "502_key_1";
		String value = "502_data_1";
		tairManager.delete(namespace, key);
		List keys = new ArrayList();
	    keys.add(key);
		ResultCode resultCode = tairManager.minvalid(namespace, keys);
	    Assert.assertFalse(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, resultCode);    

        //clear data
		tairManager.delete(namespace, key);
        
	}
	//TODO 目前无论数据是否存在都返回删除成功 
	@Test
	public void test_503_data_not_exist_namespace_equals_0_qa() {
		
		int namespace = 0;
		String key = "503_key_1";
		String value = "503_data_1";
		List keys = new ArrayList();
	    keys.add(key);
	    
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.minvalid(namespace, keys);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);    
	    
        //clear data
		tairManager.delete(namespace, key);
        
	}
	
	@Test
	public void test_504_data_not_exist_namespace_equals_1023_qa() {
		
		int namespace = 1023;
		String key = "504_key_1";
		String value = "504_data_1";
		List keys = new ArrayList();
	    keys.add(key);
	    
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.minvalid(namespace, keys);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);       
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_505_data_not_exist_namespace_equals_1024_qa() {
	
		int namespace = 1024;
		String key = "505_key_1";
		String value = "505_data_1";
		List keys = new ArrayList();
	    keys.add(key);
	    
		tairManager.delete(namespace, key);
		ResultCode result = tairManager.minvalid(namespace, keys);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, result);       
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_505_2_data_not_exist_namespace_equals_65536_qa() {
	
		int namespace = 65536;
		String key = "505_key_1";
		String value = "505_data_1";
		List keys = new ArrayList();
	    keys.add(key);
	    
		tairManager.delete(namespace, key);
		ResultCode result = tairManager.minvalid(namespace, keys);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, result);       
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_506_data_not_exist_key_equals_null_qa() {

		String key = null;
		String value = "506_data_1";
		List keys = new ArrayList();
	    keys.add(key);
		try{
			ResultCode resultCode = tairManager.minvalid(namespace, keys);
			Assert.fail("shoule throw an IllegalArgumentException");
		}catch(Exception e){
			Assert.assertEquals("key,value can not be null", e.getMessage());
		}
			
	}
	@Test
	public void test_506_1_data_not_exist_key_equals_empty_string_qa() {

		String key = "";
		String value = "506_data_1";
		List keys = new ArrayList();
	    keys.add(key);
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.minvalid(namespace, keys);
	    Assert.assertTrue(resultCode.isSuccess());  
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode); 
	    
	    //clear data
		tairManager.delete(namespace, key);
	}
	
	@Test
	public void test_507_data_exist_old_date_version_not_equals_0_qa(){
		List keys = new ArrayList();
	    
		String key1 = "507_key_1";
		String value1 = "507_data_1";
		tairManager.delete(namespace, key1);
		ResultCode resultCode = tairManager.put(namespace, key1, value1,2);
	    Assert.assertTrue(resultCode.isSuccess()); 
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    keys.add(key1);
	    Result<DataEntry> result = tairManager.get(namespace, key1);
	    assertTrue(result.isSuccess());
	    assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(value1, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key1, ((DataEntry) result.getValue()).getKey());
		
	    int key2 = 2;
	    int value2 = 2;
	    tairManager.delete(namespace, key2);
		resultCode = tairManager.put(namespace, key2, value2,2);
	    Assert.assertTrue(resultCode.isSuccess()); 
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    keys.add(key2);
	    result = tairManager.get(namespace, key2);
	    assertTrue(result.isSuccess());
	    assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(value2, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key2, ((DataEntry) result.getValue()).getKey());
	    
		resultCode = tairManager.minvalid(namespace, keys);
		Assert.assertTrue(resultCode.isSuccess()); 
		Assert.assertEquals(ResultCode.SUCCESS, resultCode);
		
		 Result<List<DataEntry>> resultList = tairManager.mget(namespace, keys);
		assertTrue(resultList.isSuccess());
		assertEquals(ResultCode.DATANOTEXSITS, resultList.getRc());
		Assert.assertEquals(0, resultList.getValue().size());
		
	    //clear data
		tairManager.mdelete(namespace, keys);
	}
	@Test
	public void test_508_data_exist_data_expired_qa() throws InterruptedException{
		List keys = new ArrayList();
		
		String key1 = "508_key_1";
		String value1 = "508_data_1";
		tairManager.delete(namespace, key1);
		ResultCode resultCode = tairManager.put(namespace, key1, value1,2,1);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    keys.add(key1);
	    
	    String key2 = "508_key_2";
		String value2 = "508_data_2";
		tairManager.delete(namespace, key2);
		resultCode = tairManager.put(namespace, key2, value2,2,1);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    keys.add(key2);
	    
	    Thread.sleep(2000);
	    Result<List<DataEntry>> resultList = tairManager.mget(namespace, keys);
	    Assert.assertFalse(resultList.isSuccess());
	    Assert.assertEquals(ResultCode.DATAEXPIRED, resultList.getRc());
	    
		resultCode = tairManager.minvalid(namespace, keys);
		assertTrue(resultCode.isSuccess());
		Assert.assertEquals(ResultCode.SUCCESS, resultCode);
		
		resultList = tairManager.mget(namespace, keys);
	    Assert.assertTrue(resultList.isSuccess());
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, resultList.getRc());
	
		tairManager.mdelete(namespace, keys);
	}
	@Test
	public void test_509_data_exist_some_data_not_exist_and_some_data_expired_qa() throws InterruptedException{
		List keys = new ArrayList();
		
		String key1 = "509_key_1";
		String value1 = "509_data_1";
		tairManager.delete(namespace, key1);
		ResultCode resultCode = tairManager.put(namespace, key1, value1,2,1);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    keys.add(key1);
	    
	    String key2 = "509_key_2";
		String value2 = "509_data_2";
		tairManager.delete(namespace, key2);
		resultCode = tairManager.put(namespace, key2, value2,2,5);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    keys.add(key2);
	    
	    String key3 = "509_key_3";
	    tairManager.delete(namespace, key3);
	    keys.add(key3);
	    
	    Thread.sleep(2000);
	    Result<List<DataEntry>> resultList = tairManager.mget(namespace, keys);
	    Assert.assertFalse(resultList.isSuccess());
	    Assert.assertEquals(ResultCode.PARTSUCC, resultList.getRc());
	    
		resultCode = tairManager.minvalid(namespace, keys);
		assertTrue(resultCode.isSuccess());
		Assert.assertEquals(ResultCode.SUCCESS, resultCode);
		
		resultList = tairManager.mget(namespace, keys);
	    Assert.assertTrue(resultList.isSuccess());
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, resultList.getRc());
	
		tairManager.mdelete(namespace, keys);
	}
	
}
