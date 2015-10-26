package com.taobao.tair.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import junit.framework.Assert;

import org.junit.Test;

import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;
import com.taobao.tair.etc.TairConstant;

public class TairManager_16_x_interface_allType_Test extends BaseTest {
	private int namespace = 1;
	
	@Test
	public void test_1601_basic_interface_different_type_String_type_qa() {
		//step 1 add String value
		String key1 = "1601_key_1";
		String value1 = "1";
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.put(namespace, key1, value1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
		String key2 = "1602_key_2";
		ArrayList value2 = new ArrayList();
		value2.add(2);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.put(namespace, key2, value2);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 add List value
		String key3 = "1602_key_3";
		Hashtable value3 = new Hashtable();
		value3.put(key3, "3");
		tairManager.delete(namespace, key3);
        resultCode = tairManager.put(namespace, key3, value3);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 4 get String value
        Result<DataEntry> result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 5 get String value
        result = tairManager.get(namespace, key2);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
        
      //step 6 get String value
        result = tairManager.get(namespace, key3);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value3,((DataEntry)result.getValue()).getValue());
        
      //step 7 get String value
        List keys = new ArrayList();
        keys.add(key1);
        keys.add(key2);
        keys.add(key3);
        Result<List<DataEntry>> results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,results.getRc());
        Assert.assertEquals(3,results.getValue().size());
        String valueAll = value1.toString()+value2.toString()+value3.toString();
        Assert.assertTrue(valueAll.contains(results.getValue().get(0).getValue().toString()));
        Assert.assertTrue(valueAll.contains(results.getValue().get(1).getValue().toString()));
        Assert.assertTrue(valueAll.contains(results.getValue().get(2).getValue().toString()));
        
      //step 8 delete String value
        resultCode = tairManager.delete(namespace, key1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
        //step 9 mdelete String value
        resultCode = tairManager.mdelete(namespace, keys);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,results.getRc());
        
	}
	@Test
	public void test_1602_Item_interface_different_type_String_type_qa() {
		//step 1 add String value
		String key1 = "1601_key_1";
		List value1 = new ArrayList();
		value1.add("1");
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
		String key2 = "1602_key_2";
		ArrayList value2 = new ArrayList();
		value2.add((long)1);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.addItems(namespace, key2, value2, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 get String value
        Result<DataEntry> result = tairManager.getItems(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 4 get String value
        result = tairManager.getItems(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
              
      //step 5 getItemCount String value
        Result<Integer> count = tairManager.getItemCount(namespace, key1);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(1,count.getValue().longValue());
      
        //step 6 getAndRemove String value
        result = tairManager.getAndRemove(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        result = tairManager.getItems(namespace, key1,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
      //step 7 delete String value
        resultCode = tairManager.removeItems(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        result = tairManager.getItems(namespace, key2,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
     
	}
	@Test
	public void test_1603_count_interface_different_type_String_type_qa() {
		//step 1 incr String key
		String key = "1601_key_1";
		int value = 1;
		tairManager.delete(namespace, key);
		Result<Integer> count = tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 2 incr List value
		count =  tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(5,count.getValue().intValue());
        
      //step 3 decr String key
		count = tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 4 decr List value
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(3,count.getValue().intValue());
        
        //setp 5 delete data
        tairManager.delete(value, key);
        
        //step 6 incr String data
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(2,count.getValue().intValue());
       
        //setp 7 delete data
        tairManager.delete(value, key);
    
	}
	
	@Test
	public void test_1604_basic_interface_different_type_boolean_type_qa() {
		//step 1 add String value
		boolean key1 = true;
		boolean value1 = true;
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.put(namespace, key1, value1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        boolean key2 = false;
		LinkedList value2 = new LinkedList();
		value2.add(1);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.put(namespace, key2, value2);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
        
      //step 3 get String value
        Result<DataEntry> result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 4 get String value
        result = tairManager.get(namespace, key2);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
        
      //step 7 get String value
        List keys = new ArrayList();
        keys.add(key1);
        keys.add(key2);
        Result<List<DataEntry>> results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,results.getRc());
        Assert.assertEquals(2,results.getValue().size());
        String valueAll = value1+value2.toString();
        Assert.assertTrue(valueAll.contains(results.getValue().get(0).getValue().toString()));
        Assert.assertTrue(valueAll.contains(results.getValue().get(1).getValue().toString()));
        
      //step 8 delete String value
        resultCode = tairManager.delete(namespace, key1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
        //step 9 mdelete String value
        resultCode = tairManager.mdelete(namespace, keys);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,results.getRc());
        
	}
	@Test
	public void test_1605_Item_interface_different_type_boolean_type_qa() {
		//step 1 add String value
		boolean key1 = true;
		List value1 = new ArrayList();
		value1.add(3.2);
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        boolean key2 = false;
		ArrayList value2 = new ArrayList();
		value2.add(1);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.addItems(namespace, key2, value2, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 get String value
        Result<DataEntry> result = tairManager.getItems(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 4 get String value
        result = tairManager.getItems(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
              
      //step 5 getItemCount String value
        Result<Integer> count = tairManager.getItemCount(namespace, key1);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(1,count.getValue().longValue());
      
        //step 6 getAndRemove String value
        result = tairManager.getAndRemove(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        result = tairManager.getItems(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
      //step 7 delete String value
        resultCode = tairManager.removeItems(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        result = tairManager.getItems(namespace, key2,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
     
	}
	@Test
	public void test_1606_count_interface_different_type_boolean_type_qa() {
		//step 1 incr String key
		boolean key = true;
		int value = 1;
		tairManager.delete(namespace, key);
		Result<Integer> count = tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 2 incr List value
		count =  tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(5,count.getValue().intValue());
        
      //step 3 decr String key
		count = tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 4 decr List value
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(3,count.getValue().intValue());
        
        //setp 5 delete data
        tairManager.delete(value, key);
        
        //step 6 incr String data
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(2,count.getValue().intValue());
       
        //setp 7 delete data
        tairManager.delete(value, key);
    
	}
	@Test
	public void test_1607_basic_interface_different_type_short_type_qa() {
		//step 1 add String value
		short key1 = 1;
		short value1 = 1;
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.put(namespace, key1, value1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        short key2 = 2;
		Vector value2 = new Vector();
		value2.add(1);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.put(namespace, key2, value2);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 add List value
        short key3 = 0;
		HashMap value3 = new HashMap();
		value3.put(key3, "2");
		tairManager.delete(namespace, key3);
        resultCode = tairManager.put(namespace, key3, value3);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 4 get String value
        Result<DataEntry> result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 5 get String value
        result = tairManager.get(namespace, key2);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
        
      //step 6 get String value
        result = tairManager.get(namespace, key3);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value3,((DataEntry)result.getValue()).getValue());
        
      //step 7 get String value
        List keys = new ArrayList();
        keys.add(key1);
        keys.add(key2);
        keys.add(key3);
        Result<List<DataEntry>> results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,results.getRc());
        Assert.assertEquals(3,results.getValue().size());
        String valueAll = String.valueOf(value1)+value2.toString()+value3.toString();
        Assert.assertTrue(valueAll.contains(results.getValue().get(0).getValue().toString()));
        Assert.assertTrue(valueAll.contains(results.getValue().get(1).getValue().toString()));
        Assert.assertTrue(valueAll.contains(results.getValue().get(2).getValue().toString()));
        
      //step 8 delete String value
        resultCode = tairManager.delete(namespace, key1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
        //step 9 mdelete String value
        resultCode = tairManager.mdelete(namespace, keys);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,results.getRc());
        
	}
	@Test
	public void test_1608_Item_interface_different_type_short_type_qa() {
		//step 1 add String value
		short key1 = 1;
		List value1 = new ArrayList();
		value1.add("1");
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 5, 1, 5);
//        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        short key2 = 2;
		ArrayList value2 = new ArrayList();
		value2.add(1);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.addItems(namespace, key2, value2, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 get String value
        Result<DataEntry> result = tairManager.getItems(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 4 get String value
        result = tairManager.getItems(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
              
      //step 5 getItemCount String value
        Result<Integer> count = tairManager.getItemCount(namespace, key1);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(1,count.getValue().longValue());
      
        //step 6 getAndRemove String value
        result = tairManager.getAndRemove(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        result = tairManager.getItems(namespace, key1,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
      //step 7 delete String value
        resultCode = tairManager.removeItems(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        result = tairManager.getItems(namespace, key2,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
     
	}
	@Test
	public void test_1609_count_interface_different_type_String_type_qa() {
		//step 1 incr String key
		short key = 1;
		int value = 1;
		tairManager.delete(namespace, key);
		Result<Integer> count = tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 2 incr List value
		count =  tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(5,count.getValue().intValue());
        
      //step 3 decr String key
		count = tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 4 decr List value
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(3,count.getValue().intValue());
        
        //setp 5 delete data
        tairManager.delete(value, key);
        
        //step 6 incr String data
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(2,count.getValue().intValue());
       
        //setp 7 delete data
        tairManager.delete(value, key);
    
	}
	@Test
	public void test_1610_basic_interface_different_type_char_type_qa() {
		//step 1 add String value
		char key1 = 'a';
		char value1 = 'a';
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.put(namespace, key1, value1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        char key2 = 'b';
		Stack value2 = new Stack();
		value2.add(1);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.put(namespace, key2, value2);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 add List value
        char key3 = 'c';
		TreeMap value3 = new TreeMap();
		value3.put(key3, "2");
		tairManager.delete(namespace, key3);
        resultCode = tairManager.put(namespace, key3, value3);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 4 get String value
        Result<DataEntry> result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 5 get String value
        result = tairManager.get(namespace, key2);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
        
      //step 6 get String value
        result = tairManager.get(namespace, key3);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value3,((DataEntry)result.getValue()).getValue());
        
      //step 7 get String value
        List keys = new ArrayList();
        keys.add(key1);
        keys.add(key2);
        keys.add(key3);
        Result<List<DataEntry>> results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,results.getRc());
        Assert.assertEquals(3,results.getValue().size());
        
        String valueAll = String.valueOf(value1)+value2.toString()+value3.toString();
        Assert.assertTrue(valueAll.contains(results.getValue().get(0).getValue().toString()));
        Assert.assertTrue(valueAll.contains(results.getValue().get(1).getValue().toString()));
        Assert.assertTrue(valueAll.contains(results.getValue().get(2).getValue().toString()));
        
      //step 8 delete String value
        resultCode = tairManager.delete(namespace, key1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
        //step 9 mdelete String value
        resultCode = tairManager.mdelete(namespace, keys);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,results.getRc());
        
	}
	@Test
	public void test_1611_Item_interface_different_type_char_type_qa() {
		//step 1 add String value
		char key1 = '.';
		List value1 = new ArrayList();
		value1.add(1.1);
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        char key2 = '>';
		ArrayList value2 = new ArrayList();
		value2.add((long)1);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.addItems(namespace, key2, value2, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 get String value
        Result<DataEntry> result = tairManager.getItems(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 4 get String value
        result = tairManager.getItems(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
              
      //step 5 getItemCount String value
        Result<Integer> count = tairManager.getItemCount(namespace, key1);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(1,count.getValue().longValue());
      
        //step 6 getAndRemove String value
        result = tairManager.getAndRemove(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        result = tairManager.getItems(namespace, key1,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
      //step 7 delete String value
        resultCode = tairManager.removeItems(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        result = tairManager.getItems(namespace, key2,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
     
	}
	@Test
	public void test_1612_count_interface_different_type_char_type_qa() {
		//step 1 incr String key
		char key = '1';
		int value = 1;
		tairManager.delete(namespace, key);
		Result<Integer> count = tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 2 incr List value
		count =  tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(5,count.getValue().intValue());
        
      //step 3 decr String key
		count = tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 4 decr List value
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(3,count.getValue().intValue());
        
        //setp 5 delete data
        tairManager.delete(value, key);
        
        //step 6 incr String data
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(2,count.getValue().intValue());
       
        //setp 7 delete data
        tairManager.delete(value, key);
    
	}
	@Test
	public void test_1613_basic_interface_different_type_double_type_qa() {
		//step 1 add String value
		double key1 = 1.1;
		double value1 = 1.22;
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.put(namespace, key1, value1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        double key2 = 1.2;
		TreeSet value2 = new TreeSet();
		value2.add(1);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.put(namespace, key2, value2);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 add List value
        double key3 = 1.3;
		TreeMap value3 = new TreeMap();
		value3.put(key3, "2");
		tairManager.delete(namespace, key3);
        resultCode = tairManager.put(namespace, key3, value3);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 4 get String value
        Result<DataEntry> result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 5 get String value
        result = tairManager.get(namespace, key2);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
        
      //step 6 get String value
        result = tairManager.get(namespace, key3);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value3,((DataEntry)result.getValue()).getValue());
        
      //step 7 get String value
        List keys = new ArrayList();
        keys.add(key1);
        keys.add(key2);
        keys.add(key3);
        Result<List<DataEntry>> results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,results.getRc());
        Assert.assertEquals(3,results.getValue().size());
        String valueAll = String.valueOf(value1)+value2.toString()+value3.toString();
        Assert.assertTrue(valueAll.contains(results.getValue().get(0).getValue().toString()));
        Assert.assertTrue(valueAll.contains(results.getValue().get(1).getValue().toString()));
        Assert.assertTrue(valueAll.contains(results.getValue().get(2).getValue().toString()));
        
      //step 8 delete String value
        resultCode = tairManager.delete(namespace, key1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
        //step 9 mdelete String value
        resultCode = tairManager.mdelete(namespace, keys);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,results.getRc());
        
	}
	@Test
	public void test_1614_Item_interface_different_type_double_type_qa() {
		//step 1 add String value
		double key1 = 1.5;
		List value1 = new ArrayList();
		value1.add(1.1);
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        double key2 = 1.6;
		ArrayList value2 = new ArrayList();
		value2.add((long)1);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.addItems(namespace, key2, value2, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 get String value
        Result<DataEntry> result = tairManager.getItems(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 4 get String value
        result = tairManager.getItems(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
              
      //step 5 getItemCount String value
        Result<Integer> count = tairManager.getItemCount(namespace, key1);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(1,count.getValue().longValue());
      
        //step 6 getAndRemove String value
        result = tairManager.getAndRemove(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        result = tairManager.getItems(namespace, key1,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
      //step 7 delete String value
        resultCode = tairManager.removeItems(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        result = tairManager.getItems(namespace, key2,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
     
	}
	@Test
	public void test_1615_count_interface_different_type_double_type_qa() {
		//step 1 incr String key
		double key = 1.8;
		int value = 1;
		tairManager.delete(namespace, key);
		Result<Integer> count = tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 2 incr List value
		count =  tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(5,count.getValue().intValue());
        
      //step 3 decr String key
		count = tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 4 decr List value
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(3,count.getValue().intValue());
        
        //setp 5 delete data
        tairManager.delete(value, key);
        
        //step 6 incr String data
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(2,count.getValue().intValue());
       
        //setp 7 delete data
        tairManager.delete(value, key);
    
	}
	@Test
	public void test_1616_basic_interface_different_type_int_type_qa() {
		//step 1 add String value
		int key1 = 1;
		int value1 = 1;
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.put(namespace, key1, value1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        int key2 = 2;
		TreeSet value2 = new TreeSet();
		value2.add(1);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.put(namespace, key2, value2);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 add List value
        int key3 = 3;
		TreeMap value3 = new TreeMap();
		value3.put(key3, "2");
		tairManager.delete(namespace, key3);
        resultCode = tairManager.put(namespace, key3, value3);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 4 get String value
        Result<DataEntry> result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 5 get String value
        result = tairManager.get(namespace, key2);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
        
      //step 6 get String value
        result = tairManager.get(namespace, key3);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value3,((DataEntry)result.getValue()).getValue());
        
      //step 7 get String value
        List keys = new ArrayList();
        keys.add(key1);
        keys.add(key2);
        keys.add(key3);
        Result<List<DataEntry>> results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,results.getRc());
        Assert.assertEquals(3,results.getValue().size());
        String valueAll = String.valueOf(value1)+value2.toString()+value3.toString();
        Assert.assertTrue(valueAll.contains(results.getValue().get(0).getValue().toString()));
        Assert.assertTrue(valueAll.contains(results.getValue().get(1).getValue().toString()));
        Assert.assertTrue(valueAll.contains(results.getValue().get(2).getValue().toString()));
        
      //step 8 delete String value
        resultCode = tairManager.delete(namespace, key1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
        //step 9 mdelete String value
        resultCode = tairManager.mdelete(namespace, keys);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,results.getRc());
        
	}
	@Test
	public void test_1617_Item_interface_different_type_int_type_qa() {
		//step 1 add String value
		int key1 = 1;
		List value1 = new ArrayList();
		value1.add(1.1);
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        int key2 = 2;
		ArrayList value2 = new ArrayList();
		value2.add((long)1);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.addItems(namespace, key2, value2, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 get String value
        Result<DataEntry> result = tairManager.getItems(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 4 get String value
        result = tairManager.getItems(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
              
      //step 5 getItemCount String value
        Result<Integer> count = tairManager.getItemCount(namespace, key1);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(1,count.getValue().longValue());
      
        //step 6 getAndRemove String value
        result = tairManager.getAndRemove(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        result = tairManager.getItems(namespace, key1,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
      //step 7 delete String value
        resultCode = tairManager.removeItems(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        result = tairManager.getItems(namespace, key2,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
     
	}
	@Test
	public void test_1618_count_interface_different_type_int_type_qa() {
		//step 1 incr String key
		int key = 600;
		int value = 1;
		tairManager.delete(namespace, key);
		Result<Integer> count = tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 2 incr List value
		count =  tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(5,count.getValue().intValue());
        
      //step 3 decr String key
		count = tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 4 decr List value
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(3,count.getValue().intValue());
        
        //setp 5 delete data
        tairManager.delete(value, key);
        
        //step 6 incr String data
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(2,count.getValue().intValue());
       
        //setp 7 delete data
        tairManager.delete(value, key);
    
	}
	@Test
	public void test_1619_basic_interface_different_type_int_type_qa() {
		//step 1 add String value
		byte key1 = 1;
		byte value1 = 1;
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.put(namespace, key1, value1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        byte key2 = 2;
		TreeSet value2 = new TreeSet();
		value2.add(1);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.put(namespace, key2, value2);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 add List value
        byte key3 = 3;
		TreeMap value3 = new TreeMap();
		value3.put(key3, "2");
		tairManager.delete(namespace, key3);
        resultCode = tairManager.put(namespace, key3, value3);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 4 get String value
        Result<DataEntry> result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 5 get String value
        result = tairManager.get(namespace, key2);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
        
      //step 6 get String value
        result = tairManager.get(namespace, key3);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value3,((DataEntry)result.getValue()).getValue());
        
      //step 7 get String value
        List keys = new ArrayList();
        keys.add(key1);
        keys.add(key2);
        keys.add(key3);
        Result<List<DataEntry>> results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,results.getRc());
        Assert.assertEquals(3,results.getValue().size());
        String valueAll = String.valueOf(value1)+value2.toString()+value3.toString();
        Assert.assertTrue(valueAll.contains(results.getValue().get(0).getValue().toString()));
        Assert.assertTrue(valueAll.contains(results.getValue().get(1).getValue().toString()));
        Assert.assertTrue(valueAll.contains(results.getValue().get(2).getValue().toString()));
        
      //step 8 delete String value
        resultCode = tairManager.delete(namespace, key1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
        //step 9 mdelete String value
        resultCode = tairManager.mdelete(namespace, keys);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,results.getRc());
        
	}
	@Test
	public void test_1620_Item_interface_different_type_byte_type_qa() {
		//step 1 add String value
		byte key1 = 1;
		List value1 = new ArrayList();
		value1.add(1.1);
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        byte key2 = 2;
		ArrayList value2 = new ArrayList();
		value2.add((long)1);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.addItems(namespace, key2, value2, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 get String value
        Result<DataEntry> result = tairManager.getItems(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 4 get String value
        result = tairManager.getItems(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
              
      //step 5 getItemCount String value
        Result<Integer> count = tairManager.getItemCount(namespace, key1);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(1,count.getValue().longValue());
      
        //step 6 getAndRemove String value
        result = tairManager.getAndRemove(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        result = tairManager.getItems(namespace, key1,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
      //step 7 delete String value
        resultCode = tairManager.removeItems(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        result = tairManager.getItems(namespace, key2,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
     
	}
	@Test
	public void test_1621_count_interface_different_type_byte_type_qa() {
		//step 1 incr String key
		byte key =1;
		int value = 1;
		tairManager.delete(namespace, key);
		Result<Integer> count = tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 2 incr List value
		count =  tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(5,count.getValue().intValue());
        
      //step 3 decr String key
		count = tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 4 decr List value
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(3,count.getValue().intValue());
        
        //setp 5 delete data
        tairManager.delete(value, key);
        
        //step 6 incr String data
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(2,count.getValue().intValue());
       
        //setp 7 delete data
        tairManager.delete(value, key);
    
	}
	
	@Test
	public void test_1622_basic_interface_different_type_ArrayList_type_qa() {
		//step 1 add String value
		ArrayList key1 = new ArrayList();
		key1.add("1");
		String value1 = "1";
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.put(namespace, key1, value1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        ArrayList key2 =new ArrayList();
        key2.add(key1);
		TreeSet value2 = new TreeSet();
		value2.add(2);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.put(namespace, key2, value2);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 add List value
        ArrayList key3 = new ArrayList();
        key3.add(key2);
		TreeMap value3 = new TreeMap();
		value3.put(key3, "2");
		tairManager.delete(namespace, key3);
        resultCode = tairManager.put(namespace, key3, value3);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 4 get String value
        Result<DataEntry> result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 5 get String value
        result = tairManager.get(namespace, key2);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
        
      //step 6 get String value
        result = tairManager.get(namespace, key3);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value3.toString(),((DataEntry)result.getValue()).getValue().toString());
        
      //step 7 get String value
        List keys = new ArrayList();
        keys.add(key1);
        keys.add(key2);
        keys.add(key3);
        Result<List<DataEntry>> results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,results.getRc());
        Assert.assertEquals(3,results.getValue().size());
        Assert.assertTrue(results.getValue().toString().contains(value1.toString())&&
        		results.getValue().toString().contains(value2.toString())&&
        		results.getValue().toString().contains(value3.toString()));
        
      //step 8 delete String value
        resultCode = tairManager.delete(namespace, key1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
        //step 9 mdelete String value
        resultCode = tairManager.mdelete(namespace, keys);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,results.getRc());
        
	}
	@Test
	public void test_1623_Item_interface_different_type_ArrayList_type_qa() {
		//step 1 add String value
		ArrayList key1 = new ArrayList();
		key1.add(1.1);
		List value1 = new ArrayList();
		value1.add(1.1);
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        ArrayList key2 = new ArrayList(0);
		ArrayList value2 = new ArrayList();
		value2.add((long)1);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.addItems(namespace, key2, value2, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 get String value
        Result<DataEntry> result = tairManager.getItems(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 4 get String value
        result = tairManager.getItems(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
              
      //step 5 getItemCount String value
        Result<Integer> count = tairManager.getItemCount(namespace, key1);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(1,count.getValue().longValue());
      
        //step 6 getAndRemove String value
        result = tairManager.getAndRemove(namespace, key1,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        result = tairManager.get(namespace, key1);
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals(ResultCode.TYPENOTMATCH,result.getRc());
        
      //step 7 delete String value
        resultCode = tairManager.removeItems(namespace, key2,0,1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        result = tairManager.get(namespace, key2);
        Assert.assertFalse(result.isSuccess());
        Assert.assertEquals(ResultCode.TYPENOTMATCH,result.getRc());
     
	}
	@Test
	public void test_1624_count_interface_different_type_ArrayList_type_qa() {
		//step 1 incr String key
		ArrayList key = new ArrayList(1);
		int value = 1;
		tairManager.delete(namespace, key);
		Result<Integer> count = tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 2 incr List value
		count =  tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(5,count.getValue().intValue());
        
      //step 3 decr String key
		count = tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 4 decr List value
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(3,count.getValue().intValue());
        
        //setp 5 delete data
        tairManager.delete(value, key);
        
        //step 6 incr String data
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(2,count.getValue().intValue());
       
        //setp 7 delete data
        tairManager.delete(value, key);
    
	}
	@Test
	public void test_1625_basic_interface_different_type_LinkedList_type_qa() {
		//step 1 add String value
		LinkedList key1 = new LinkedList();
		key1.add("1");
		String value1 = "1";
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.put(namespace, key1, value1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        LinkedList key2 =new LinkedList();
        key2.add(key1);
        LinkedList value2 = new LinkedList();
		value2.add(2);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.put(namespace, key2, value2);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 add List value
        ArrayList key3 = new ArrayList();
        key3.add(key2);
		TreeMap value3 = new TreeMap();
		value3.put(key3, "2");
		tairManager.delete(namespace, key3);
        resultCode = tairManager.put(namespace, key3, value3);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 4 get String value
        Result<DataEntry> result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 5 get String value
        result = tairManager.get(namespace, key2);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
        
      //step 6 get String value
        result = tairManager.get(namespace, key3);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value3.toString(),((DataEntry)result.getValue()).getValue().toString());
        
      //step 7 get String value
        List keys = new ArrayList();
        keys.add(key1);
        keys.add(key2);
        keys.add(key3);
        Result<List<DataEntry>> results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,results.getRc());
        Assert.assertEquals(3,results.getValue().size());
        Assert.assertTrue(results.getValue().toString().contains(value1.toString())&&
        		results.getValue().toString().contains(value2.toString())&&
        		results.getValue().toString().contains(value3.toString()));
        
      //step 8 delete String value
        resultCode = tairManager.delete(namespace, key1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
        //step 9 mdelete String value
        resultCode = tairManager.mdelete(namespace, keys);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,results.getRc());
        
	}
	@Test
	public void test_1626_Item_interface_different_type_LinkedList_type_qa() {
		//step 1 add String value
		LinkedList key1 = new LinkedList();
		key1.add(1.1);
		List value1 = new ArrayList();
		value1.add(1.1);
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        LinkedList key2 = new LinkedList();
        key2.add(0);
        LinkedList value2 = new LinkedList();
		value2.add((long)1);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.addItems(namespace, key2, value2, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 get String value
        Result<DataEntry> result = tairManager.getItems(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 4 get String value
        result = tairManager.getItems(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
              
      //step 5 getItemCount String value
        Result<Integer> count = tairManager.getItemCount(namespace, key1);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(1,count.getValue().longValue());
      
        //step 6 getAndRemove String value
        result = tairManager.getAndRemove(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        result = tairManager.getItems(namespace, key1,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
      //step 7 delete String value
        result = tairManager.getAndRemove(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
        result = tairManager.getItems(namespace, key2,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
     
	}
	@Test
	public void test_1627_count_interface_different_type_LinkedList_type_qa() {
		//step 1 incr String key
		LinkedList key = new LinkedList();
		key.add(1);
		int value = 1;
		tairManager.delete(namespace, key);
		Result<Integer> count = tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 2 incr List value
		count =  tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(5,count.getValue().intValue());
        
      //step 3 decr String key
		count = tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 4 decr List value
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(3,count.getValue().intValue());
        
        //setp 5 delete data
        tairManager.delete(value, key);
        
        //step 6 incr String data
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(2,count.getValue().intValue());
       
        //setp 7 delete data
        tairManager.delete(value, key);
    
	}
	@Test
	public void test_1628_basic_interface_different_type_Vector_type_qa() {
		//step 1 add String value
		Vector key1 = new Vector();
		key1.add("1");
		String value1 = "1";
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.put(namespace, key1, value1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        Vector key2 =new Vector();
        key2.add(key1);
        Vector value2 = new Vector();
		value2.add(2);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.put(namespace, key2, value2);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 add List value
        Vector key3 = new Vector();
        key3.add(key2);
		TreeMap value3 = new TreeMap();
		value3.put(key3, "2");
		tairManager.delete(namespace, key3);
        resultCode = tairManager.put(namespace, key3, value3);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 4 get String value
        Result<DataEntry> result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 5 get String value
        result = tairManager.get(namespace, key2);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
        
      //step 6 get String value
        result = tairManager.get(namespace, key3);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value3.toString(),((DataEntry)result.getValue()).getValue().toString());
        
      //step 7 get String value
        List keys = new ArrayList();
        keys.add(key1);
        keys.add(key2);
        keys.add(key3);
        Result<List<DataEntry>> results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,results.getRc());
        Assert.assertEquals(3,results.getValue().size());
        String valueAll = value1.toString()+value2.toString()+value3.toString();
        Assert.assertTrue(valueAll.contains(results.getValue().get(0).getValue().toString()));
        Assert.assertTrue(valueAll.contains(results.getValue().get(1).getValue().toString()));
        Assert.assertTrue(valueAll.contains(results.getValue().get(2).getValue().toString()));
        
      //step 8 delete String value
        resultCode = tairManager.delete(namespace, key1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
        //step 9 mdelete String value
        resultCode = tairManager.mdelete(namespace, keys);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,results.getRc());
        
	}
	@Test
	public void test_1629_Item_interface_different_type_Vector_type_qa() {
		//step 1 add String value
		Vector key1 = new Vector();
		key1.add(1.1);
		List value1 = new ArrayList();
		value1.add(1.1);
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        Vector key2 = new Vector(0);
		ArrayList value2 = new ArrayList();
		value2.add((long)1);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.addItems(namespace, key2, value2, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 get String value
        Result<DataEntry> result = tairManager.getItems(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 4 get String value
        result = tairManager.getItems(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
              
      //step 5 getItemCount String value
        Result<Integer> count = tairManager.getItemCount(namespace, key1);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(1,count.getValue().longValue());
      
        result = tairManager.getAndRemove(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        result = tairManager.getItems(namespace, key1,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
      //step 7 delete String value
        result = tairManager.getAndRemove(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
        result = tairManager.getItems(namespace, key2,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
     
	}
	@Test
	public void test_1630_count_interface_different_type_Vector_type_qa() {
		//step 1 incr String key
		Vector key = new Vector();
		key.add(1);
		int value = 1;
		tairManager.delete(namespace, key);
		Result<Integer> count = tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 2 incr List value
		count =  tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(5,count.getValue().intValue());
        
      //step 3 decr String key
		count = tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 4 decr List value
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(3,count.getValue().intValue());
        
        //setp 5 delete data
        tairManager.delete(value, key);
        
        //step 6 incr String data
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(2,count.getValue().intValue());
       
        //setp 7 delete data
        tairManager.delete(value, key);
    
	}
	@Test
	public void test_1631_basic_interface_different_type_HashSet_type_qa() {
		//step 1 add String value
		HashSet key1 = new HashSet();
		key1.add("1");
		String value1 = "1";
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.put(namespace, key1, value1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        HashSet key2 =new HashSet();
        key2.add(key1);
        Vector value2 = new Vector();
		value2.add(2);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.put(namespace, key2, value2);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 add List value
        HashSet key3 = new HashSet();
        key3.add(key2);
		TreeMap value3 = new TreeMap();
		value3.put(key3, "2");
		tairManager.delete(namespace, key3);
        resultCode = tairManager.put(namespace, key3, value3);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 4 get String value
        Result<DataEntry> result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 5 get String value
        result = tairManager.get(namespace, key2);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
        
      //step 6 get String value
        result = tairManager.get(namespace, key3);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value3.toString(),((DataEntry)result.getValue()).getValue().toString());
        
      //step 7 get String value
        List keys = new ArrayList();
        keys.add(key1);
        keys.add(key2);
        keys.add(key3);
        Result<List<DataEntry>> results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,results.getRc());
        Assert.assertEquals(3,results.getValue().size());
        String valueAll = value1.toString()+value2.toString()+value3.toString();
        Assert.assertTrue(valueAll.contains(results.getValue().get(0).getValue().toString()));
        Assert.assertTrue(valueAll.contains(results.getValue().get(1).getValue().toString()));
        Assert.assertTrue(valueAll.contains(results.getValue().get(2).getValue().toString()));
        
      //step 8 delete String value
        resultCode = tairManager.delete(namespace, key1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
        //step 9 mdelete String value
        resultCode = tairManager.mdelete(namespace, keys);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,results.getRc());
        
	}
	@Test
	public void test_1632_Item_interface_different_type_HashSet_type_qa() {
		//step 1 add String value
		HashSet key1 = new HashSet();
		key1.add(1.1);
		List value1 = new ArrayList();
		value1.add(1.1);
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        HashSet key2 = new HashSet(0);
		ArrayList value2 = new ArrayList();
		value2.add((long)1);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.addItems(namespace, key2, value2, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 get String value
        Result<DataEntry> result = tairManager.getItems(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 4 get String value
        result = tairManager.getItems(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
              
      //step 5 getItemCount String value
        Result<Integer> count = tairManager.getItemCount(namespace, key1);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(1,count.getValue().longValue());
      
        //step 6 getAndRemove String value
        result = tairManager.getAndRemove(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        result = tairManager.getItems(namespace, key1,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
      //step 7 delete String value
        result = tairManager.getAndRemove(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
        result = tairManager.getItems(namespace, key2,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
	}
	@Test
	public void test_1633_count_interface_different_type_HashSet_type_qa() {
		//step 1 incr String key
		HashSet key = new HashSet();
		key.add(1);
		int value = 1;
		tairManager.delete(namespace, key);
		Result<Integer> count = tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 2 incr List value
		count =  tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(5,count.getValue().intValue());
        
      //step 3 decr String key
		count = tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 4 decr List value
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(3,count.getValue().intValue());
        
        //setp 5 delete data
        tairManager.delete(value, key);
        
        //step 6 incr String data
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(2,count.getValue().intValue());
       
        //setp 7 delete data
        tairManager.delete(value, key);
    
	}
	@Test
	public void test_1634_basic_interface_different_type_TreeSet_type_qa() {
		//step 1 add String value
		TreeSet key1 = new TreeSet();
		key1.add("1");
		String value1 = "1";
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.put(namespace, key1, value1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        TreeSet key2 =new TreeSet();
        key2.add(key1);
        Vector value2 = new Vector();
		value2.add(2);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.put(namespace, key2, value2);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 add List value
        TreeSet key3 = new TreeSet();
        key3.add(key2);
		TreeMap value3 = new TreeMap();
		value3.put(key3, "2");
		tairManager.delete(namespace, key3);
        resultCode = tairManager.put(namespace, key3, value3);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 4 get String value
        Result<DataEntry> result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 5 get String value
        result = tairManager.get(namespace, key2);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
        
      //step 6 get String value
        result = tairManager.get(namespace, key3);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value3.toString(),((DataEntry)result.getValue()).getValue().toString());
        
      //step 7 get String value
        List keys = new ArrayList();
        keys.add(key1);
        keys.add(key2);
        keys.add(key3);
        Result<List<DataEntry>> results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,results.getRc());
        Assert.assertEquals(3,results.getValue().size());
        String valueAll = value1.toString()+value2.toString()+value3.toString();
        Assert.assertTrue(valueAll.contains(results.getValue().get(0).getValue().toString()));
        Assert.assertTrue(valueAll.contains(results.getValue().get(1).getValue().toString()));
        Assert.assertTrue(valueAll.contains(results.getValue().get(2).getValue().toString()));
        
      //step 8 delete String value
        resultCode = tairManager.delete(namespace, key1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
        //step 9 mdelete String value
        resultCode = tairManager.mdelete(namespace, keys);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,results.getRc());
        
	}
	@Test
	public void test_1635_Item_interface_different_type_TreeSet_type_qa() {
		//step 1 add String value
		TreeSet key1 = new TreeSet();
		key1.add(1.1);
		List value1 = new ArrayList();
		value1.add(1.1);
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        TreeSet key2 = new TreeSet();
        key2.add(0);
		ArrayList value2 = new ArrayList();
		value2.add((long)1);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.addItems(namespace, key2, value2, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 get String value
        Result<DataEntry> result = tairManager.getItems(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 4 get String value
        result = tairManager.getItems(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
              
      //step 5 getItemCount String value
        Result<Integer> count = tairManager.getItemCount(namespace, key1);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(1,count.getValue().longValue());
      
        //step 6 getAndRemove String value
        result = tairManager.getAndRemove(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        result = tairManager.getItems(namespace, key1,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
      //step 7 delete String value
        result = tairManager.getAndRemove(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
        result = tairManager.getItems(namespace, key2,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
     
	}
	@Test
	public void test_1636_count_interface_different_type_TreeSet_type_qa() {
		//step 1 incr String key
		TreeSet key = new TreeSet();
		key.add(1);
		int value = 1;
		tairManager.delete(namespace, key);
		Result<Integer> count = tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 2 incr List value
		count =  tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(5,count.getValue().intValue());
        
      //step 3 decr String key
		count = tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 4 decr List value
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(3,count.getValue().intValue());
        
        //setp 5 delete data
        tairManager.delete(value, key);
        
        //step 6 incr String data
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(2,count.getValue().intValue());
       
        //setp 7 delete data
        tairManager.delete(value, key);
    
	}
	@Test
	public void test_1637_basic_interface_different_type_Stack_type_qa() {
		//step 1 add String value
		Stack key1 = new Stack();
		key1.add("1");
		String value1 = "1";
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.put(namespace, key1, value1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        Stack key2 =new Stack();
        key2.add(key1);
        Vector value2 = new Vector();
		value2.add(2);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.put(namespace, key2, value2);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 add List value
        Stack key3 = new Stack();
        key3.add(key2);
		TreeMap value3 = new TreeMap();
		value3.put(key3, "2");
		tairManager.delete(namespace, key3);
        resultCode = tairManager.put(namespace, key3, value3);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 4 get String value
        Result<DataEntry> result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 5 get String value
        result = tairManager.get(namespace, key2);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
        
      //step 6 get String value
        result = tairManager.get(namespace, key3);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value3.toString(),((DataEntry)result.getValue()).getValue().toString());
        
      //step 7 get String value
        List keys = new ArrayList();
        keys.add(key1);
        keys.add(key2);
        keys.add(key3);
        Result<List<DataEntry>> results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,results.getRc());
        Assert.assertEquals(3,results.getValue().size());
        Assert.assertTrue(results.getValue().toString().contains(value1.toString())&&
        		results.getValue().toString().contains(value2.toString())&&
        		results.getValue().toString().contains(value3.toString()));
        
      //step 8 delete String value
        resultCode = tairManager.delete(namespace, key1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
        //step 9 mdelete String value
        resultCode = tairManager.mdelete(namespace, keys);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,results.getRc());
        
	}
	@Test
	public void test_1638_Item_interface_different_type_Stack_type_qa() {
		//step 1 add String value
		Stack key1 = new Stack();
		key1.add(1.1);
		List value1 = new ArrayList();
		value1.add(1.1);
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        TreeSet key2 = new TreeSet();
        key2.add(0);
		ArrayList value2 = new ArrayList();
		value2.add((long)1);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.addItems(namespace, key2, value2, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 get String value
        Result<DataEntry> result = tairManager.getItems(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 4 get String value
        result = tairManager.getItems(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
              
      //step 5 getItemCount String value
        Result<Integer> count = tairManager.getItemCount(namespace, key1);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(1,count.getValue().longValue());
      
        //step 6 getAndRemove String value
        result = tairManager.getAndRemove(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        result = tairManager.getItems(namespace, key1,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
      //step 7 delete String value
        result = tairManager.getAndRemove(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
        result = tairManager.getItems(namespace, key2,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
     
	}
	@Test
	public void test_1639_count_interface_different_type_Stack_type_qa() {
		//step 1 incr String key
		Stack key = new Stack();
		key.add(1);
		int value = 1;
		tairManager.delete(namespace, key);
		Result<Integer> count = tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 2 incr List value
		count =  tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(5,count.getValue().intValue());
        
      //step 3 decr String key
		count = tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 4 decr List value
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(3,count.getValue().intValue());
        
        //setp 5 delete data
        tairManager.delete(value, key);
        
        //step 6 incr String data
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(2,count.getValue().intValue());
       
        //setp 7 delete data
        tairManager.delete(value, key);
    
	}
	@Test
	public void test_1640_basic_interface_different_type_Hashtable_type_qa() {
		//step 1 add String value
		Hashtable key1 = new Hashtable();
		key1.put(1, "1");
		String value1 = "1";
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.put(namespace, key1, value1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
		Hashtable key2 = new Hashtable();
		key2.put(2, "2");
        Vector value2 = new Vector();
		value2.add(2);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.put(namespace, key2, value2);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 add List value
		Hashtable key3 = new Hashtable();
		key3.put(3, "3");
		TreeMap value3 = new TreeMap();
		value3.put(key3, "2");
		tairManager.delete(namespace, key3);
        resultCode = tairManager.put(namespace, key3, value3);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 4 get String value
        Result<DataEntry> result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 5 get String value
        result = tairManager.get(namespace, key2);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
        
      //step 6 get String value
        result = tairManager.get(namespace, key3);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value3.toString(),((DataEntry)result.getValue()).getValue().toString());
        
      //step 7 get String value
        List keys = new ArrayList();
        keys.add(key1);
        keys.add(key2);
        keys.add(key3);
        Result<List<DataEntry>> results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,results.getRc());
        Assert.assertEquals(3,results.getValue().size());
        String valueAll = value1.toString()+value2.toString()+value3.toString();
        Assert.assertTrue(valueAll.contains(results.getValue().get(0).getValue().toString()));
        Assert.assertTrue(valueAll.contains(results.getValue().get(1).getValue().toString()));
        Assert.assertTrue(valueAll.contains(results.getValue().get(2).getValue().toString()));
        
      //step 8 delete String value
        resultCode = tairManager.delete(namespace, key1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
        //step 9 mdelete String value
        resultCode = tairManager.mdelete(namespace, keys);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,results.getRc());
        
	}
	@Test
	public void test_1641_Item_interface_different_type_Hashtable_type_qa() {
		//step 1 add String value
		Hashtable key1 = new Hashtable();
		key1.put(1, "1");
		List value1 = new ArrayList();
		value1.add(1.1);
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        Hashtable key2 = new Hashtable();
		key2.put(2, "2");
		ArrayList value2 = new ArrayList();
		value2.add((long)1);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.addItems(namespace, key2, value2, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 get String value
        Result<DataEntry> result = tairManager.getItems(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 4 get String value
        result = tairManager.getItems(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
              
      //step 5 getItemCount String value
        Result<Integer> count = tairManager.getItemCount(namespace, key1);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(1,count.getValue().longValue());
      
        //step 6 getAndRemove String value
        result = tairManager.getAndRemove(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        result = tairManager.getItems(namespace, key1,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
      //step 7 delete String value
        result = tairManager.getAndRemove(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
        result = tairManager.getItems(namespace, key2,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
     
	}
	@Test
	public void test_1642_count_interface_different_type_Hashtable_type_qa() {
		//step 1 incr String key
		Hashtable key = new Hashtable();
		key.put(1, "1");
		int value = 1;
		tairManager.delete(namespace, key);
		Result<Integer> count = tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 2 incr List value
		count =  tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(5,count.getValue().intValue());
        
      //step 3 decr String key
		count = tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 4 decr List value
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(3,count.getValue().intValue());
        
        //setp 5 delete data
        tairManager.delete(value, key);
        
        //step 6 incr String data
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(2,count.getValue().intValue());
       
        //setp 7 delete data
        tairManager.delete(value, key);
    
	}
	@Test
	public void test_1643_basic_interface_different_type_HashMap_type_qa() {
		//step 1 add String value
		HashMap key1 = new HashMap();
		key1.put(1, "1");
		String value1 = "1";
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.put(namespace, key1, value1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        HashMap key2 = new HashMap();
		key2.put(2, "2");
        Vector value2 = new Vector();
		value2.add(2);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.put(namespace, key2, value2);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 add List value
        HashMap key3 = new HashMap();
		key3.put(3, "3");
		TreeMap value3 = new TreeMap();
		value3.put(key3, "2");
		tairManager.delete(namespace, key3);
        resultCode = tairManager.put(namespace, key3, value3);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 4 get String value
        Result<DataEntry> result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 5 get String value
        result = tairManager.get(namespace, key2);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
        
      //step 6 get String value
        result = tairManager.get(namespace, key3);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value3.toString(),((DataEntry)result.getValue()).getValue().toString());
        
      //step 7 get String value
        List keys = new ArrayList();
        keys.add(key1);
        keys.add(key2);
        keys.add(key3);
        Result<List<DataEntry>> results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,results.getRc());
        Assert.assertEquals(3,results.getValue().size());
        String valueAll = value1.toString()+value2.toString()+value3.toString();
        Assert.assertTrue(valueAll.contains(results.getValue().get(0).getValue().toString()));
        Assert.assertTrue(valueAll.contains(results.getValue().get(1).getValue().toString()));
        Assert.assertTrue(valueAll.contains(results.getValue().get(2).getValue().toString()));
        
      //step 8 delete String value
        resultCode = tairManager.delete(namespace, key1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
        //step 9 mdelete String value
        resultCode = tairManager.mdelete(namespace, keys);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,results.getRc());
        
	}
	@Test
	public void test_1644_Item_interface_different_type_HashMap_type_qa() {
		//step 1 add String value
		HashMap key1 = new HashMap();
		key1.put(1, "1");
		List value1 = new ArrayList();
		value1.add(1.1);
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        HashMap key2 = new HashMap();
		key2.put(2, "2");
		ArrayList value2 = new ArrayList();
		value2.add((long)1);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.addItems(namespace, key2, value2, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 get String value
        Result<DataEntry> result = tairManager.getItems(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 4 get String value
        result = tairManager.getItems(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
              
      //step 5 getItemCount String value
        Result<Integer> count = tairManager.getItemCount(namespace, key1);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(1,count.getValue().longValue());
      
        //step 6 getAndRemove String value
        result = tairManager.getAndRemove(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        result = tairManager.getItems(namespace, key1,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
      //step 7 delete String value
        result = tairManager.getAndRemove(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
        result = tairManager.getItems(namespace, key2,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
     
	}
	@Test
	public void test_1645_count_interface_different_type_HashMap_type_qa() {
		//step 1 incr String key
		HashMap key = new HashMap();
		key.put(1, "1");
		int value = 1;
		tairManager.delete(namespace, key);
		Result<Integer> count = tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 2 incr List value
		count =  tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(5,count.getValue().intValue());
        
      //step 3 decr String key
		count = tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 4 decr List value
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(3,count.getValue().intValue());
        
        //setp 5 delete data
        tairManager.delete(value, key);
        
        //step 6 incr String data
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(2,count.getValue().intValue());
       
        //setp 7 delete data
        tairManager.delete(value, key);
    
	}
	
	@Test
	public void test_1646_basic_interface_different_type_TreeMap_type_qa() {
		//step 1 add String value
		TreeMap key1 = new TreeMap();
		key1.put(1, "1");
		String value1 = "1";
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.put(namespace, key1, value1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        TreeMap key2 = new TreeMap();
		key2.put(2, "2");
        Vector value2 = new Vector();
		value2.add(2);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.put(namespace, key2, value2);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 add List value
        TreeMap key3 = new TreeMap();
		key3.put(3, "3");
		TreeMap value3 = new TreeMap();
		value3.put(key3, "2");
		tairManager.delete(namespace, key3);
        resultCode = tairManager.put(namespace, key3, value3);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 4 get String value
        Result<DataEntry> result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 5 get String value
        result = tairManager.get(namespace, key2);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
        
      //step 6 get String value
        result = tairManager.get(namespace, key3);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value3.toString(),((DataEntry)result.getValue()).getValue().toString());
        
      //step 7 get String value
        List keys = new ArrayList();
        keys.add(key1);
        keys.add(key2);
        keys.add(key3);
        Result<List<DataEntry>> results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,results.getRc());
        Assert.assertEquals(3,results.getValue().size());
        
        String valueAll = value1.toString()+value2.toString()+value3.toString();
        Assert.assertTrue(valueAll.contains(results.getValue().get(0).getValue().toString()));
        Assert.assertTrue(valueAll.contains(results.getValue().get(1).getValue().toString()));
        Assert.assertTrue(valueAll.contains(results.getValue().get(2).getValue().toString()));
        
      //step 8 delete String value
        resultCode = tairManager.delete(namespace, key1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
        //step 9 mdelete String value
        resultCode = tairManager.mdelete(namespace, keys);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,results.getRc());
        
	}
	@Test
	public void test_1647_Item_interface_different_type_TreeMap_type_qa() {
		//step 1 add String value
		TreeMap key1 = new TreeMap();
		key1.put(1, "1");
		List value1 = new ArrayList();
		value1.add(1.1);
		tairManager.delete(namespace, key1);
        ResultCode resultCode = tairManager.addItems(namespace, key1, value1, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
    	//step 2 add List value
        TreeMap key2 = new TreeMap();
		key2.put(2, "2");
		ArrayList value2 = new ArrayList();
		value2.add((long)1);
		tairManager.delete(namespace, key2);
        resultCode = tairManager.addItems(namespace, key2, value2, 5, 1, 5);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,resultCode);
        
      //step 3 get String value
        Result<DataEntry> result = tairManager.getItems(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        
      //step 4 get String value
        result = tairManager.getItems(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
              
      //step 5 getItemCount String value
        Result<Integer> count = tairManager.getItemCount(namespace, key1);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(1,count.getValue().longValue());
      
        //step 6 getAndRemove String value
        result = tairManager.getAndRemove(namespace, key1,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value1,((DataEntry)result.getValue()).getValue());
        result = tairManager.getItems(namespace, key1,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
        
      //step 7 delete String value
        result = tairManager.getAndRemove(namespace, key2,0,TairConstant.ITEM_ALL);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,result.getRc());
        Assert.assertEquals(value2,((DataEntry)result.getValue()).getValue());
        result = tairManager.getItems(namespace, key2,0,1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.DATANOTEXSITS,result.getRc());
     
	}
	@Test
	public void test_1648_count_interface_different_type_TreeMap_type_qa() {
		//step 1 incr String key
		TreeMap key = new TreeMap();
		key.put(1, "1");
		int value = 1;
		tairManager.delete(namespace, key);
		Result<Integer> count = tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 2 incr List value
		count =  tairManager.incr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(5,count.getValue().intValue());
        
      //step 3 decr String key
		count = tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(4,count.getValue().intValue());
        
    	//step 4 decr List value
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(3,count.getValue().intValue());
        
        //setp 5 delete data
        tairManager.delete(value, key);
        
        //step 6 incr String data
		count =  tairManager.decr(namespace, key, value, 3, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(2,count.getValue().intValue());
       
        //setp 7 delete data
        tairManager.delete(value, key);
    
	}
}
