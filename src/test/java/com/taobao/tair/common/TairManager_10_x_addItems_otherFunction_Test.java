package com.taobao.tair.common;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;

public class TairManager_10_x_addItems_otherFunction_Test extends BaseTest {
	private int namespace = 1;
	@Test
	public void test_1001_addItems_interface_otherFunction_Test_qa() {   
        //step 1 addItems data to test
		String key1 = "1001_key_1";
		List value1 = new ArrayList();
        value1.add(1);
        value1.add(2);
        value1.add(3);
        tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.addItems(namespace, key1, value1,5, 1, 0);

        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        Assert.assertTrue(resultCode.isSuccess());
        //step 2 addItems data to test
		String key2 = "1001_key_2";
		List value2 = new ArrayList();
        value2.add("2");
        tairManager.delete(namespace, key2);
        resultCode = tairManager.addItems(namespace, key2, value2,5, 1, 0);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
        //step 3 get
        Result<DataEntry> result = tairManager.get(namespace, key1);
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals(ResultCode.TYPENOTMATCH, result.getRc());
      
        //step 4 mget
        String key3 = "1001_key_3";
		String value3 = "3";
        tairManager.delete(namespace, key3);
        resultCode = tairManager.put(namespace, key3, value3);
        List keys = new ArrayList();
        keys.add(key3);
        keys.add(key1);
        keys.add(key2);
        Result<List<DataEntry>> items = tairManager.mget(namespace, keys);
        Assert.assertFalse(items.isSuccess());
        Assert.assertEquals(ResultCode.TYPENOTMATCH, result.getRc());
        
        //step 5 incr data to test
        Result<Integer> count = tairManager.incr(namespace, key1, 1, 4, 0);
        Assert.assertFalse(count.isSuccess());
        Assert.assertEquals(ResultCode.CANNT_OVERRIDE,count.getRc());
        
        //step 6 incr data to test
        count = tairManager.decr(namespace, key1, 1, 4, 0);
        Assert.assertFalse(count.isSuccess());
        Assert.assertEquals(ResultCode.CANNT_OVERRIDE,count.getRc());
        
        //step 7 add with diff type args
		List value4 = new ArrayList();
        value4.add("1");
        value1.add("1");
        resultCode = tairManager.addItems(namespace, key1, value4,5, 1, 0);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 6 add with same type args
		List value5 = new ArrayList();
        value5.add(4);
        value1.add(4);
        resultCode = tairManager.addItems(namespace, key1, value5,5, 2, 0);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
		
        //step 9 getItems
        result = tairManager.getItems(namespace, key1,0,5);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
        Assert.assertEquals(value1, result.getValue().getValue());
        
        //step 10 getAndRemove
        value1.remove(0);
        result = tairManager.getAndRemove(namespace, key1,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
        Assert.assertEquals("[1]", result.getValue().getValue().toString());
        
      //step 11 removeItems
        resultCode = tairManager.removeItems(namespace, key1,0,1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS, resultCode);
       
        //step 12 getItemCount
        count = tairManager.getItemCount(namespace, key1);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS, count.getRc());
        Assert.assertEquals(3, count.getValue().intValue());
       
        //step 13 delete
        resultCode = tairManager.delete(namespace, key1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS, resultCode);
        count = tairManager.getItemCount(namespace, key1);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS, count.getRc());
        
        //step 14 addItems
        resultCode = tairManager.addItems(namespace, key1, value1,5, 1, 0);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
        //step 15 mdelete
        resultCode = tairManager.mdelete(namespace, keys);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
        //step 16 addItems
        resultCode = tairManager.addItems(namespace, key1, value1,5, 1, 0);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
        //set 17 put 
        String value = "1";
        resultCode = tairManager.put(namespace, key1, value);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
        //step 18 addItems
        resultCode = tairManager.addItems(namespace, key1, value1,5, 0, 0);
        Assert.assertFalse(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.TYPENOTMATCH,resultCode);
        
        //step 19 getItems
        result = tairManager.getItems(namespace, key1,0,4);
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals(ResultCode.TYPENOTMATCH, result.getRc());
        
        //step 20 getAndRemove
        result = tairManager.getAndRemove(namespace, key1,0,1);
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals(ResultCode.TYPENOTMATCH, result.getRc());
        
      //step 21 removeItems
        resultCode = tairManager.removeItems(namespace, key1,0,1);
        Assert.assertFalse(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.TYPENOTMATCH, resultCode);
       
        //step 22 getItemCount
        count = tairManager.getItemCount(namespace, key1);
        Assert.assertFalse(count.isSuccess());
        Assert.assertEquals(ResultCode.TYPENOTMATCH, count.getRc());
        
        //step 23 delete
        resultCode = tairManager.delete(namespace, key1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS, resultCode);
        
	}
	
}
