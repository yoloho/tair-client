package com.taobao.tair.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Test;

import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;

public class TairManager_09_x_decr_Test extends BaseTest {
	private int namespace = 1;
	@Test
	public void test_901_data_not_exist_qa() {
		//data1 area equals key different
		String key1 = "901_key_1";
		int value1 = 1;
		tairManager.delete(namespace, key1);
		Result<Integer> result = tairManager.decr(namespace, key1, value1, 1, 5);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(0,result.getValue().intValue());
        
        //data2 area different key equals
        String key = "901_key_2_ÓÃÀý";
        int value2 = 2;
        tairManager.delete(namespace+1, key);
        result = tairManager.decr(namespace+1, key, value2,3,5);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(1,result.getValue().intValue());
        
        //data to test
        int value = 3;
        tairManager.delete(namespace, key);
        result = tairManager.decr(namespace, key,value,0,5);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(-3,result.getValue().intValue());
        Result<DataEntry> data = tairManager.get(namespace, key);
        assertTrue(data.isSuccess());
		assertEquals(-3, ((DataEntry) data.getValue()).getValue());
		assertEquals(1, ((DataEntry) data.getValue()).getVersion());
		assertEquals(key, ((DataEntry) data.getValue()).getKey());
        
		//verfy othre datas are not changed
		data= tairManager.get(namespace, key1);
        assertTrue(result.isSuccess());
		assertEquals(0, ((DataEntry) data.getValue()).getValue());
		assertEquals(1, ((DataEntry) data.getValue()).getVersion());
		assertEquals(key1, ((DataEntry) data.getValue()).getKey());
		data = tairManager.get(namespace+1, key);
        assertTrue(data.isSuccess());
		assertEquals(1, ((DataEntry) data.getValue()).getValue());
		assertEquals(1, ((DataEntry) data.getValue()).getVersion());
		assertEquals(key, ((DataEntry) data.getValue()).getKey());
		
        //clear data
		tairManager.delete(namespace, key1);
		tairManager.delete(namespace+1, key);
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_902_data_not_exist_namespace_equals_negative_qa() {
	
		int namespace = -1;
		String key = "902_key_1";
		int value = 1;
		tairManager.delete(namespace, key);
		Result<Integer> result = tairManager.decr(namespace, key, value, 2, 2);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, result.getRc());
	    Assert.assertEquals(null, result.getValue());

        //clear data
		tairManager.delete(namespace, key);
        
	}
	
	@Test
	public void test_903_data_not_exist_namespace_equals_0_qa() {
		
		int namespace = 0;
		String key = "903_key_1";
		int value = 3;

		tairManager.delete(namespace, key);
		Result<Integer> result = tairManager.decr(namespace, key, value, 10, 5);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());    
	    Assert.assertEquals(7, result.getValue().intValue());
	    
	    Result<DataEntry> data = tairManager.get(namespace, key);
        assertTrue(data.isSuccess());
		assertEquals(7, ((DataEntry) data.getValue()).getValue());
		assertEquals(1, ((DataEntry) data.getValue()).getVersion());
		assertEquals(key, ((DataEntry) data.getValue()).getKey());
	    
        //clear data
		tairManager.delete(namespace, key);
        
	}
	
	@Test
	public void test_904_data_not_exist_namespace_equals_1023_qa() {
		
		int namespace = 1023;
		String key = "904_key_1";
		int value = 4;
	    
		tairManager.delete(namespace, key);
		Result<Integer> result = tairManager.decr(namespace, key, value, -1, 5);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());   
	    Assert.assertEquals(-5, result.getValue().intValue());
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_905_data_not_exist_namespace_equals_1024_qa() {
	
		int namespace = 1024;
		String key = "905_key_1";
		int value = 0;
	    
		tairManager.delete(namespace, key);
		Result<Integer> result = tairManager.decr(namespace, key, value, 0, 6);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, result.getRc());   
	    Assert.assertEquals(null, result.getValue());
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_905_2_data_not_exist_namespace_equals_65536_qa() {
	
		int namespace = 65536;
		String key = "905_key_1";
		int value = 0;
	    
		tairManager.delete(namespace, key);
		Result<Integer> result = tairManager.decr(namespace, key, value, 0, 6);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, result.getRc());   
	    Assert.assertEquals(null, result.getValue());
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_906_data_not_exist_key_equals_null_qa() {

		String key = null;
		int value = 6;
		
		try{
			Result<Integer> result = tairManager.decr(namespace, key, value, 5, 5);
			Assert.fail("shoule throw an IllegalArgumentException");
		}catch(Exception e){
			Assert.assertEquals("key,value can not be null", e.getMessage());
		}
			
	}
	@Test
	public void test_906_1_data_not_exist_key_equals_empty_string_qa() {

		String key = "";
		int value = Integer.MAX_VALUE;

		tairManager.delete(namespace, key);
		Result<Integer> result = tairManager.decr(namespace, key, value, 0, 5);
	    Assert.assertTrue(result.isSuccess());  
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc()); 
	    Assert.assertEquals(Integer.MIN_VALUE+1, result.getValue().intValue());
	    
	    //clear data
		tairManager.delete(namespace, key);
	}
	
	@Test
	public void test_907_data_not_exist_count_bigger_than_0_initValue_bigger_than_0_qa(){
	    
		String key = "907_key_1";
		int value = 1;
		tairManager.delete(namespace, key);
		Result<Integer> resultCode = tairManager.decr(namespace, key, value, Integer.MAX_VALUE, 5);
	    Assert.assertTrue(resultCode.isSuccess()); 
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode.getRc());
	    Assert.assertEquals(Integer.MAX_VALUE-value, resultCode.getValue().intValue());
	    
	    Result<DataEntry> result = tairManager.get(namespace, key);
	    assertTrue(result.isSuccess());
	    assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(Integer.MAX_VALUE-value, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
	    //clear data
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_908_data_not_exist_count_bigger_than_0_initValue_less_than_0_qa(){
	    
		String key = "908_key_1";
		int value = 1;
		tairManager.delete(namespace, key);
		Result<Integer> resultCode = tairManager.decr(namespace, key, value, -2, 6);
	    Assert.assertTrue(resultCode.isSuccess()); 
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode.getRc());
	    Assert.assertEquals(-3, resultCode.getValue().intValue());
	    
	    Result<DataEntry> result = tairManager.get(namespace, key);
	    assertTrue(result.isSuccess());
	    assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(-3, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
	    //clear data
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_909_data_not_exist_count_equals_0_initValue_equals_0_qa(){
	    
		String key = "909_key_1";
		int value = 0;
		tairManager.delete(namespace, key);
		Result<Integer> resultCode = tairManager.decr(namespace, key, value, 0, 6);
	    Assert.assertTrue(resultCode.isSuccess()); 
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode.getRc());
	    Assert.assertEquals(0, resultCode.getValue().intValue());
	    
	    Result<DataEntry> result = tairManager.get(namespace, key);
	    assertTrue(result.isSuccess());
	    assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(0, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
	    //clear data
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_910_data_not_exist_count_less_than_0_initValue_equals_0_qa(){
	    
		String key = "909_key_1";
		int value = -1;
		tairManager.delete(namespace, key);
		Result<Integer> resultCode = tairManager.decr(namespace, key, value, 0, 6);
	    Assert.assertFalse(resultCode.isSuccess()); 
	    Assert.assertEquals(ResultCode.ITEMSIZEERROR, resultCode.getRc());
	    Assert.assertEquals(null, resultCode.getValue());
	    
	    Result<DataEntry> result = tairManager.get(namespace, key);
	    assertTrue(result.isSuccess());
	    assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
		
	    //clear data
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_912_data_exist_count_bigger_than_0_initValue_exist_qa(){
	    
		String key = "912_key_1_ÓÃÀý";
		int value = 5;
		tairManager.delete(namespace, key);
		Result<Integer> resultCode = tairManager.decr(namespace, key, value, 0, 6);
	    Assert.assertTrue(resultCode.isSuccess()); 
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode.getRc());
	    Assert.assertEquals(-5, resultCode.getValue().intValue());
	    
	    Result<DataEntry> data = tairManager.get(namespace, key);
        assertTrue(data.isSuccess());
		assertEquals(-5, ((DataEntry) data.getValue()).getValue());
		assertEquals(1, ((DataEntry) data.getValue()).getVersion());
		assertEquals(key, ((DataEntry) data.getValue()).getKey());
		
	    resultCode = tairManager.decr(namespace, key, value, 2, 6);
	    Assert.assertTrue(resultCode.isSuccess()); 
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode.getRc());
	    Assert.assertEquals(-10, resultCode.getValue().intValue());
	    
	    data = tairManager.get(namespace, key);
        assertTrue(data.isSuccess());
		assertEquals(-10, ((DataEntry) data.getValue()).getValue());
		assertEquals(2, ((DataEntry) data.getValue()).getVersion());
		assertEquals(key, ((DataEntry) data.getValue()).getKey());
		
	    //clear data
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_913_data_exist_count_equals_0_initValue_exist_qa(){
	    
		String key = "913_key_1";
		int value = 0;
		tairManager.delete(namespace, key);
		Result<Integer> resultCode = tairManager.decr(namespace, key, value, -1, 6);
	    Assert.assertTrue(resultCode.isSuccess()); 
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode.getRc());
	    Assert.assertEquals(-1, resultCode.getValue().intValue());
	    
	    Result<DataEntry> data = tairManager.get(namespace, key);
        assertTrue(data.isSuccess());
		assertEquals(-1, ((DataEntry) data.getValue()).getValue());
		assertEquals(1, ((DataEntry) data.getValue()).getVersion());
		assertEquals(key, ((DataEntry) data.getValue()).getKey());
		
	    resultCode = tairManager.decr(namespace, key, value, 2, 6);
	    Assert.assertTrue(resultCode.isSuccess()); 
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode.getRc());
	    Assert.assertEquals(-1, resultCode.getValue().intValue());
	    
	    data = tairManager.get(namespace, key);
        assertTrue(data.isSuccess());
		assertEquals(-1, ((DataEntry) data.getValue()).getValue());
		assertEquals(2, ((DataEntry) data.getValue()).getVersion());
		assertEquals(key, ((DataEntry) data.getValue()).getKey());
		
	    
	    //clear data
		tairManager.delete(namespace, key);
	}
	
	@Test
	public void test_914_data_exist_count_less_than_0_initValue_exist_qa(){
	    
		String key = "914_key_1";
		int value = 5;
		tairManager.delete(namespace, key);
		Result<Integer> resultCode = tairManager.decr(namespace, key, value, -1, 6);
	    Assert.assertTrue(resultCode.isSuccess()); 
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode.getRc());
	    Assert.assertEquals(-6, resultCode.getValue().intValue());
	    
	    Result<DataEntry> data = tairManager.get(namespace, key);
        assertTrue(data.isSuccess());
		assertEquals(-6, ((DataEntry) data.getValue()).getValue());
		assertEquals(1, ((DataEntry) data.getValue()).getVersion());
		assertEquals(key, ((DataEntry) data.getValue()).getKey());
		
		value = -1;
	    resultCode = tairManager.decr(namespace, key, value, 2, 6);
	    Assert.assertFalse(resultCode.isSuccess()); 
	    Assert.assertEquals(ResultCode.ITEMSIZEERROR, resultCode.getRc());
	    
	    data = tairManager.get(namespace, key);
        assertTrue(data.isSuccess());
		assertEquals(-6, ((DataEntry) data.getValue()).getValue());
		assertEquals(1, ((DataEntry) data.getValue()).getVersion());
		assertEquals(key, ((DataEntry) data.getValue()).getKey());
	    
	    //clear data
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_915_data_exist_count_bigger_than_0_last_cout_equals_MinIntValue_initValue_exist_qa(){
	    
		String key = "915_key_1";
		int value = 0;
		tairManager.delete(namespace, key);
		Result<Integer> resultCode = tairManager.decr(namespace, key, value, Integer.MIN_VALUE, 6);
	    Assert.assertTrue(resultCode.isSuccess()); 
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode.getRc());
	    Assert.assertEquals(Integer.MIN_VALUE, resultCode.getValue().intValue());
	    
	    Result<DataEntry> data = tairManager.get(namespace, key);
        assertTrue(data.isSuccess());
		assertEquals(Integer.MIN_VALUE, ((DataEntry) data.getValue()).getValue());
		assertEquals(1, ((DataEntry) data.getValue()).getVersion());
		assertEquals(key, ((DataEntry) data.getValue()).getKey());
		
		value = 1;
	    resultCode = tairManager.decr(namespace, key, value, 2, 6);
	    Assert.assertTrue(resultCode.isSuccess()); 
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode.getRc());
	    Assert.assertEquals(Integer.MAX_VALUE, resultCode.getValue().intValue());
	    
	    data = tairManager.get(namespace, key);
        assertTrue(data.isSuccess());
		assertEquals(Integer.MAX_VALUE, ((DataEntry) data.getValue()).getValue());
		assertEquals(2, ((DataEntry) data.getValue()).getVersion());
		assertEquals(key, ((DataEntry) data.getValue()).getKey());
	    
	    //clear data
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_916_data_exist_data_expired_qa() throws InterruptedException{
		
		String key = "916_key_1";
		int value = 5;
		tairManager.delete(namespace, key);
		Result<Integer> resultCode = tairManager.decr(namespace, key, value, 0, 2);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode.getRc());
	    Assert.assertEquals(-5, resultCode.getValue().intValue());
	    
	    Result<DataEntry> data = tairManager.get(namespace, key);
        assertTrue(data.isSuccess());
		assertEquals(-5, ((DataEntry) data.getValue()).getValue());
		assertEquals(1, ((DataEntry) data.getValue()).getVersion());
		assertEquals(key, ((DataEntry) data.getValue()).getKey());
		
	   
	    resultCode =  tairManager.decr(namespace, key, value, 0, 2);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode.getRc());
	    Assert.assertEquals(-10, resultCode.getValue().intValue());
	    
	    data = tairManager.get(namespace, key);
        assertTrue(data.isSuccess());
		assertEquals(-10, ((DataEntry) data.getValue()).getValue());
		assertEquals(2, ((DataEntry) data.getValue()).getVersion());
		assertEquals(key, ((DataEntry) data.getValue()).getKey());
		
		
	    Thread.sleep(3000);
	    resultCode =  tairManager.decr(namespace, key, value, 0, 2);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode.getRc());
	    Assert.assertEquals(-5, resultCode.getValue().intValue());
	    
	    data = tairManager.get(namespace, key);
        assertTrue(data.isSuccess());
		assertEquals(-5, ((DataEntry) data.getValue()).getValue());
		assertEquals(1, ((DataEntry) data.getValue()).getVersion());
		assertEquals(key, ((DataEntry) data.getValue()).getKey());
	
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_917_data_exist_expiretimenegative_qa() throws InterruptedException{
		String key = "311_key_1";
		int value = 1;
		tairManager.delete(namespace, key);
		Result<Integer> count = tairManager.decr(namespace, key, value,2,5);
	    Assert.assertTrue(count.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, count.getRc());
		
		value = 2;
		count = tairManager.incr(namespace, key, value,1,-1);
		Assert.assertFalse(count.isSuccess());
		Assert.assertEquals(ResultCode.INVALIDARG, count.getRc());
		
		Result<DataEntry> result = tairManager.get(namespace, key);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
		
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_918_data_not_exist_expiretimenegative_qa() throws InterruptedException{
		String key = "311_key_1";
		int value = 1;
		tairManager.delete(namespace, key);
		Result<Integer> count = tairManager.decr(namespace, key, value,1,-1);
		Assert.assertFalse(count.isSuccess());
		Assert.assertEquals(ResultCode.INVALIDARG, count.getRc());
		
		Result<DataEntry> result = tairManager.get(namespace, key);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
		
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_918_decr_data_namespace_not_exist_qa() {
		int namespace = 5;
		//data1 area equals key different
		String key = "819_key_1";
		int value = 1;
		tairManager.delete(namespace, key);
		Result<Integer> count = tairManager.decr(namespace, key, value, 1, 0);
        Assert.assertFalse(count.isSuccess());
        Assert.assertEquals(ResultCode.NSUNALLOC,count.getRc());
         
        //verify get function
        Result<DataEntry> data = tairManager.get(namespace, key);
		assertTrue(data.isSuccess());
		assertEquals(ResultCode.DATANOTEXSITS,data.getRc());
		assertEquals(null, data.getValue());

        //clear data
		tairManager.delete(namespace, key);
        
	}
}
