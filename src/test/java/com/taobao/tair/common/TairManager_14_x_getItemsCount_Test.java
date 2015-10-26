package com.taobao.tair.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Vector;

import junit.framework.Assert;

import org.junit.Test;

import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;

public class TairManager_14_x_getItemsCount_Test extends BaseTest {
	private int namespace = 1;
	
	@Test
	public void test_1401_data_not_exist_qa() {
		//data1 area equals key different
		String key1 = "1401_key_1";
		Vector value1 = new Vector();
		value1.add("1");
		
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
        //data2 area different key equals
        String key = "1401_key_2";
        Vector value2 = new Vector();
		value2.add("2");
        tairManager.delete(namespace+1, key);
        resultCode = tairManager.addItems(namespace+1, key, value2,5,1,5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
        //data to test
        Vector value = new Vector();
        tairManager.delete(namespace, key);
        
        //verify get function
        Result<Integer> count = tairManager.getItemCount(namespace, key);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS, count.getRc());
		assertEquals(0,  count.getValue());	

		//verfy othre datas are not changed
		Result<DataEntry> result = tairManager.getItems(namespace, key1,0,1);
        assertTrue(result.isSuccess());
		assertEquals(value1, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key1, ((DataEntry) result.getValue()).getKey());
		
		result = tairManager.getItems(namespace+1, key,0,1);
        assertTrue(result.isSuccess());
		assertEquals(value2, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
        //clear data
		tairManager.delete(namespace, key1);
		tairManager.delete(namespace+1, key);
	}
	@Test
	public void test_1402_data_not_exist_namespace_equals_negative_qa() {
	
		int namespace = -1;
		String key = "1402_key_1";
		tairManager.delete(namespace, key);
		Result<Integer> result = tairManager.getItemCount(namespace, key);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, result.getRc());
	    assertEquals(null, result.getValue());	    
		
        //clear data
		tairManager.delete(namespace, key);
        
	}

	@Test
	public void test_1403_data_not_exist_namespace_equals_0_qa() {
		
		int namespace = 0;
		String key = "1403_key_1";
		tairManager.delete(namespace, key);
		Result<Integer> result = tairManager.getItemCount(namespace, key);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
	    assertEquals(0, result.getValue());	    
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	
	@Test
	public void test_1404_data_not_exist_namespace_equals_1023_qa() {
		
		int namespace = 1023;
		String key = "1404_key_1";
		tairManager.delete(namespace, key);
		Result<Integer> result = tairManager.getItemCount(namespace, key);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
	    assertEquals(0, result.getValue());	    
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_1405_data_not_exist_namespace_equals_1024_qa() {
	
		int namespace = 1024;
		String key = "1405_key_1";
		tairManager.delete(namespace, key);
		Result<Integer> result = tairManager.getItemCount(namespace, key);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, result.getRc());
	    assertEquals(null, result.getValue());	    
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_1405_2_data_not_exist_namespace_equals_65536_qa() {
	
		int namespace = 65536;
		String key = "1405_key_1";
		tairManager.delete(namespace, key);
		Result<Integer> result = tairManager.getItemCount(namespace, key);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, result.getRc());
	    assertEquals(null, result.getValue());	    
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_1406_data_not_exist_key_equals_null_qa() {

		String key = null;
		try{
			Result<Integer> result = tairManager.getItemCount(namespace, key);
			Assert.fail("should throw an IllegalArgumentException");
		}catch(IllegalArgumentException e){
			Assert.assertEquals("key,value can not be null", e.getMessage());
		}
        
	}
	@Test
	public void test_1406_1_data_not_exist_key_equals_empty_string_qa() {

		String key = "";
		tairManager.delete(namespace, key);
		Result<Integer> result = tairManager.getItemCount(namespace, key);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
	    assertEquals(0,  result.getValue());	    
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_1407_data_exist_data_not_expired_qa() {
		int namespace = 1023;
		String key = "1407_key_1_ÓÃÀý";
		ArrayList value = new ArrayList();
		value.add("ÓÃÀý");
		value.add("2");
		value.add("3");
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 1, 5);
		Assert.assertTrue(resultCode.isSuccess());
		Result<Integer> result = tairManager.getItemCount(namespace, key);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    value.remove(2);
	    value.remove(1);
	    assertEquals(3, result.getValue());	    
	    
        //clear data
		tairManager.delete(namespace, key);
        
	}
	
	@Test
	public void test_1408_data_exist_data_expired_qa() throws InterruptedException {
		int namespace = 1023;
		String key = "1408_key_1";
		ArrayList value = new ArrayList();
		value.add("1");
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 1, 2);
		Assert.assertTrue(resultCode.isSuccess());
		Result<Integer> result = tairManager.getItemCount(namespace, key);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    assertEquals(1, result.getValue());	    
	   
	    Thread.sleep(3000);
	    result = tairManager.getItemCount(namespace, key);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATAEXPIRED, result.getRc());
	    assertEquals(0, result.getValue());	    
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_1409_data_exist_data_num_equals_10000000_qa() throws InterruptedException {
		int namespace = 1023;
		String key = "1409_key_1";
		ArrayList value = new ArrayList();
		for(int i =0;i<10000;i++){
			value.add(i);
		}
		tairManager.delete(namespace, key);
		for(int i=1;i<1000;i++){
			System.out.println(i);
			ResultCode resultCode = tairManager.addItems(namespace, key, value, 10010000, 0, 0);
			System.out.println(resultCode.getCode());
			Assert.assertTrue(resultCode.isSuccess());
			Result<Integer> result = tairManager.getItemCount(namespace, key);
			Assert.assertTrue(result.isSuccess());
			Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
			assertEquals(10000*i, result.getValue());	
		}
        //clear data
		tairManager.delete(namespace, key);
        
	}
	
	@Test
	public void test_1410_data_exist_data_num_equals_10000001_qa() throws InterruptedException {
		int namespace = 1023;
		String key = "1409_key_1";
		ArrayList value = new ArrayList();
		for(int i =0;i<10000;i++){
			value.add(i);
		}
		tairManager.delete(namespace, key);
		for(int i=1;i<1001;i++){
			System.out.println(i);
			ResultCode resultCode = tairManager.addItems(namespace, key, value, 10010000, 0, 0);
			System.out.println(resultCode.getCode());
			Assert.assertTrue(resultCode.isSuccess());
			Result<Integer> result = tairManager.getItemCount(namespace, key);
			Assert.assertTrue(result.isSuccess());
			Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
			assertEquals(10000*i, result.getValue());	
		}
		value.clear();
		value.add(1);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 10010000, 0, 0);
		System.out.println(resultCode.getCode());
		Assert.assertTrue(resultCode.isSuccess());
		Result<Integer> result = tairManager.getItemCount(namespace, key);
		assertEquals(10000*1000, result.getValue());	
		resultCode = tairManager.addItems(namespace, key, value, 10010000, 0, 0);
		System.out.println(resultCode.getCode());
		Assert.assertTrue(resultCode.isSuccess());
		result = tairManager.getItemCount(namespace, key);
		assertEquals(10000*1000, result.getValue());	
        //clear data
		tairManager.delete(namespace, key);
        
	}
}
