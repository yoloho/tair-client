package com.taobao.tair.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import junit.framework.Assert;

import org.junit.Test;

import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;

public class TairManager_10_x_addItems_Test extends BaseTest {

	private int namespace = 1;
	@Test
	public void test_1001_data_not_exist_qa() {
		//data1 area equals key different
		String key1 = "1001_key_1";
		List value1 = new Vector();
		value1.add(1);
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 5, 0, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
        //data2 area different key equals
        String key = "1001_key_2_用例";
        List value2 = new Vector();
        value2.add("22");
        tairManager.delete(namespace+1, key);
        resultCode = tairManager.addItems(namespace+1, key, value2, 5, 0, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
        //data to test
        List value = new Vector();
        value.add("用例");
        tairManager.delete(namespace, key);
        resultCode = tairManager.addItems(namespace, key, value, 5, 0, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
        //verify get function
        Result<DataEntry> result = tairManager.getItems(namespace, key,0,1);
        assertTrue(result.isSuccess());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());

		//verfy othre datas are not changed
		result = tairManager.getItems(namespace, key1, 0, 1);
        assertTrue(result.isSuccess());
		assertEquals(value1, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key1, ((DataEntry) result.getValue()).getKey());
		
		result = tairManager.getItems(namespace+1, key, 0, 1);
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
	public void test_1002_data_not_exist_area_equals_negative_qa() {
		int namespace = -1;
		String key = "1002_key_1";
		List value = new Vector();
	    value.add("22");
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 2, 5);
	    Assert.assertFalse(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, resultCode);    

        //clear data
		tairManager.delete(namespace, key);
	}
	
	@Test
	public void test_1003_data_not_exist_area_equals_0_qa() {
		int namespace = 0;
		String key = "1003_key_1";
		List value = new Vector();
	    value.add(5);
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 2, 5);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);    

	    Result<DataEntry> result = tairManager.getItems(namespace, key, 0, 1);
        assertTrue(result.isSuccess());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
        //clear data
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_1004_data_not_exist_area_equals_1023_qa() {
		int namespace = 1023;
		String key = "1004_key_1";
		List value = new Vector();
	    value.add(5);
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 2, 5);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);    

	    Result<DataEntry> result = tairManager.getItems(namespace, key, 0, 1);
        assertTrue(result.isSuccess());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
        //clear data
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_1005_data_not_exist_area_equals_1024_qa() {
		int namespace = 1024;
		String key = "1005_key_1";
		List value = new Vector();
	    value.add(5);
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 2, 5);
	    Assert.assertFalse(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, resultCode);    

        //clear data
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_1005_2_data_not_exist_area_equals_65536_qa() {
		int namespace = 65536;
		String key = "1005_key_1";
		List value = new Vector();
	    value.add(5);
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 2, 5);
	    Assert.assertFalse(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, resultCode);    

        //clear data
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_1006_data_not_exist_key_equals_null_qa() {
		int namespace = 5;
		String key = null;
		List value = new Vector();
	    value.add(5);
	    try{
	    	ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 2, 5);
	    	Assert.fail("should throw an IllegalArgumentException");
	    }catch(IllegalArgumentException e){
	    	Assert.assertEquals("key,value can not be null", e.getMessage());
	    }
	    	
	}
	
	@Test
	public void test_1006_1_data_not_exist_key_equals_empty_string_qa() {
		int namespace = 1;
		String key = "";
		List value = new Vector();
	    value.add(5);
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 2, 5);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);    

	    Result<DataEntry> result = tairManager.getItems(namespace, key, 0, 1);
        assertTrue(result.isSuccess());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
        //clear data
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_1007_data_not_exist_value_equals_null_qa() {
		int namespace = 5;
		String key = "1005_key_1";
		List value = null;
	    ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 2, 5);
	    Assert.assertFalse(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SERIALIZEERROR, resultCode);    
	    	
	}
	@Test
	public void test_1007_1_data_not_exist_value_equals_empty_string_qa() {
		int namespace = 5;
		String key = "1005_key_1";
		List value = new Vector();
//		value.add(null);
	    ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 2, 5);
	    Assert.assertFalse(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SERIALIZEERROR, resultCode);  

		value.add(null);
	    resultCode = tairManager.addItems(namespace, key, value, 5, 2, 5);
	    Assert.assertFalse(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SERIALIZEERROR, resultCode);
	   
	    value.add("");
	    resultCode = tairManager.addItems(namespace, key, value, 5, 2, 5);
	    Assert.assertFalse(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SERIALIZEERROR, resultCode);
	    	
	}
	
	@Test
	public void test_1009_data_exist_old_date_version_not_equals_0_version_equals_qa(){
		String key = "1009_key_1_用例";
		List value = new Vector();
		value.add(2);
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 1, 5);
	    Assert.assertTrue(resultCode.isSuccess()); 
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    
	    Result<DataEntry> result = tairManager.getItems(namespace, key,0,1);
	    assertTrue(result.isSuccess());
	    assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
		value.add(4);
		value.add(2);
		resultCode = tairManager.addItems(namespace, key, value, 5, 1, 5);
		Assert.assertTrue(resultCode.isSuccess()); 
		Assert.assertEquals(ResultCode.SUCCESS, resultCode);
		
		result = tairManager.getItems(namespace, key, 0, 5);
		assertTrue(result.isSuccess());
		assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(2, ((DataEntry) result.getValue()).getVersion());
		assertEquals("[2, 2, 4, 2]", ((DataEntry) result.getValue()).getValue().toString());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
	    //clear data
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_1010_data_exist_old_date_version_not_equals_0_version_bigger_than__qa(){
		String key = "1010_key_1";
		List value = new Vector();
		value.add(2);
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 1, 5);
	    Assert.assertTrue(resultCode.isSuccess()); 
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    
	    Result<DataEntry> result = tairManager.getItems(namespace, key,0,1);
	    assertTrue(result.isSuccess());
	    assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
		value.add(4);
		value.add(2);
		resultCode = tairManager.addItems(namespace, key, value, 5, 2, 5);
		Assert.assertFalse(resultCode.isSuccess()); 
		Assert.assertEquals(ResultCode.VERERROR, resultCode);
		
		result = tairManager.getItems(namespace, key, 0, 1);
		assertTrue(result.isSuccess());
		assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals("[2]", ((DataEntry) result.getValue()).getValue().toString());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
	    //clear data
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_1011_data_exist_old_date_version_not_equals_0_version_less_than__qa(){
		String key = "1011_key_1";
		List value = new Vector();
		value.add(2);
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 1, 5);
	    Assert.assertTrue(resultCode.isSuccess()); 
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    resultCode = tairManager.addItems(namespace, key, value, 5, 1, 5);
	    Assert.assertTrue(resultCode.isSuccess()); 
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    
	    Result<DataEntry> result = tairManager.getItems(namespace, key,0,2);
	    assertTrue(result.isSuccess());
	    assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals("[2, 2]",((DataEntry) result.getValue()).getValue().toString());
		assertEquals(2, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
		value.add(4);
		value.add(2);
		resultCode = tairManager.addItems(namespace, key, value, 5, 1, 5);
		Assert.assertFalse(resultCode.isSuccess()); 
		Assert.assertEquals(ResultCode.VERERROR, resultCode);
		
		result = tairManager.getItems(namespace, key, 0, 2);
		assertTrue(result.isSuccess());
		assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(2, ((DataEntry) result.getValue()).getVersion());
		assertEquals("[2, 2]", ((DataEntry) result.getValue()).getValue().toString());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
	    //clear data
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_1012_data_exist_argument_maxcount_bigger_than_(){
		String key = "1012_key_1";
		List value = new Vector();
		value.add(1);
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 1, 1, 5);
	    Assert.assertTrue(resultCode.isSuccess()); 
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    
	    Result<DataEntry> result = tairManager.getItems(namespace, key,0,2);
	    assertTrue(result.isSuccess());
	    assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals("[1]",((DataEntry) result.getValue()).getValue().toString());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
		value.clear();
		value.add(2);
		value.add(3);
		resultCode = tairManager.addItems(namespace, key, value, 2, 1, 5);
		Assert.assertTrue(resultCode.isSuccess()); 
		Assert.assertEquals(ResultCode.SUCCESS, resultCode);
		
		result = tairManager.getItems(namespace, key, 0, 3);
		assertTrue(result.isSuccess());
		assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(2, ((DataEntry) result.getValue()).getVersion());
		assertEquals("[2, 3]", ((DataEntry) result.getValue()).getValue().toString());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
	    //clear data
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_1013_data_exist_argument_maxcount_less_than_(){
		String key = "1013_key_1";
		List value = new Vector();
		value.add(1);
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 1, 1, 5);
	    Assert.assertTrue(resultCode.isSuccess()); 
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    resultCode = tairManager.addItems(namespace, key, value, 2, 1, 5);
	    
	    Result<DataEntry> result = tairManager.getItems(namespace, key,0,3);
	    assertTrue(result.isSuccess());
	    assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals("[1, 1]",((DataEntry) result.getValue()).getValue().toString());
		assertEquals(2, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
		value.clear();
		value.add(2);
		value.add(3);
		resultCode = tairManager.addItems(namespace, key, value, 1, 2, 5);
		Assert.assertTrue(resultCode.isSuccess()); 
		Assert.assertEquals(ResultCode.SUCCESS, resultCode);
		
		result = tairManager.getItems(namespace, key, 0, 3);
		assertTrue(result.isSuccess());
		assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(3, ((DataEntry) result.getValue()).getVersion());
		assertEquals("[3]", ((DataEntry) result.getValue()).getValue().toString());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
	    //clear data
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_1014_data_exist_argument_maxcount_equals_(){
		String key = "1014_key_1";
		List value = new Vector();
		value.add(1);
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 1, 1, 5);
	    Assert.assertTrue(resultCode.isSuccess()); 
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    resultCode = tairManager.addItems(namespace, key, value, 2, 1, 5);
	    
	    Result<DataEntry> result = tairManager.getItems(namespace, key,0,3);
	    assertTrue(result.isSuccess());
	    assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals("[1, 1]",((DataEntry) result.getValue()).getValue().toString());
		assertEquals(2, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
		value.clear();
		value.add(2);
		value.add(3);
		resultCode = tairManager.addItems(namespace, key, value, 2, 2, 5);
		Assert.assertTrue(resultCode.isSuccess()); 
		Assert.assertEquals(ResultCode.SUCCESS, resultCode);
		
		result = tairManager.getItems(namespace, key, 0, 3);
		assertTrue(result.isSuccess());
		assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(3, ((DataEntry) result.getValue()).getVersion());
		assertEquals("[2, 3]", ((DataEntry) result.getValue()).getValue().toString());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
	    //clear data
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_1015_data_exist_argument_maxcount_equals_0(){
		String key = "1015_key_1";
		List value = new Vector();
		value.add(1);
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 1, 1, 5);
	    Assert.assertTrue(resultCode.isSuccess()); 
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    resultCode = tairManager.addItems(namespace, key, value, 2, 1, 5);
	    
	    Result<DataEntry> result = tairManager.getItems(namespace, key,0,3);
	    assertTrue(result.isSuccess());
	    assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals("[1, 1]",((DataEntry) result.getValue()).getValue().toString());
		assertEquals(2, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
		value.clear();
		value.add(2);
		value.add(3);
		resultCode = tairManager.addItems(namespace, key, value, 0, 2, 5);
		Assert.assertFalse(resultCode.isSuccess()); 
		Assert.assertEquals(ResultCode.INVALIDARG, resultCode);
		
		result = tairManager.getItems(namespace, key, 0, 3);
		assertTrue(result.isSuccess());
		assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(2, ((DataEntry) result.getValue()).getVersion());
		assertEquals("[1, 1]", ((DataEntry) result.getValue()).getValue().toString());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
	    //clear data
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_1016_key_size_test_data_not_exist_key_less_than_1k_qa() throws InterruptedException{
		byte[] key = new byte[1020];
		for(int i=0;i<1020;i++){
			key[i]=(byte)i;
		}
		List value = new Vector();
		value.add("1016_data_1");
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 2, 5);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    
	    Result<DataEntry> result = tairManager.getItems(namespace, key,0,1);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    assertEquals(value, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
//		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		    
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_1017_key_size_test_data_not_exist_key_equals_1k_qa() throws InterruptedException{
		byte[] key = new byte[1022];
		for(int i=0;i<1022;i++){
			key[i]=(byte)i;
		}
		List value = new Vector();
		value.add("1017_data_1");
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 2, 5);
	    Assert.assertFalse(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.KEYTOLARGE, resultCode);
		    
	    Result<DataEntry> result = tairManager.getItems(namespace, key,0,1);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.KEYTOLARGE, result.getRc());
	    
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_1018_key_size_test_data_not_exist_key_bigger_than_1k_qa() throws InterruptedException{
		byte[] key = new byte[1023];
	
		List value = new Vector();
		value.add("1018_data_1");
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 2, 5);
	    Assert.assertFalse(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.KEYTOLARGE, resultCode);
		    
	    Result<DataEntry> result = tairManager.get(namespace, key);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.KEYTOLARGE, result.getRc());
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_1019_data_size_test_data_not_exist_data_less_than_one_million_bytes_qa() throws InterruptedException{
		String key = "1019_key_1";
		List value = new Vector();
		for(long i=0;i<99999;i++){
			value.add(i);//1MB字节
		}
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 1000000, 2, 5);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
		    
	    Result<DataEntry> result = tairManager.getItems(namespace, key,0,1000000);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    assertEquals(value, ((DataEntry) result.getValue()).getValue());
	    assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_1020_data_size_test_data_not_exist_data_equals_one_million_bytes_qa() throws InterruptedException{
		String key = "1020_key_1";
		List value = new Vector();
		for(long i=0;i<100000;i++){
			value.add(i);//1MB字节
		}
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 1000000, 2, 5);
	    Assert.assertFalse(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.VALUETOLARGE, resultCode);
		    
	    Result<DataEntry> result = tairManager.getItems(namespace, key,0,1000000);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
	  
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_1021_data_size_test_data_not_exist_data_bigger_than_one_million_bytes_qa() throws InterruptedException{
		String key = "1021_key_1";
		List value = new Vector();
		for(long i=0;i<166667;i++){
			value.add("abcd");//1MB字节
		}
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 1000000, 2, 5);
	    Assert.assertFalse(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.VALUETOLARGE, resultCode);
		    
	    Result<DataEntry> result = tairManager.get(namespace, key);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
	   
		tairManager.delete(namespace, key);
	}
	
	@Test
	public void test_1022_data_size_test_data_exist_old_date_less_than_one_million_bytes_after_put_bigger_than_one_million_bytes_qa() throws InterruptedException{
		String key = "1022_key_1";
		List value = new Vector();
		for(long i=0;i<166666;i++){
			value.add("abcd");
		}
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 1000000, 2, 5);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
		    
	    Result<DataEntry> result = tairManager.getItems(namespace, key,0,1000000);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    Assert.assertEquals(value, ((DataEntry)result.getValue()).getValue());
	    Assert.assertEquals(1, ((DataEntry)result.getValue()).getVersion());
		for(long i=0;i<199996;i++){
			value.add("abcd");
		}
		resultCode = tairManager.addItems(namespace, key, value, 65535, 1, 5);
	    Assert.assertFalse(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.VALUETOLARGE, resultCode);
	    
	    assertEquals(166666, tairManager.getItemCount(namespace, key).getValue());
	    result = tairManager.getItems(namespace, key,0,1000000);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    value.clear();
	    for(long i=0;i<166666;i++){
			value.add("abcd");
		}
	    Assert.assertEquals(value, ((DataEntry)result.getValue()).getValue());
	    Assert.assertEquals(1, ((DataEntry)result.getValue()).getVersion());
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_1023_data_size_test_data_exist_old_date_num_equals_maxcount_after_put_bigger_than_one_million_qa() throws InterruptedException{
		String key = "1023_key_1";
		List value = new Vector();
		for(long i=0;i<65535;i++){
			value.add("1");
		}
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 65535, 2, 5);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    
		value.clear();
		for(long i=0;i<333334;i++){
			value.add("2");
		}
		resultCode = tairManager.addItems(namespace, key, value, 65536, 2, 5);
	    Assert.assertFalse(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.VALUETOLARGE, resultCode);
	    
	    Result<DataEntry> result = tairManager.getItems(namespace, key,0,1000000);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    value.clear();
	    for(long i=0;i<65535;i++){
			value.add("1");
		}
	    assertEquals(value, ((DataEntry) result.getValue()).getValue());
	    assertEquals(1, ((DataEntry) result.getValue()).getVersion());
	    
		tairManager.delete(namespace, key);
	}
	
	@Test
	public void test_1024_data_expired_qa() throws InterruptedException{
		String key = "1024_key_1";
		List value = new Vector();
		value.add(1);
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 2, 2);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    
	    Result<DataEntry> result = tairManager.getItems(namespace, key,0,65536);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    Assert.assertEquals(value, ((DataEntry)result.getValue()).getValue());
	    Assert.assertEquals(1, result.getValue().getVersion());
		
	    Thread.sleep(3000);
	    
	    result = tairManager.getItems(namespace, key,0,65536);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATAEXPIRED, result.getRc());
	    
		value.clear();
		value.add("2");
		resultCode = tairManager.addItems(namespace, key, value, 65536, 2, 5);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    
	    result = tairManager.getItems(namespace, key,0,65536);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    Assert.assertEquals(value, ((DataEntry)result.getValue()).getValue());
	    Assert.assertEquals(1, result.getValue().getVersion());
		
		tairManager.delete(namespace, key);
	}
	
	@Test
	public void test_1025_data_exist_put_data_type_differ_to_old_data_type_int_qa() throws InterruptedException{
		String key = "1025_key_1";
		List value = new Vector();
		value.add(1);
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 2, 5);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    
	    Result<DataEntry> result = tairManager.getItems(namespace, key,0,65536);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    Assert.assertEquals(value,((DataEntry) result.getValue()).getValue());
	    Assert.assertEquals(1, result.getValue().getVersion());
	    
	    List value2 = new Vector();
		value2.add("2");
		value.add("2");
		resultCode = tairManager.addItems(namespace, key, value2, 65536, 1, 5);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    
	    result = tairManager.getItems(namespace, key,0,65536);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    Assert.assertEquals(value,((DataEntry) result.getValue()).getValue());
	    Assert.assertEquals(2, result.getValue().getVersion());
		
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_1025_1_data_exist_put_data_type_differ_to_old_data_type_double_qa() throws InterruptedException{
		String key = "1025_key_1";
		List value = new Vector();
		value.add(1);
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 2, 5);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    
	    Result<DataEntry> result = tairManager.getItems(namespace, key,0,65536);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    Assert.assertEquals(value,((DataEntry) result.getValue()).getValue());
	    Assert.assertEquals(1, result.getValue().getVersion());
	    
	    List value2 = new Vector();
		value2.add(2.1);
		value.add(2.1);
		resultCode = tairManager.addItems(namespace, key, value2, 65536, 1, 5);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    
	    result = tairManager.getItems(namespace, key,0,65536);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    Assert.assertEquals(value,((DataEntry) result.getValue()).getValue());
	    Assert.assertEquals(2, result.getValue().getVersion());
		
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_1025_2_data_exist_put_data_type_differ_to_old_data_type_Long_qa() throws InterruptedException{
		String key = "1025_key_1";
		List value = new Vector();
		value.add(1);
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 2, 5);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    
	    Result<DataEntry> result = tairManager.getItems(namespace, key,0,65536);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    Assert.assertEquals(value,((DataEntry) result.getValue()).getValue());
	    Assert.assertEquals(1, result.getValue().getVersion());
	    
	    List value2 = new Vector();
		value2.add((long)2);
		value.add((long)2);
		resultCode = tairManager.addItems(namespace, key, value2, 65536, 1, 5);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    
	    result = tairManager.getItems(namespace, key,0,65536);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    Assert.assertEquals(value,((DataEntry) result.getValue()).getValue());
	    Assert.assertEquals(2, result.getValue().getVersion());
		
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_1026_data_exist_value_num_bigger_than_maxCount_qa() throws InterruptedException{
		String key = "1026_key_1";
		List value = new Vector();
		value.add(1);
		value.add(2);
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 1, 2, 5);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    
	    Result<DataEntry> result = tairManager.getItems(namespace, key,0,65536);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    value.remove(0);
	    Assert.assertEquals(value, ((DataEntry)result.getValue()).getValue());
	    Assert.assertEquals(1, result.getValue().getVersion());
	    
	    value.add(2);
	    resultCode = tairManager.addItems(namespace, key, value, 1, 1, 5);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    
	    result = tairManager.getItems(namespace, key,0,65536);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    value.remove(0);
	    Assert.assertEquals(value, ((DataEntry)result.getValue()).getValue());
	    Assert.assertEquals(2, result.getValue().getVersion());
		
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_1027_data_exist_expiretimenegative_qa() throws InterruptedException{
		String key = "311_key_1";
		List value = new ArrayList();
		value.add(1);
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value,6,2,5);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
		
		resultCode = tairManager.addItems(namespace, key, value,7,1,-1);
		Assert.assertFalse(resultCode.isSuccess());
		Assert.assertEquals(ResultCode.INVALIDARG, resultCode);
		
		Result<DataEntry> result = tairManager.getItems(namespace, key,0,2);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
		
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_1028_data_not_exist_expiretimenegative_qa() throws InterruptedException{
		String key = "311_key_1";
		List value = new ArrayList();
		value.add(1);
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value,6,1,-1);
		Assert.assertFalse(resultCode.isSuccess());
		Assert.assertEquals(ResultCode.INVALIDARG, resultCode);
		
		Result<DataEntry> result = tairManager.get(namespace, key);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
		
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_1029_maxcount_test_data_not_exist_data_num_equals_1MB_qa() throws InterruptedException{
		String key = "1023_key_1";
		List value = new Vector();
		for(long i=0;i<333333;i++){
			value.add("1");
		}
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 1000000, 2, 5);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    
	    Result<DataEntry> result = tairManager.getItems(namespace, key, 0, 1000000);
	    assertEquals(ResultCode.SUCCESS, result.getRc());
		assertTrue(result.isSuccess());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(333333, ((ArrayList)((DataEntry) result.getValue()).getValue()).size());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
	
		value.clear();
		for(long i=0;i<333334;i++){
			value.add("1");
		}
		tairManager.delete(namespace, key);
		resultCode = tairManager.addItems(namespace, key, value, 10000000, 2, 5);
	    Assert.assertFalse(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.VALUETOLARGE, resultCode);
	    
	    result = tairManager.getItems(namespace, key, 0, 1000000);
		assertTrue(result.isSuccess());
		assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
		
		tairManager.delete(namespace, key);
	}
	
	@Test
	public void test_1019_addItem_data_namespace_not_exist_qa() {
		int namespace = 5;
		//data1 area equals key different
		String key = "819_key_1";
		List value = new Vector();
		value.add("1");
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.addItems(namespace, key, value, 5, 0, 5);
        Assert.assertFalse(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.NSUNALLOC,resultCode);
         
        //verify get function
        Result<DataEntry> data = tairManager.getItems(namespace, key,0,1);
		assertTrue(data.isSuccess());
		assertEquals(ResultCode.DATANOTEXSITS,data.getRc());
		assertEquals(null, data.getValue());

        //clear data
		tairManager.delete(namespace, key);
        
	}
}
