package com.taobao.tair.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Test;

import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;

public class TairManager_03_2_put_Test extends BaseTest {
	private int namespace = 1;
	@Test
	public void test_301_data_not_exist_qa() {
		//data1 area equals key different
		String key1 = "321_key_1";
		String value1 = "321_data_1";
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.put(namespace, key1, value1,1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
        //data2 area different key equals
        String key = "321_key_2";
        String value2 = "32data_2";
        tairManager.delete(namespace+1, key);
        resultCode = tairManager.put(namespace+1, key, value2,0);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
        //data to test
        String value = "321_data_3";
        tairManager.delete(namespace, key);
        resultCode = tairManager.put(namespace, key, value,-1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
        //verify get function
        Result<DataEntry> result = tairManager.get(namespace, key);
        assertTrue(result.isSuccess());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());

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
	public void test_302_data_not_exist_namespace_equals_negative_qa() {
	
		int namespace = -1;
		String key = "322_key_1";
		String value = "322_data_1";
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.put(namespace, key, value,0);
	    Assert.assertFalse(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, resultCode);    

        //clear data
		tairManager.delete(namespace, key);
        
	}

	@Test
	public void test_303_data_not_exist_namespace_equals_0_qa() {
		
		int namespace = 0;
		String key = "323_key_1";
		String value = "323_data_1";
		
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.put(namespace, key, value,Integer.MAX_VALUE);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);    
	    
	    Result<DataEntry> result = tairManager.get(namespace, key);
        assertTrue(result.isSuccess());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
        //clear data
		tairManager.delete(namespace, key);
        
	}
	
	@Test
	public void test_304_data_not_exist_namespace_equals_1023_qa() {
		
		int namespace = 1023;
		String key = "324_key_1";
		String value = "324_data_1";
		
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.put(namespace, key, value,Integer.MIN_VALUE-1);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);       
		
	    Result<DataEntry> result = tairManager.get(namespace, key);
        assertTrue(result.isSuccess());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_305_data_not_exist_namespace_equals_1024_qa() {
	
		int namespace = 1024;
		String key = "325_key_1";
		String value = "325_data_1";
		tairManager.delete(namespace, key);
		ResultCode result = tairManager.put(namespace, key, value,Integer.MAX_VALUE+1);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, result);       
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_305_data_not_exist_namespace_equals_65536_qa() {
	
		int namespace = 65536;
		String key = "325_key_1";
		String value = "325_data_1";
		tairManager.delete(namespace, key);
		ResultCode result = tairManager.put(namespace, key, value,Integer.MAX_VALUE+1);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.NSERROR, result);       
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_306_data_not_exist_key_equals_null_qa() {

		String key = null;
		String value = "326_data_1";
		ResultCode resultCode = tairManager.put(namespace, key, value,1);
		Assert.assertFalse(resultCode.isSuccess());  
		Assert.assertEquals(ResultCode.SERIALIZEERROR, resultCode); 
			
	}
	@Test
	public void test_306_1_data_not_exist_key_equals_empty_string_qa() {

		String key = "";
		String value = "326_data_1";
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.put(namespace, key, value,Integer.MAX_VALUE+1);
	    Assert.assertTrue(resultCode.isSuccess());  
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode); 
	    
	    Result<DataEntry> result = tairManager.get(namespace, key);
        assertTrue(result.isSuccess());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
	    //clear data
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_307_data_not_exist_data_equals_null_qa() {

		String key = "327_key_1";
		String value = null;
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.put(namespace, key, value,Integer.MAX_VALUE+1);
		Assert.assertFalse(resultCode.isSuccess());  
		Assert.assertEquals(ResultCode.SERIALIZEERROR, resultCode); 
	    //clear data
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_307_1_data_not_exist_data_equals_empty_string_qa() {

		String key = "307_key_1";
		String value = "";
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.put(namespace, key, value,Integer.MAX_VALUE+1);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode); 
	    
	    Result<DataEntry> result = tairManager.get(namespace, key);
        assertTrue(result.isSuccess());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
	    //clear data
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_308_data_exist_old_date_version_not_equals_0_version_equals_qa(){
		String key = "328_key_1";
		String value = "328_data_1";
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
		
		value = "328_data_2";
		resultCode = tairManager.put(namespace, key, value,1);
		Assert.assertTrue(resultCode.isSuccess()); 
		Assert.assertEquals(ResultCode.SUCCESS, resultCode);
		
		result = tairManager.get(namespace, key);
		assertTrue(result.isSuccess());
		assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(2, ((DataEntry) result.getValue()).getVersion());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
	    //clear data
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_309_data_exist_old_date_version_not_equals_0_version_bigger_than__qa(){
		String key = "328_key_1";
		String value = "328_data_1";
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
		
		String value2 = "328_data_2";
		resultCode = tairManager.put(namespace, key, value2,2);
		Assert.assertFalse(resultCode.isSuccess()); 
		Assert.assertEquals(ResultCode.VERERROR, resultCode);
		
		result = tairManager.get(namespace, key);
		assertTrue(result.isSuccess());
		assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
	    //clear data
		tairManager.delete(namespace, key);
	}
	
	@Test
	public void test_310_data_exist_old_date_version_not_equals_0_version_less_than__qa(){
		String key = "328_key_1";
		String value = "328_data_1";
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.put(namespace, key, value,2);
	    Assert.assertTrue(resultCode.isSuccess()); 
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    
	    resultCode = tairManager.put(namespace, key, value,1);
	    Assert.assertTrue(resultCode.isSuccess()); 
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    
	    Result<DataEntry> result = tairManager.get(namespace, key);
	    assertTrue(result.isSuccess());
	    assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
		assertEquals(2, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
		String value2 = "328_data_2";
		resultCode = tairManager.put(namespace, key, value2,1);
		Assert.assertFalse(resultCode.isSuccess()); 
		Assert.assertEquals(ResultCode.VERERROR, resultCode);
		
		result = tairManager.get(namespace, key);
		assertTrue(result.isSuccess());
		assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(2, ((DataEntry) result.getValue()).getVersion());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
	    //clear data
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_311_data_exist_old_date_expired_qa() throws InterruptedException{
		String key = "311_key_1";
		String value = "311_data_1";
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.put(namespace, key, value,2,1);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    
	    Thread.sleep(2000);
	    Result<DataEntry> result = tairManager.get(namespace, key);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATAEXPIRED, result.getRc());
	    Assert.assertEquals(null, result.getValue());
	    
		
		value = "311_data_2";
		resultCode = tairManager.put(namespace, key, value,3);
		assertTrue(resultCode.isSuccess());
		Assert.assertEquals(ResultCode.SUCCESS, resultCode);
		
		result = tairManager.get(namespace, key);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
		Thread.sleep(2000);
		result = tairManager.get(namespace, key);
		Assert.assertTrue(result.isSuccess());
		Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
		    
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_312_key_size_test_data_not_exist_key_less_than_1k_qa() throws InterruptedException{
		byte[] key = new byte[1020];
		for(int i=0;i<1020;i++){
			key[i]=(byte)i;
		}
		String value = "312_data_1";
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.put(namespace, key, value,1);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	    
	    Result<DataEntry> result = tairManager.get(namespace, key);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    assertEquals(value, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		//assertEquals(key, ((DataEntry) result.getValue()).getKey());
		    
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_313_key_size_test_data_not_exist_key_equals_1k_qa() throws InterruptedException{
		byte[] key = new byte[1022];
		for(int i=0;i<1022;i++){
			key[i]=(byte)i;
		}
		String value = "313_data_1";
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.put(namespace, key, value,1);
	    Assert.assertFalse(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.KEYTOLARGE, resultCode);
		    
	    Result<DataEntry> result = tairManager.get(namespace, key);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.KEYTOLARGE, result.getRc());
	    
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_314_key_size_test_data_not_exist_key_bigger_than_1k_qa() throws InterruptedException{
		byte[] key = new byte[1023];
	
		String value = "314_data_1";
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.put(namespace, key, value,2);
	    Assert.assertFalse(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.KEYTOLARGE, resultCode);
		    
	    Result<DataEntry> result = tairManager.get(namespace, key);
	    Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.KEYTOLARGE, result.getRc());
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_315_data_size_test_data_not_exist_data_less_than_one_million_bytes_qa() throws InterruptedException{
		String key = "315_key_1";
		int[] value = new int[249990];//999960字节
	
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.put(namespace, key, value,3);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
		    
	    Result<DataEntry> result = tairManager.get(namespace, key);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    for(int i=0;i<value.length;i++){
	    	assertEquals(value[i], ((int[])((DataEntry) result.getValue()).getValue())[i]);
	    }
	    assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_316_data_size_test_data_not_exist_data_equals_one_million_bytes_qa() throws InterruptedException{
		String key = "316_key_1";
		int[] value = new int[249993];//999972字节
	
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.put(namespace, key, value,4);
	    Assert.assertFalse(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.VALUETOLARGE, resultCode);
		    
	    Result<DataEntry> result = tairManager.get(namespace, key);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
	   
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_317_data_size_test_data_not_exist_data_bigger_than_one_million_bytes_qa() throws InterruptedException{
		String key = "317_key_1";
		int[] value = new int[250000];//1000000字节
	
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.put(namespace, key, value,5);
	    Assert.assertFalse(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.VALUETOLARGE, resultCode);
		    
	    Result<DataEntry> result = tairManager.get(namespace, key);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
	   
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_318_data_size_test_data_exist_argument_data_bigger_than_one_million_bytes_qa() throws InterruptedException{
		String key = "318_key_1";
		int[] value = new int[50];//1000000字节
	
		tairManager.delete(namespace, key);
		ResultCode resultCode = tairManager.put(namespace, key, value,6);
	    Assert.assertTrue(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, resultCode);
		    
	    Result<DataEntry> result = tairManager.get(namespace, key);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    for(int i=0;i<value.length;i++){
	    	assertEquals(value[i], ((int[])((DataEntry) result.getValue()).getValue())[i]);
	    }
	    assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
	    
	    byte[] value2 = new byte[1000000];//1000000字节
	    resultCode = tairManager.put(namespace, key, value2,1);
	    Assert.assertFalse(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.VALUETOLARGE, resultCode);
	    
	    result = tairManager.get(namespace, key);
	    Assert.assertTrue(result.isSuccess());
	    Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
	    for(int i=0;i<value.length;i++){
	    	assertEquals(value[i], ((int[])((DataEntry) result.getValue()).getValue())[i]);
	    }
	    assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
		tairManager.delete(namespace, key);
	}
	@Test
	public void test_319_version_boundary_qa() {
        
        //data to test
		String key = "319_key_建";
        String value = "321_data_3件";
        tairManager.delete(namespace, key);
        ResultCode resultCode ;
        for(int i=0;i<32767;i++)
        {
        	resultCode = tairManager.put(namespace, key, value);
        	Assert.assertTrue(resultCode.isSuccess());
        	Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        }
        //verify get function
        Result<DataEntry> result = tairManager.get(namespace, key);
        assertTrue(result.isSuccess());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
		assertEquals(32767, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());

		//modify data
		resultCode = tairManager.put(namespace, key, value);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
		
        
        //verify get function
		result = tairManager.get(namespace, key);
		assertTrue(result.isSuccess());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
		assertEquals(-32768, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		//modify data function
		for (int i = 0; i < 32770; i++) {
			resultCode = tairManager.put(namespace, key, value);
			Assert.assertTrue(resultCode.isSuccess());
			Assert.assertEquals(ResultCode.SUCCESS, resultCode);
		}       
        //verify get function
		result = tairManager.get(namespace, key);
		assertTrue(result.isSuccess());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
		assertEquals(2, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
        //clear data
		tairManager.delete(namespace, key);
        
	}
}
