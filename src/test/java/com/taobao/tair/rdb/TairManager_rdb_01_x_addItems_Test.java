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

public class TairManager_rdb_01_x_addItems_Test extends BaseTest {

	private int namespace = 1;
	@Test
	public void test_rdb_0101_data_smaller_than_32k_qa() {
		//data1
		String key1 = "rdb_0101_key_1";
		String value = "";
		for(int i=0;i<32*1024-3;i++){
			value += "1";
		}//value 63K
		List value1 = new Vector();
		value1.add(value);
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 5, 0, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
        //verify get function
        Result<DataEntry> result = tairManager.getItems(namespace, key1,0,1);
        assertTrue(result.isSuccess());
		assertEquals(value1, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key1, ((DataEntry) result.getValue()).getKey());

        //clear data
		tairManager.delete(namespace, key1);
        
	}
	@Test
	public void test_rdb_0102_data_equals_32k_qa() {
		//data1 
		String key1 = "rdb_0102_key_1";
		String value = "";
		for(int i=0;i<32*1024-2;i++){
			value += "1";
		}//value 64K
		List value1 = new Vector();
		value1.add(value);
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 5, 0, 5);
        Assert.assertFalse(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.ITEMTOLARGE,resultCode);
        
        //verify get function
        Result<DataEntry> result = tairManager.getItems(namespace, key1,0,1);
        assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());

        //clear data
		tairManager.delete(namespace, key1);
        
	}
	@Test
	public void test_rdb_0103_data_bigger_than_32k_qa() {
		//data1 area equals key different
		String key1 = "rdb_0103_key_1";
		String value = "";
		for(int i=0;i<32*1024-1;i++){
			value += "1";
		}//value 64K
		List value1 = new Vector();
		value1.add(value);
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 5, 0, 5);
        Assert.assertFalse(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.ITEMTOLARGE,resultCode);
        
        //verify get function
        Result<DataEntry> result = tairManager.getItems(namespace, key1,0,1);
        assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());

        //clear data
		tairManager.delete(namespace, key1);
        
	}
	@Test
	public void test_rdb_0104_no_data_dup_item_one_item_bigger_than_32k_qa() {
		//data1 area equals key different
		String key1 = "rdb_0104_key_1";
		String value = "";
		for(int i=0;i<32*1024-1;i++){
			value += "1";
		}//value 64K
		List value1 = new Vector();
		value1.add(1);
		value1.add((long)2);
		value1.add(value);
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 5, 0, 5);
        Assert.assertFalse(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.ITEMTOLARGE,resultCode);
        
        //verify get function
        Result<DataEntry> result = tairManager.getItems(namespace, key1,0,1);
        assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());

        //clear data
		tairManager.delete(namespace, key1);
        
	}
	@Test
	public void test_rdb_0105_data_exist_dup_item_one_item_bigger_than_32k_qa() {
		//data1 area equals key different
		String key1 = "rdb_0105_key_1";
		List value1 = new Vector();
		value1.add(1);
		value1.add((long)2);
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 3, 0, 0);
        
        Result<DataEntry> result = tairManager.getItems(namespace, key1,0,3);
        assertTrue(result.isSuccess());
		assertEquals(value1, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key1, ((DataEntry) result.getValue()).getKey());
		
        List value2 = new Vector();
		value2.add(1);
		value2.add((long)2);
		String value = "";
		for(int i=0;i<32*1024-1;i++){
			value += "1";
		}//value 64K
		value2.add(value);
        resultCode = tairManager.addItems(namespace, key1, value2, 3, 0, 5);
        Assert.assertFalse(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.ITEMTOLARGE,resultCode);
        
        //verify get function
        result = tairManager.getItems(namespace, key1,0,3);
        assertTrue(result.isSuccess());
		assertEquals(value1, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key1, ((DataEntry) result.getValue()).getKey());

        //clear data
		tairManager.delete(namespace, key1);
        
	}
	//case 06 07 08 09 imply in TairManager_10_x_addItems_Test (19 20 21 22)
	//case 10 11 imply in TairManager_14_x_getItemsCount_Test (09 10)
	@Test
	public void test_rdb_0112_data_exist_size_127M_add_item_size_1M_qa() {
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
		Assert.assertFalse(resultCode.isSuccess());
		Assert.assertEquals(ResultCode.ITEMSIZEERROR,resultCode);

        //clear data
		tairManager.delete(namespace, key1);
	}
}
