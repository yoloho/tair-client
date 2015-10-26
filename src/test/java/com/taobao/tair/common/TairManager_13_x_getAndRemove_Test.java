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
import com.taobao.tair.etc.TairConstant;

public class TairManager_13_x_getAndRemove_Test extends BaseTest {
	private int namespace = 1;
	
	@Test
	public void test_1301_data_not_exist_qa() {
		//data1 area equals key different
		String key1 = "101_key_1";
		Vector value1 = new Vector();
		value1.add("1");
		
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
        //data2 area different key equals
        String key = "101_key_2";
        Vector value2 = new Vector();
		value2.add("2");
        tairManager.delete(namespace+1, key);
        resultCode = tairManager.addItems(namespace+1, key, value2,5,1,5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
        //data to test
        Vector value = new Vector();
		value.add("3");
        tairManager.delete(namespace, key);
        
        //verify get function
        Result<DataEntry> result = tairManager.getAndRemove(namespace, key,0,5);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
		assertEquals(null, ((DataEntry) result.getValue()));	

		//verfy othre datas are not changed
		result = tairManager.getItems(namespace, key1,0,1);
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
	public void test_1302_data_not_exist_namespace_equals_negative_qa() {
	
		int namespace = -1;
		String key = "102_key_1";
		tairManager.delete(namespace, key);
		Result<DataEntry> result = tairManager.getAndRemove(namespace, key,0,1);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, result.getRc());
	    assertEquals(null, ((DataEntry) result.getValue()));	    
		
        //clear data
		tairManager.delete(namespace, key);
        
	}

	@Test
	public void test_1303_data_not_exist_namespace_equals_0_qa() {
		
		int namespace = 0;
		String key = "103_key_1";
		tairManager.delete(namespace, key);
		Result<DataEntry> result = tairManager.getAndRemove(namespace, key,0,1);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
	    assertEquals(null, ((DataEntry) result.getValue()));	    
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	
	@Test
	public void test_1304_data_not_exist_namespace_equals_1023_qa() {
		
		int namespace = 1023;
		String key = "104_key_1";
		tairManager.delete(namespace, key);
		Result<DataEntry> result = tairManager.getAndRemove(namespace, key,0,1);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
	    assertEquals(null, ((DataEntry) result.getValue()));	    
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_1305_data_not_exist_namespace_equals_1024_qa() {
	
		int namespace = 1024;
		String key = "105_key_1";
		tairManager.delete(namespace, key);
		Result<DataEntry> result = tairManager.getAndRemove(namespace, key,0,1);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, result.getRc());
	    assertEquals(null, ((DataEntry) result.getValue()));	    
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_1305_data_not_exist_namespace_equals_65536_qa() {
	
		int namespace = 65536;
		String key = "105_key_1";
		tairManager.delete(namespace, key);
		Result<DataEntry> result = tairManager.getAndRemove(namespace, key,0,1);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, result.getRc());
	    assertEquals(null, ((DataEntry) result.getValue()));	    
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_1306_data_not_exist_key_equals_null_qa() {

		String key = null;
		try{
			Result<DataEntry> result = tairManager.getAndRemove(namespace, key,0,1);
			Assert.fail("should throw an IllegalArgumentException");
		}catch(IllegalArgumentException e){
			Assert.assertEquals("key,value can not be null", e.getMessage());
		}
        
	}
	@Test
	public void test_1306_1_data_not_exist_key_equals_empty_string_qa() {

		String key = "";
		tairManager.delete(namespace, key);
		Result<DataEntry> result = tairManager.getAndRemove(namespace, key,0,1);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
	    assertEquals(null, ((DataEntry) result.getValue()));	    
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_1307_data_exist_offset_equals_0_count_less_than_autal_data_num_qa() {
		int namespace = 1023;
		String key = "107_key_1_ÓÃÀý";
		ArrayList value = new ArrayList();
		value.add("1_ÓÃÀý");
		value.add("2");
		value.add("3");
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 1, 5);
		Assert.assertTrue(resultCode.isSuccess());
		Result<DataEntry> result = tairManager.getAndRemove(namespace, key,0,1);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    value.remove(2);
	    value.remove(1);
	    assertEquals(value, ((DataEntry) result.getValue()).getValue());	    
	    assertEquals(key, ((DataEntry) result.getValue()).getKey());	    
	    assertEquals(2, ((DataEntry) result.getValue()).getVersion());
	    
	    result = tairManager.getItems(namespace, key,0,TairConstant.ITEM_ALL);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    value.clear();
	    value.add("2");
	    value.add("3");
	    assertEquals(value, ((DataEntry) result.getValue()).getValue());	    
	    assertEquals(key, ((DataEntry) result.getValue()).getKey());	    
	    assertEquals(2, ((DataEntry) result.getValue()).getVersion());
        //clear data
		tairManager.delete(namespace, key);
        
	}
	
	@Test
	public void test_1308_data_exist_offset_bigger_than_0_count_less_than_autal_data_num_qa() {
		int namespace = 1023;
		String key = "108_key_1";
		ArrayList value = new ArrayList();
		value.add("1");
		value.add("2");
		value.add("3");
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 1, 5);
		Assert.assertTrue(resultCode.isSuccess());
		Result<DataEntry> result = tairManager.getAndRemove(namespace, key,1,1);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    value.remove(2);
	    value.remove(0);
	    assertEquals(value, ((DataEntry) result.getValue()).getValue());	    
	    assertEquals(key, ((DataEntry) result.getValue()).getKey());	    
	    assertEquals(2, ((DataEntry) result.getValue()).getVersion());	
	    
	    result = tairManager.getItems(namespace, key,0,TairConstant.ITEM_ALL);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    value.clear();
	    value.add("1");
	    value.add("3");
	    assertEquals(value, ((DataEntry) result.getValue()).getValue());	    
	    assertEquals(key, ((DataEntry) result.getValue()).getKey());	    
	    assertEquals(2, ((DataEntry) result.getValue()).getVersion());
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_1309_data_exist_offset_less_than_0_count_less_than_autal_data_num_qa() {
		int namespace = 1023;
		String key = "109_key_1";
		ArrayList value = new ArrayList();
		value.add("1");
		value.add("2");
		value.add("3");
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 1, 5);
		Assert.assertTrue(resultCode.isSuccess());
		Result<DataEntry> result = tairManager.getAndRemove(namespace, key,-1,1);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    value.remove(0);
	    value.remove(0);
	    assertEquals(value, ((DataEntry) result.getValue()).getValue());	    
	    assertEquals(key, ((DataEntry) result.getValue()).getKey());	    
	    assertEquals(2, ((DataEntry) result.getValue()).getVersion());	    
	    
	    result = tairManager.getItems(namespace, key,0,TairConstant.ITEM_ALL);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    value.clear();
	    value.add("1");
	    value.add("2");
	    assertEquals(value, ((DataEntry) result.getValue()).getValue());	    
	    assertEquals(key, ((DataEntry) result.getValue()).getKey());	    
	    assertEquals(2, ((DataEntry) result.getValue()).getVersion());
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_1310_data_exist_offset_bigger_than_0_and_bigger_than_autal_data_num_count_less_than_autal_data_num_qa() {
		int namespace = 1023;
		String key = "130_key_1";
		ArrayList value = new ArrayList();
		value.add("1");
		value.add("2");
		value.add("3");
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 1, 5);
		Assert.assertTrue(resultCode.isSuccess());
		Result<DataEntry> result = tairManager.getAndRemove(namespace, key,4,1);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.OUTOFRANGE, result.getRc());  
	    
	    result = tairManager.getItems(namespace, key,0,TairConstant.ITEM_ALL);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    assertEquals(value, ((DataEntry) result.getValue()).getValue());	    
	    assertEquals(key, ((DataEntry) result.getValue()).getKey());	    
	    assertEquals(1, ((DataEntry) result.getValue()).getVersion());
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_1312_data_exist_offset_bigger_than_0_and_less_than_autal_data_num_count_equals_MaxGetNum_qa() {
		int namespace = 1023;
		String key = "130_key_1";
		ArrayList value = new ArrayList();
		value.add("1");
		value.add("2");
		value.add("3");
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 1, 5);
		Assert.assertTrue(resultCode.isSuccess());
		Result<DataEntry> result = tairManager.getAndRemove(namespace, key,1,2);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    value.remove(0);
	    assertEquals(value, ((DataEntry) result.getValue()).getValue());	    
	    assertEquals(key, ((DataEntry) result.getValue()).getKey());	    
	    assertEquals(2, ((DataEntry) result.getValue()).getVersion());	
	    
	    result = tairManager.getItems(namespace, key,0,TairConstant.ITEM_ALL);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    value.clear();
	    value.add("1");
	    assertEquals(value, ((DataEntry) result.getValue()).getValue());	    
	    assertEquals(key, ((DataEntry) result.getValue()).getKey());	    
	    assertEquals(2, ((DataEntry) result.getValue()).getVersion());
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_1311_data_exist_offset_bigger_than_0_and_less_than_autal_data_num_count_bigger_than_MaxGetNum_qa() {
		int namespace = 1023;
		String key = "130_key_1";
		ArrayList value = new ArrayList();
		value.add("1");
		value.add("2");
		value.add("3");
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 1, 5);
		Assert.assertTrue(resultCode.isSuccess());
		Result<DataEntry> result = tairManager.getAndRemove(namespace, key,1,3);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    value.remove(0);
	    assertEquals(value, ((DataEntry) result.getValue()).getValue());	    
	    assertEquals(key, ((DataEntry) result.getValue()).getKey());	    
	    assertEquals(2, ((DataEntry) result.getValue()).getVersion());	 
	    
	    result = tairManager.getItems(namespace, key,0,TairConstant.ITEM_ALL);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    value.clear();
	    value.add("1");
	    assertEquals(value, ((DataEntry) result.getValue()).getValue());	    
	    assertEquals(key, ((DataEntry) result.getValue()).getKey());	    
	    assertEquals(2, ((DataEntry) result.getValue()).getVersion());
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_1313_data_exist_offset_less_than_0_and_less_than_autal_data_num_count_bigger_than_MaxGetNum_qa() {
		int namespace = 1023;
		String key = "130_key_1";
		ArrayList value = new ArrayList();
		value.add("1");
		value.add("2");
		value.add("3");
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 1, 5);
		Assert.assertTrue(resultCode.isSuccess());
		Result<DataEntry> result = tairManager.getAndRemove(namespace, key,-1,3);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    value.remove(0);
	    value.remove(0);
	    assertEquals(value, ((DataEntry) result.getValue()).getValue());	    
	    assertEquals(key, ((DataEntry) result.getValue()).getKey());	    
	    assertEquals(2, ((DataEntry) result.getValue()).getVersion());	  
	    
	    result = tairManager.getItems(namespace, key,0,TairConstant.ITEM_ALL);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    value.clear();
	    value.add("1");
	    value.add("2");
	    assertEquals(value, ((DataEntry) result.getValue()).getValue());	    
	    assertEquals(key, ((DataEntry) result.getValue()).getKey());	    
	    assertEquals(2, ((DataEntry) result.getValue()).getVersion());
        //clear data
		tairManager.delete(namespace, key); 
	}
	@Test
	public void test_1314_data_exist_offset_less_than_0_and_less_than_autal_data_num_count_bigger_than_MaxGetNum_qa() {
		int namespace = 1023;
		String key = "130_key_1";
		ArrayList value = new ArrayList();
		value.add("1");
		value.add("2");
		value.add("3");
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 1, 5);
		Assert.assertTrue(resultCode.isSuccess());
		Result<DataEntry> result = tairManager.getAndRemove(namespace, key,-1,3);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    value.remove(0);
	    value.remove(0);
	    assertEquals(value, ((DataEntry) result.getValue()).getValue());	    
	    assertEquals(key, ((DataEntry) result.getValue()).getKey());	    
	    assertEquals(2, ((DataEntry) result.getValue()).getVersion());	  
	    
	    result = tairManager.getItems(namespace, key,0,TairConstant.ITEM_ALL);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    value.clear();
	    value.add("1");
	    value.add("2");
	    assertEquals(value, ((DataEntry) result.getValue()).getValue());	    
	    assertEquals(key, ((DataEntry) result.getValue()).getKey());	    
	    assertEquals(2, ((DataEntry) result.getValue()).getVersion());
        //clear data
		tairManager.delete(namespace, key); 
	}
	
	@Test
	public void test_1315_data_exist_offset_bigger_than__count_equals_0_qa() {
		int namespace = 1023;
		String key = "130_key_1";
		ArrayList value = new ArrayList();
		value.add("1");
		value.add("2");
		value.add("3");
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 1, 5);
		Assert.assertTrue(resultCode.isSuccess());
		Result<DataEntry> result = tairManager.getAndRemove(namespace, key,1,0);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.INVALIDARG , result.getRc()); 
	  
	    result = tairManager.getItems(namespace, key,0,TairConstant.ITEM_ALL);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    assertEquals(value, ((DataEntry) result.getValue()).getValue());	    
	    assertEquals(key, ((DataEntry) result.getValue()).getKey());	    
	    assertEquals(1, ((DataEntry) result.getValue()).getVersion());
        //clear data
		tairManager.delete(namespace, key); 
	}
	@Test
	public void test_1316_data_exist_offset_equals_0_count_equals_all_qa() {
		int namespace = 1023;
		String key = "130_key_1";
		ArrayList value = new ArrayList();
		value.add("1");
		value.add("2");
		value.add("3");
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 1, 5);
		Assert.assertTrue(resultCode.isSuccess());
		Result<DataEntry> result = tairManager.getAndRemove(namespace, key,0,TairConstant.ITEM_ALL);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS , result.getRc()); 
	    assertEquals(value, ((DataEntry) result.getValue()).getValue());	    
	    assertEquals(key, ((DataEntry) result.getValue()).getKey());	    
	    assertEquals(2, ((DataEntry) result.getValue()).getVersion());	
	  
	    result = tairManager.getItems(namespace, key,0,TairConstant.ITEM_ALL);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
	    assertEquals(null,  result.getValue());	    
        //clear data
		tairManager.delete(namespace, key); 
	}
	@Test
	public void test_1317_data_exist_offset_less_than_0_count_equals_all_qa() {
		int namespace = 1023;
		String key = "130_key_1";
		ArrayList value = new ArrayList();
		value.add("1");
		value.add("2");
		value.add("3");
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 1, 5);
		Assert.assertTrue(resultCode.isSuccess());
		Result<DataEntry> result = tairManager.getAndRemove(namespace, key,-2,TairConstant.ITEM_ALL);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS , result.getRc()); 
	    value.remove(0);
	    assertEquals(value, ((DataEntry) result.getValue()).getValue());	    
	    assertEquals(key, ((DataEntry) result.getValue()).getKey());	    
	    assertEquals(2, ((DataEntry) result.getValue()).getVersion());	
	    
	    result = tairManager.getItems(namespace, key,0,TairConstant.ITEM_ALL);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    value.clear();
	    value.add("1");
	    assertEquals(value, ((DataEntry) result.getValue()).getValue());	    
	    assertEquals(key, ((DataEntry) result.getValue()).getKey());	    
	    assertEquals(2, ((DataEntry) result.getValue()).getVersion());
	  
        //clear data
		tairManager.delete(namespace, key); 
	}
	@Test
	public void test_1318_data_exist_offset_less_than_0_count_less_than_0_qa() {
		int namespace = 1023;
		String key = "130_key_1";
		ArrayList value = new ArrayList();
		value.add("1");
		value.add("2");
		value.add("3");
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 1, 5);
		Assert.assertTrue(resultCode.isSuccess());
		Result<DataEntry> result = tairManager.getAndRemove(namespace, key,-2,-1);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.INVALIDARG , result.getRc()); 
	    
	    result = tairManager.getItems(namespace, key,0,TairConstant.ITEM_ALL);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    assertEquals(value, ((DataEntry) result.getValue()).getValue());	    
	    assertEquals(key, ((DataEntry) result.getValue()).getKey());	    
	    assertEquals(1, ((DataEntry) result.getValue()).getVersion());
	  
        //clear data
		tairManager.delete(namespace, key); 
	}
	
	@Test
	public void test_1319_data_exist_data_expired_namespace_equals_1023_qa() throws InterruptedException {
		int namespace = 1023;
		String key = "108_key_1";
		String value = "108_data_1";
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.put(namespace, key, value, 0, 1);
		Assert.assertTrue(resultCode.isSuccess());
		Thread.sleep(2000);
		Result<DataEntry> result = tairManager.getAndRemove(namespace, key,0,1);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATAEXPIRED, result.getRc());
	    
	    result = tairManager.getItems(namespace, key,0,TairConstant.ITEM_ALL);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
	    assertEquals(null,  result.getValue());	    
        //clear data
		tairManager.delete(namespace, key);
        
	}
}
