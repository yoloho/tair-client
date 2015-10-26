package com.taobao.tair.common;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import junit.framework.Assert;

import org.junit.Test;

import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;

public class TairManager_08_x_incr_otherFunction_Test extends BaseTest {
	private int namespace = 1;
	@Test
	public void test_801_incr_interface_otherFunction_Test_qa() {   
        //step 1 add data to test
		String key1 = "321_key_1";
        int value1 = 1;
        tairManager.delete(namespace, key1);
        Result<Integer> count = tairManager.incr(namespace, key1, value1, 0, 10);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(1,count.getValue().intValue());
        
        //step 2 add data to test
        String key2 = "321_key_2";
        int value2 = 3;
        tairManager.delete(namespace, key2);
        count = tairManager.incr(namespace, key2, value2, 0, 10);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(value2,count.getValue().intValue());
        
        //step 3 get
        Result<DataEntry> result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(1, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key1, ((DataEntry) result.getValue()).getKey());
        
		 //step 4 mget
		List keys = new ArrayList();
		keys.add(key1);
		keys.add(key2);
		Result<List<DataEntry>> results = tairManager.mget(namespace, keys);
        Assert.assertTrue(results.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS, results.getRc());
        Assert.assertEquals(2, results.getValue().size());
        
        Assert.assertTrue((((DataEntry) results.getValue().get(0)).getValue().equals(value1)
        			&&((DataEntry) results.getValue().get(0)).getVersion()==1
        			&&((DataEntry) results.getValue().get(0)).getKey().equals(key1)
        			&&((DataEntry) results.getValue().get(1)).getValue().equals(value2)
        			&&((DataEntry) results.getValue().get(1)).getVersion()==1
        			&&((DataEntry) results.getValue().get(1)).getKey().equals(key2))
        		||(((DataEntry) results.getValue().get(1)).getValue().equals(value1)
            			&&((DataEntry) results.getValue().get(1)).getVersion()==1
            			&&((DataEntry) results.getValue().get(1)).getKey().equals(key1)
            			&&((DataEntry) results.getValue().get(0)).getValue().equals(value2)
            			&&((DataEntry) results.getValue().get(0)).getVersion()==1
            			&&((DataEntry) results.getValue().get(0)).getKey().equals(key2)));
		
		//step 5 addItems
		Vector items = new Vector();
		items.add(3);
		ResultCode resultCode = tairManager.addItems(namespace, key1, items, 5, 1, 6);
		Assert.assertFalse(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.TYPENOTMATCH, resultCode);
	    
	    //step 6 getItems
	    result = tairManager.getItems(namespace, key1, 0, 1);
		Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.TYPENOTMATCH, result.getRc());
	    Assert.assertEquals(null, result.getValue());
	    
	  //step 7 getAndRemove 
	    result = tairManager.getAndRemove(namespace, key1, 0, 1);
		Assert.assertFalse(result.isSuccess());
	    Assert.assertEquals(ResultCode.TYPENOTMATCH, result.getRc());
	    Assert.assertEquals(null, result.getValue());
	    
	  //step 8 removeItems //TODO
	    resultCode = tairManager.removeItems(namespace, key1, 0, 1);
		Assert.assertFalse(resultCode.isSuccess());
	    Assert.assertEquals(ResultCode.TYPENOTMATCH, resultCode);
	    
	  //step 9 getItemsCount
	    count = tairManager.getItemCount(namespace, key1);
        Assert.assertFalse(count.isSuccess());
        Assert.assertEquals(ResultCode.TYPENOTMATCH, count.getRc());
        result = tairManager.get(namespace, key1);
        Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(value1, ((DataEntry) result.getValue()).getValue());
		assertEquals(1, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key1, ((DataEntry) result.getValue()).getKey());
		
		//step 10 incr
		count = tairManager.incr(namespace, key1, 1, 4, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS, count.getRc());
        result = tairManager.get(namespace, key1);
        Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(value1+1, ((DataEntry) result.getValue()).getValue());
		assertEquals(2, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key1, ((DataEntry) result.getValue()).getKey());
		
		//step 11 incr
		count = tairManager.decr(namespace, key1, 1, 4, 5);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS, count.getRc());
        result = tairManager.get(namespace, key1);
        Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(value1, ((DataEntry) result.getValue()).getValue());
		assertEquals(3, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key1, ((DataEntry) result.getValue()).getKey());
		
		//step 12 delete
		resultCode = tairManager.delete(namespace, key1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS, resultCode);
        result = tairManager.get(namespace, key1);
        Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
		
        //step 13 incr new data
        count = tairManager.incr(namespace, key1, value1, 0, 10);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(1,count.getValue().intValue());
		
        //step 14 mdelete
        resultCode = tairManager.mdelete(namespace, keys);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS, resultCode);
        result = tairManager.get(namespace, key1);
        Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
        result = tairManager.get(namespace, key2);
        Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
        
        //step 15 incr new data
        count = tairManager.incr(namespace, key1, value1, 0, 10);
        Assert.assertTrue(count.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS,count.getRc());
        Assert.assertEquals(1,count.getValue().intValue());
        
        //step 16 put
        int value3 = 5;
        resultCode = tairManager.put(namespace, key1, value3);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS, resultCode);
        
        result = tairManager.get(namespace, key1);
        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS, result.getRc());
		assertEquals(5, ((DataEntry) result.getValue()).getValue());
		assertEquals(2, ((DataEntry) result.getValue()).getVersion());
		assertEquals(key1, ((DataEntry) result.getValue()).getKey());
		
		//step 17 incr
		count = tairManager.incr(namespace, key1, 1, 4, 5);
        Assert.assertFalse(count.isSuccess());
        Assert.assertEquals(ResultCode.CANNT_OVERRIDE, count.getRc());

    	//step 18 decr //TODO
		count = tairManager.decr(namespace, key1, 1, 4, 5);
		Assert.assertFalse(count.isSuccess());
        Assert.assertEquals(ResultCode.CANNT_OVERRIDE, count.getRc());
        
      //step 19 delete
		resultCode = tairManager.delete(namespace, key1);
        Assert.assertTrue(resultCode.isSuccess());
        Assert.assertEquals(ResultCode.SUCCESS, resultCode);
        result = tairManager.get(namespace, key1);
        Assert.assertEquals(ResultCode.DATANOTEXSITS, result.getRc());
		
	}
	
}
