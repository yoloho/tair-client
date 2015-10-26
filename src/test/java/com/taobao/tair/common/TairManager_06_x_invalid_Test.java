package com.taobao.tair.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Test;

import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;

public class TairManager_06_x_invalid_Test extends BaseTest {
	private int namespace = 1;
	@Test
	public void test_401_data_not_exist_qa() {
		//data1 area equals key different
		String key1 = "401_key_1";
		String value1 = "401_data_1";
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.put(namespace, key1, value1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
        //data2 area different key equals
        String key = "401_key_2";
        String value2 = "40data_2";
        tairManager.delete(namespace+1, key);
        resultCode = tairManager.put(namespace+1, key, value2);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
        //data to test no this data
        tairManager.delete(namespace, key);
        resultCode = tairManager.invalid(namespace, key);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,resultCode);
        
        //verify get function
        Result<DataEntry> result = tairManager.get(namespace, key);
        assertTrue(result.isSuccess());
		

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
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_402_data_not_exist_namespace_equals_negative_qa() {
	
		int namespace = -1;
		String key = "402_key_1";
		String value = "402_data_1";
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.invalid(namespace, key);
	    Assert.assertFalse(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, resultCode);    

        //clear data
		tairManager.delete(namespace, key);
        
	}

	@Test
	public void test_403_data_not_exist_namespace_equals_0_qa() {
		
		int namespace = 0;
		String key = "403_key_1";
		String value = "403_data_1";
		
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.invalid(namespace, key);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, resultCode);    
	    
        //clear data
		tairManager.delete(namespace, key);
        
	}
	
	@Test
	public void test_404_data_not_exist_namespace_equals_1023_qa() {
		
		int namespace = 1023;
		String key = "404_key_1";
		String value = "404_data_1";
		
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.invalid(namespace, key);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, resultCode);       
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_405_data_not_exist_namespace_equals_1024_qa() {
	
		int namespace = 1024;
		String key = "405_key_1";
		String value = "405_data_1";
		tairManager.delete(namespace, key);
		ResultCode result = tairManager.invalid(namespace, key);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, result);       
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_405_data_not_exist_namespace_equals_65536_qa() {
	
		int namespace = 65536;
		String key = "405_key_1";
		String value = "405_data_1";
		tairManager.delete(namespace, key);
		ResultCode result = tairManager.invalid(namespace, key);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, result);       
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_406_data_not_exist_key_equals_null_qa() {

		String key = null;
		String value = "406_data_1";
		try{
			ResultCode resultCode = tairManager.invalid(namespace, key);
			Assert.fail("shoule throw an IllegalArgumentException");
		}catch(Exception e){
			Assert.assertEquals("key,value can not be null", e.getMessage());
		}
			
	}
	@Test
	public void test_406_1_data_not_exist_key_equals_empty_string_qa() {

		String key = "";
		String value = "406_data_1";
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.invalid(namespace, key);
	    Assert.assertTrue(resultCode.isSuccess());  
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, resultCode); 
	    
	    //clear data
		tairManager.delete(namespace, key);
	}
	
	@Test
	public void test_407_data_exist_old_date_version_not_equals_0_qa(){
		String key = "407_key_1";
		String value = "407_data_1";
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.put(namespace, key, value,2);
	    Assert.assertTrue(resultCode.isSuccess()); 
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    
	    Result<DataEntry> result = tairManager.get(namespace, key);
	    assertTrue(result.isSuccess());
	    assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
		resultCode = tairManager.invalid(namespace, key);
		Assert.assertTrue(resultCode.isSuccess()); 
		Assert.assertEquals(ResultCode.SUCCESS, resultCode);
		
		result = tairManager.get(namespace, key);
		assertTrue(result.isSuccess());
		assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
		
	    //clear data
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_408_data_exist_data_expired_qa() throws InterruptedException{
		String key = "408_key_1";
		String value = "408_data_1";
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.put(namespace, key, value,2,1);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    
	    Thread.sleep(2000);
	    Result<DataEntry> result = tairManager.get(namespace, key);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATAEXPIRED, result.getRc());
	    Assert.assertEquals(null, result.getValue());
	    
		resultCode = tairManager.invalid(namespace, key);
		assertTrue(resultCode.isSuccess());
		Assert.assertEquals(ResultCode.DATANOTEXSITS, resultCode);
	
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_410_key_size_test_data_not_exist_key_less_than_1k_qa() throws InterruptedException{
		byte[] key = new byte[1020];
		for(int i=0;i<1020;i++){
			key[i]=(byte)i;
		}
		String value = "409_data_1";
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.put(namespace, key, value);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    
	    resultCode = tairManager.invalid(namespace, key);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    
	    Result<DataEntry> result = tairManager.get(namespace, key);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
		    
		tairManager.delete(namespace, key);
	}
	
	@Test
	public void test_413_data_size_test_data_not_exist_data_less_than_one_million_bytes_qa() throws InterruptedException{
		String key = "409_key_1";
		int[] value = new int[249990];//999960×Ö½Ú
	
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.put(namespace, key, value);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
		    
	    resultCode = tairManager.invalid(namespace, key);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	  

	    Result<DataEntry> result = tairManager.get(namespace, key);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
	    
		tairManager.delete(namespace, key);
	}
	
}
