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

public class TairManager_02_x_mget_Test extends BaseTest {
	private int namespace = 1;
	
	@Test
	public void test_201_data_not_exist_qa() {
		//data1 area equals key different
		String key1 = "20key_1";
		String value1 = "20data_1";
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.put(namespace, key1, value1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
        //data2 area different key equals
        String key = "20key_2";
        String value2 = "201_data_2";
        tairManager.delete(namespace+1, key);
        resultCode = tairManager.put(namespace+1, key, value2);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
        //data to test
        String value = "201_data_3";
        tairManager.delete(namespace, key);
        
        //verify get function
        List<Object> keys = new ArrayList<Object>();
		keys.add(key);
		Result<List<DataEntry>> results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(0, results.getValue().size());

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
	}
	@Test
	public void test_202_data_not_exist_namespace_equals_negative_qa() {
	
		int namespace = -1;
		String key = "202_key_1";
		tairManager.delete(namespace, key);
		List<Object> keys = new ArrayList<Object>();
		keys.add(key);
		Result<List<DataEntry>> result = tairManager.mget(namespace, keys);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, result.getRc());
	    assertEquals(null, result.getValue());	    
		
        //clear data
		tairManager.delete(namespace, key);
        
	}

	@Test
	public void test_203_data_not_exist_namespace_equals_0_qa() {
		
		int namespace = 0;
		String key = "203_key_1";
		tairManager.delete(namespace, key);
		List<Object> keys = new ArrayList<Object>();
		keys.add(key);
		Result<List<DataEntry>> result = tairManager.mget(namespace, keys);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
	    assertEquals(0, result.getValue().size());	   
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	
	@Test
	public void test_204_data_not_exist_namespace_equals_1023_qa() {
		
		int namespace = 1023;
		String key = "204_key_1";
		tairManager.delete(namespace, key);
		List<Object> keys = new ArrayList<Object>();
		keys.add(key);
		Result<List<DataEntry>> result = tairManager.mget(namespace, keys);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
	    assertEquals(0, result.getValue().size());	     
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_205_data_not_exist_namespace_equals_1024_qa() {
	
		int namespace = 1024;
		String key = "205_key_1";
		tairManager.delete(namespace, key);
		List<Object> keys = new ArrayList<Object>();
		keys.add(key);
		Result<List<DataEntry>> result = tairManager.mget(namespace, keys);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, result.getRc());
	    assertEquals(null, result.getValue());	 
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_205_2_data_not_exist_namespace_equals_65536_qa() {
	
		int namespace = 65536;
		String key = "205_key_1";
		tairManager.delete(namespace, key);
		List<Object> keys = new ArrayList<Object>();
		keys.add(key);
		Result<List<DataEntry>> result = tairManager.mget(namespace, keys);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, result.getRc());
	    assertEquals(null, result.getValue());	 
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_206_data_not_exist_key_equals_null_qa() {

		String key = null;
		try{
			List<Object> keys = new ArrayList<Object>();
			keys.add(key);
			Result<List<DataEntry>> result = tairManager.mget(namespace, keys);
			Assert.fail("should throw an IllegalArgumentException");
		}catch(IllegalArgumentException e){
			Assert.assertEquals("key,value can not be null", e.getMessage());
		}
        
	}
	@Test
	public void test_206_data_not_exist_key_equals_empty_string_qa() {

		String key = "";
		tairManager.delete(namespace, key);
		List<Object> keys = new ArrayList<Object>();
		keys.add(key);
		Result<List<DataEntry>> result = tairManager.mget(namespace, keys);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
	    assertEquals(0, result.getValue().size());	     
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	
	@Test
	public void test_207_data_exist_data_not_expired_all_data_exist_different_type_qa() {
		List<Object> keys = new ArrayList<Object>();
		String key1 = "207_key_1_用例";
		String value1 = "207_data_1_用例";
		tairManager.delete(namespace, key1);
		ResultCode resultCode = tairManager.put(namespace, key1, value1);
		Assert.assertTrue(resultCode.isSuccess());
		keys.add(key1);
		
		int key2 = 1;
		int value2 = 3;
		tairManager.delete(namespace, key2);
	    resultCode = tairManager.put(namespace, key2, value2);
	    Assert.assertTrue(resultCode.isSuccess());
	    keys.add(key2);
	    
	    ArrayList key3 = new ArrayList();
	    key3.add(key1);
	    ArrayList value3 = new ArrayList();
		value3.add(value1);
		tairManager.delete(namespace, key3);
	    resultCode = tairManager.put(namespace, key3, value3);
	    Assert.assertTrue(resultCode.isSuccess());
	    keys.add(key3);
		
	    Result<List<DataEntry>> result = tairManager.mget(namespace, keys);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    Assert.assertEquals(keys.size(), result.getValue().size());
	   	
	    //经开发确认，此处顺序不重要
	    for(int i=0;i<3;i++){
	    	if(((DataEntry) result.getValue().get(i)).getKey().equals(key1)){
	    		assertEquals(value1, ((DataEntry) result.getValue().get(i)).getValue());	    	
	    	    assertEquals(key1, ((DataEntry) result.getValue().get(i)).getKey());	    
	    	}else if(((DataEntry) result.getValue().get(i)).getKey().equals(key2)){
	    			 assertEquals(value2, ((DataEntry) result.getValue().get(i)).getValue());	    	
	    			 assertEquals(key2, ((DataEntry) result.getValue().get(i)).getKey());	    
	    			 assertEquals(1, ((DataEntry) result.getValue().get(i)).getVersion());
	    	}else if(((DataEntry) result.getValue().get(i)).getKey().equals(key3)){
	    		assertEquals(value3, ((DataEntry) result.getValue().get(i)).getValue());	    	
	    	    assertEquals(key3, ((DataEntry) result.getValue().get(i)).getKey());	    
	    	    assertEquals(1, ((DataEntry) result.getValue().get(i)).getVersion());
	    	}
	    }
	    
        //clear data
	    resultCode = tairManager.mdelete(namespace, keys);
	    Assert.assertTrue(resultCode.isSuccess());  
	}
	@Test
	public void test_208_data_exist_data_expired_different_types_qa() throws InterruptedException {
		List<Object> keys = new ArrayList<Object>();
		String key1 = "208_key_1_用例";
		String value1 = "208_data_1_用例";
		tairManager.delete(namespace, key1);
		ResultCode resultCode = tairManager.put(namespace, key1, value1,0,3);
		Assert.assertTrue(resultCode.isSuccess());
		keys.add(key1);
		
		int key2 = 1;
		int value2 = 3;
		tairManager.delete(namespace, key2);
	    resultCode = tairManager.put(namespace, key2, value2,0,3);
	    Assert.assertTrue(resultCode.isSuccess());
	    keys.add(key2);
	    
	    ArrayList key3 = new ArrayList();
	    key3.add(key1);
	    ArrayList value3 = new ArrayList();
		value3.add(value1);
		tairManager.delete(namespace, key3);
	    resultCode = tairManager.put(namespace, key3, value3,0,3);
	    Assert.assertTrue(resultCode.isSuccess());
	    keys.add(key3);
		
	    Result<List<DataEntry>> result = tairManager.mget(namespace, keys);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    Assert.assertEquals(keys.size(), result.getValue().size());
	    
	    Thread.sleep(4000);
	    result = tairManager.mget(namespace, keys);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATAEXPIRED, result.getRc());
	    Assert.assertEquals(0, result.getValue().size());
        //clear data
	    resultCode = tairManager.mdelete(namespace, keys);
	    Assert.assertTrue(resultCode.isSuccess());  
	}
	@Test
	public void test_209_data_exist_some_data_expired_different_types_qa() throws InterruptedException {
		List<Object> keys = new ArrayList<Object>();
		String key1 = "209_key_1";
		String value1 = "209_data_1";
		tairManager.delete(namespace, key1);
		ResultCode resultCode = tairManager.put(namespace, key1, value1,0,3);
		Assert.assertTrue(resultCode.isSuccess());
		keys.add(key1);
		
		int key2 = 1;
		int value2 = 3;
		tairManager.delete(namespace, key2);
	    resultCode = tairManager.put(namespace, key2, value2,0,7);
	    Assert.assertTrue(resultCode.isSuccess());
	    keys.add(key2);
	    
	    ArrayList key3 = new ArrayList();
	    key3.add(key1);
	    ArrayList value3 = new ArrayList();
		value3.add(value1);
		tairManager.delete(namespace, key3);
	    resultCode = tairManager.put(namespace, key3, value3,0,3);
	    Assert.assertTrue(resultCode.isSuccess());
	    keys.add(key3);
		
	    Result<List<DataEntry>> result = tairManager.mget(namespace, keys);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    Assert.assertEquals(keys.size(), result.getValue().size());
	    
	    Thread.sleep(4000);
	    result = tairManager.mget(namespace, keys);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.PARTSUCC, result.getRc());
	    Assert.assertEquals(1, result.getValue().size());
	    assertEquals(value2, ((DataEntry) result.getValue().get(0)).getValue());	    	
	    assertEquals(key2, ((DataEntry) result.getValue().get(0)).getKey());	    
	    assertEquals(1, ((DataEntry) result.getValue().get(0)).getVersion());
        //clear data
	    resultCode = tairManager.mdelete(namespace, keys);
	    Assert.assertTrue(resultCode.isSuccess());  
	}
	@Test
	public void test_210_data_exist_some_data_not_expired_different_types_qa() throws InterruptedException {
		List<Object> keys = new ArrayList<Object>();
		String key1 = "209_key_1";
		String value1 = "209_data_1";
		tairManager.delete(namespace, key1);
		ResultCode resultCode = tairManager.put(namespace, key1, value1,0,3);
		Assert.assertTrue(resultCode.isSuccess());
		keys.add(key1);
		
		int key2 = 1;
		int value2 = 3;
		tairManager.delete(namespace, key2);
	    resultCode = tairManager.put(namespace, key2, value2,0,7);
	    Assert.assertTrue(resultCode.isSuccess());
	    keys.add(key2);
	    
	    ArrayList key3 = new ArrayList();
	    key3.add(key1);
	    ArrayList value3 = new ArrayList();
		value3.add(value1);
		tairManager.delete(namespace, key3);
	    resultCode = tairManager.put(namespace, key3, value3,0,3);
	    Assert.assertTrue(resultCode.isSuccess());
	    keys.add(key3);
	    
	    String key4 = "notExist";
	    tairManager.delete(namespace, key3);
	    keys.add(key4);
	    
	    Thread.sleep(4000);
	    Result<List<DataEntry>> result = tairManager.mget(namespace, keys);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.PARTSUCC, result.getRc());
	    Assert.assertEquals(1, result.getValue().size());
	    assertEquals(value2, ((DataEntry) result.getValue().get(0)).getValue());	    	
	    assertEquals(key2, ((DataEntry) result.getValue().get(0)).getKey());	    
	    assertEquals(1, ((DataEntry) result.getValue().get(0)).getVersion());
        //clear data
	    resultCode = tairManager.mdelete(namespace, keys);
	    Assert.assertTrue(resultCode.isSuccess());  
	}
}
