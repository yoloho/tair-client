package com.taobao.tair.common;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;

public class TairManager_03_x_put_otherFunction_Test extends BaseTest {
	private int namespace = 1;
	@Test
	public void test_301_put_interface_otherFunction_Test_qa() {   
        //step 1 add data to test
		String key = "321_key_1";
        int value = 1;
        tairManager.delete(namespace, key);
        ResultCode resultCode = tairManager.put(namespace, key, value,-1,0);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
        //step 2 addItems
        List items = new ArrayList();
        items.add(2);
        resultCode = tairManager.addItems(namespace, key, items, 5, 1, 4);
        Assert.assertFalse(resultCode.isSuccess());
        Result<DataEntry> result = tairManager.get(namespace, key);
        Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
		//step 3 getItem
        result = tairManager.getItems(namespace, key, 0, 1);
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals(ResultCode.TYPENOTMATCH, result.getRc());
        result = tairManager.get(namespace, key);
        Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
//		step 4 getAndRemove 
        result = tairManager.getAndRemove(namespace, key, 0, 1);
        Assert.assertFalse(result.isSuccess());
        result = tairManager.get(namespace, key);
        Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
//
//		//step 5 removeItems 
        resultCode = tairManager.removeItems(namespace, key, 0, 1);
        Assert.assertTrue(result.isSuccess());
        result = tairManager.get(namespace, key);
        Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());

		//step 6 getItemsCount
		Result<Integer> count = tairManager.getItemCount(namespace, key);
        Assert.assertFalse(count.isSuccess());
        Assert.assertEquals(ResultCode.TYPENOTMATCH, count.getRc());
        result = tairManager.get(namespace, key);
        Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
		//step 7 incr
		count = tairManager.incr(namespace, key, 1, 4, 5);
        Assert.assertFalse(count.isSuccess());
        Assert.assertEquals(ResultCode.CANNT_OVERRIDE, count.getRc());
        result = tairManager.get(namespace, key);
        Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
		//step 8 incr
		count = tairManager.decr(namespace, key, 1, 4, 5);
        Assert.assertFalse(count.isSuccess());
        Assert.assertEquals(ResultCode.CANNT_OVERRIDE, count.getRc());
        result = tairManager.get(namespace, key);
        Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
		//step 9 put不带version和expire时间
		resultCode = tairManager.put(namespace, key, 0);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS, resultCode);
        result = tairManager.get(namespace, key);
        Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(0, ((DataEntry) result.getValue()).getValue());
		assertEquals(2, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
		//step 10 put 带version相等
		resultCode = tairManager.put(namespace, key, 5,2);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS, resultCode);
        result = tairManager.get(namespace, key);
        Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(5, ((DataEntry) result.getValue()).getValue());
		assertEquals(3, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
		//step 11 put 带version和expire相等
		resultCode = tairManager.put(namespace, key, 7,3,6);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS, resultCode);
        result = tairManager.get(namespace, key);
        Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(7, ((DataEntry) result.getValue()).getValue());
		assertEquals(4, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
		//step 12 put 不同的key和value
		tairManager.delete(namespace, key+1);
		resultCode = tairManager.put(namespace, key+1, 1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS, resultCode);
        result = tairManager.get(namespace, key+1);
        Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(1, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key+"1", ((DataEntry) result.getValue()).getKey());
		
		//step 13 get 操作
        result = tairManager.get(namespace, key);
        Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(7, ((DataEntry) result.getValue()).getValue());
		assertEquals(4, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
		//step 14 mget 操作
		List keys = new ArrayList();
		keys.add(key);
		keys.add(key+"1");
		Result<List<DataEntry>> resultList = tairManager.mget(namespace, keys);
        Assert.assertEquals(ResultCode.SUCCESS, resultList.getRc());
        Assert.assertEquals(2, resultList.getValue().size());
        Assert.assertTrue(
        			(
        					(
        							1==((DataEntry) resultList.getValue().get(0)).getVersion())
        							&& ((key+"1").equals(((DataEntry) resultList.getValue().get(0)).getKey()))
        							&& (4==((DataEntry) resultList.getValue().get(1)).getVersion())
        							&& (key.equals(((DataEntry) resultList.getValue().get(1)).getKey()))
        					)
        					||
        					(
        							(1==((DataEntry) resultList.getValue().get(1)).getVersion())
        							&& ((key+"1").equals(((DataEntry) resultList.getValue().get(1)).getKey()))
        							&& (4==((DataEntry) resultList.getValue().get(0)).getVersion())
        							&& (key.equals(((DataEntry) resultList.getValue().get(0)).getKey()))
        					)
 					);
		
		
		//step 15 put 相同的key和value
		resultCode = tairManager.delete(namespace, key);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS, resultCode);
        result = tairManager.get(namespace, key);
        Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());

        //step 16 put 带version和expire相等
		resultCode = tairManager.put(namespace, key, 7,4,6);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS, resultCode);
        result = tairManager.get(namespace, key);
        Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(7, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key, ((DataEntry) result.getValue()).getKey());
		
		//step 17 mremove 带version和expire相等
		resultCode = tairManager.mdelete(namespace, keys);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS, resultCode);
        resultList = tairManager.mget(namespace, keys);
        Assert.assertEquals(ResultCode.DATANOTEXSITS, resultList.getRc());
        Assert.assertEquals(0, resultList.getValue().size());
		
	}
	
}
