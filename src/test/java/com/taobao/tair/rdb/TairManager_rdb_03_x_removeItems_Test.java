package com.taobao.tair.rdb;

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
import com.taobao.tair.common.BaseTest;
import com.taobao.tair.etc.TairConstant;

public class TairManager_rdb_03_x_removeItems_Test extends BaseTest {

	private int namespace = 1;
	
	@Test
	public void test_rdb_0301_one_million_data_remove_one_million_qa() throws InterruptedException {
		//data1
		String key = "rdb_0301_key_1";
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
        
        //verify get function
		ResultCode result = tairManager.removeItems(namespace, key,0,10000000);
		assertEquals(ResultCode.SUCCESS, result);
        assertTrue(result.isSuccess());
    	
		
		Result<Integer> count  = tairManager.getItemCount(namespace, key);
		Assert.assertTrue(count.isSuccess());
		assertEquals(0, count.getValue());

		Result<DataEntry> items = tairManager.getItems(namespace, key,0,1024);
		Assert.assertFalse(items.isSuccess());
		assertEquals(ResultCode.OUTOFRANGE, items.getRc());

        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_rdb_0302_one_million_data_remove_one_million_count_1001_million_qa() throws InterruptedException {
		//data1
		String key = "rdb_0302_key_1";
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
        
        //verify get function
		ResultCode result = tairManager.removeItems(namespace, key,0,10010000);
        assertTrue(result.isSuccess());
    	assertEquals(ResultCode.SUCCESS, result);
		
		Result<Integer> count  = tairManager.getItemCount(namespace, key);
		Assert.assertTrue(count.isSuccess());
		assertEquals(0, count.getValue());

		Result<DataEntry> items = tairManager.getItems(namespace, key,0,1024);
		Assert.assertFalse(items.isSuccess());
		assertEquals(ResultCode.OUTOFRANGE, items.getRc());
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_rdb_0303_one_million_data_remove_one_million_offset_1001_million_qa() throws InterruptedException {
		//data1
		String key = "rdb_0303_key_1";
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
		Result<Integer> count  = tairManager.getItemCount(namespace, key);
		Assert.assertTrue(count.isSuccess());
		assertEquals(10000000, count.getValue());
        //verify get function
		ResultCode result = tairManager.removeItems(namespace, key,10000000,10010000);
		Assert.assertFalse(result.isSuccess());
    	assertEquals(ResultCode.OUTOFRANGE, result);
		
		count  = tairManager.getItemCount(namespace, key);
		Assert.assertTrue(count.isSuccess());
		assertEquals(10000000, count.getValue());

        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_rdb_0304_one_million_data_remove_one_million_qa() throws InterruptedException {
		//data1
		String key = "rdb_0304_key_1";
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
        
        //verify get function
		ResultCode result = tairManager.removeItems(namespace, key,0,TairConstant.ITEM_ALL);
        assertTrue(result.isSuccess());
    	assertEquals(ResultCode.SUCCESS, result);
		
		Result<Integer> count  = tairManager.getItemCount(namespace, key);
		Assert.assertTrue(count.isSuccess());
		assertEquals(ResultCode.DATANOTEXSITS, count.getRc());
		assertEquals(0, count.getValue());
		Result<DataEntry> items = tairManager.getItems(namespace, key,0,1024);
		Assert.assertTrue(items.isSuccess());
		assertEquals(ResultCode.DATANOTEXSITS, items.getRc());
		
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_rdb_0305_data_exist_size_128M_add_item_size_1M_qa() {
		//data1 area equals key different
		String key1 = "rdb_0112_key_1";
		String value = "";
		for(int i=0;i<998;i++){
			value += "1";
		}//value 1K
		List value1 = new Vector();
		for(int i=0;i<512;i++){
		value1.add(value);
		}
		tairManager.delete(namespace, key1);
		for(int i=0;i<262;i++){
			System.out.println(i);
			ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 10000000, 0, 0);
			System.out.println(resultCode.getCode());
			Assert.assertTrue(resultCode.isSuccess());
			Assert.assertEquals(ResultCode.SUCCESS,resultCode);
		}
        
		value1.clear();
		value1.add(value);
		for(int i=0;i<73;i++){
			System.out.println(i);
			ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 10000000, 0, 0);
			System.out.println(resultCode.getCode());
			Assert.assertTrue(resultCode.isSuccess());
			Assert.assertEquals(ResultCode.SUCCESS,resultCode);
		}
		ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 10000000, 0, 0);
		Assert.assertEquals(ResultCode.ITEMSIZEERROR,resultCode);
		Assert.assertFalse(resultCode.isSuccess());
		
		
		//verify remove function
		ResultCode result = tairManager.removeItems(namespace, key1,0,TairConstant.ITEM_ALL);
        assertTrue(result.isSuccess());
    	assertEquals(ResultCode.SUCCESS, result);
		
    	Result<Integer> count  = tairManager.getItemCount(namespace, key1);
		Assert.assertTrue(count.isSuccess());
		assertEquals(0, count.getValue());
		Result<DataEntry> items = tairManager.getItems(namespace, key1,0,1024);
		Assert.assertTrue(items.isSuccess());
		assertEquals(ResultCode.DATANOTEXSITS, items.getRc());
        //clear data
		tairManager.delete(namespace, key1);
	}
}
