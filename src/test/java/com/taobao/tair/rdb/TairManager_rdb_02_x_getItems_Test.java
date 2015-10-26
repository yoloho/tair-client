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

public class TairManager_rdb_02_x_getItems_Test extends BaseTest {

	private int namespace = 1;
	
	@Test
	public void test_rdb_0201_get_item_size_smaller_than_1M_qa() {
		//data1 area equals key different
		String key1 = "rdb_0201_key_1";
		String value = "";
		for(int i=0;i<998;i++){
			value += "1";
		}//value 1K
		List value1 = new Vector();
		for(int i=0;i<900;i++){
		value1.add(value);
		}
		tairManager.delete(namespace, key1);

		ResultCode resultCode = tairManager.addItems(namespace, key1, value1,10000000, 0, 0);
		Assert.assertTrue(resultCode.isSuccess());
		Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	
		resultCode = tairManager.addItems(namespace, key1, value1,10000000, 0, 0);
		Assert.assertTrue(resultCode.isSuccess());
		Assert.assertEquals(ResultCode.SUCCESS, resultCode);
        
		value1.clear();
		for(int i=0;i<997;i++){
			value1.add(value);
			}
		Result<DataEntry> result = tairManager.getItems(namespace, key1,0,997);
		Assert.assertTrue(result.isSuccess());
		assertEquals(value1, ((DataEntry) result.getValue()).getValue());

        //clear data
		tairManager.delete(namespace, key1);
	}
	@Test
	public void test_rdb_0202_get_item_size_equals_1M_qa() {
		//data1 area equals key different
		String key1 = "rdb_0202_key_1";
		String value = "";
		for(int i=0;i<998;i++){
			value += "1";
		}//value 1K
		List value1 = new Vector();
		for(int i=0;i<999;i++){
		value1.add(value);
		}
		tairManager.delete(namespace, key1);

		ResultCode resultCode = tairManager.addItems(namespace, key1, value1,10000000, 0, 0);
		System.out.println(resultCode.getCode());
		Assert.assertTrue(resultCode.isSuccess());
		Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	
		resultCode = tairManager.addItems(namespace, key1, value1,10000000, 0, 0);
		System.out.println(resultCode.getCode());
		Assert.assertTrue(resultCode.isSuccess());
		Assert.assertEquals(ResultCode.SUCCESS, resultCode);
        
		value1.clear();
		for(int i=0;i<1000;i++){
			value1.add(value);
			}
		Result<DataEntry> result = tairManager.getItems(namespace, key1,0,1000);
		Assert.assertTrue(result.isSuccess());
		assertEquals(value1, ((DataEntry) result.getValue()).getValue());

        //clear data
		tairManager.delete(namespace, key1);
	}
	@Test
	public void test_rdb_0203_get_item_size_bigger_than_1M_qa() {
		//data1 area equals key different
		String key1 = "rdb_0203_key_1";
		String value = "";
		for(int i=0;i<998;i++){
			value += "1";
		}//value 1K
		List value1 = new Vector();
		for(int i=0;i<900;i++){
		value1.add(value);
		}
		tairManager.delete(namespace, key1);

		ResultCode resultCode = tairManager.addItems(namespace, key1, value1,10000000, 0, 0);
		System.out.println(resultCode.getCode());
		Assert.assertTrue(resultCode.isSuccess());
		Assert.assertEquals(ResultCode.SUCCESS, resultCode);
	
		resultCode = tairManager.addItems(namespace, key1, value1,10000000, 0, 0);
		System.out.println(resultCode.getCode());
		Assert.assertTrue(resultCode.isSuccess());
		Assert.assertEquals(ResultCode.SUCCESS, resultCode);
        
		value1.clear();
		for(int i=0;i<1000;i++){
			value1.add(value);
			}
		Result<DataEntry> result = tairManager.getItems(namespace, key1,0,1025);
		Assert.assertTrue(result.isSuccess());
		assertEquals(value1, ((DataEntry) result.getValue()).getValue());
		assertEquals(ResultCode.PARTITEM, result.getRc());
		
        //clear data
		tairManager.delete(namespace, key1);
	}
	@Test
	public void test_rdb_0204_one_million_item_get_last_ten_Item_qa() throws InterruptedException {
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
		value.clear();
		for(int i =9990;i<10000;i++){
			value.add(i);
		}
		Result<DataEntry> result = tairManager.getItems(namespace, key,-10,10);
		Assert.assertTrue(result.isSuccess());
		assertEquals(value, ((DataEntry) result.getValue()).getValue());
        //clear data
		tairManager.delete(namespace, key);
        
	}
	@Test
	public void test_rdb_0205_get_32k_data_qa() throws InterruptedException {
		//data1
		String key1 = "rdb_0105_key_1";
		String value = "";
		for(int i=0;i<32*1024-3;i++){
			value += "1";
		}//value 63K
		List value1 = new Vector();
		value1.add(value);
		tairManager.delete(namespace, key1);
		for(int i=0;i<100;i++){
			ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 1000000, 0, 0);
			Assert.assertTrue(resultCode.isSuccess());
        	Assert.assertEquals(ResultCode.SUCCESS,resultCode);
		}
        
        //verify get function
        Result<DataEntry> result = tairManager.getItems(namespace, key1,0,TairConstant.ITEM_ALL);
        
        assertTrue(result.isSuccess());
    	assertEquals(ResultCode.PARTITEM, result.getRc());
    	value1.clear();
    	for(int i=0;i<30;i++){
			value1.add(value);
		}//value 63K
		assertEquals(value1, ((DataEntry) result.getValue()).getValue());
		assertEquals(100, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key1, ((DataEntry) result.getValue()).getKey());

        //clear data
		tairManager.delete(namespace, key1);
        
	}
}
