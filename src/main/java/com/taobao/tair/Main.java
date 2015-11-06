package com.taobao.tair;

import com.taobao.tair.etc.KeyCountPack;
import com.taobao.tair.etc.KeyValuePack;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.impl.DefaultTairManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 * Created by liujunshi on 15-10-13.
 */
public class Main {

	static double total_time = 0;
	static int total_count = 0;
	static boolean is_begin = false;
	static DefaultTairManager tairManager = new DefaultTairManager();
	public static void main(String[] args){
		List<String> confServers = new ArrayList<String>();
		confServers.add("192.168.123.231:5198");
		confServers.add("192.168.123.232:5198");
//		confServers.add("10.10.99.40:5198");
//		confServers.add("10.10.99.41:5198");
		tairManager.setConfigServerList(confServers);
		//tairManager.setGroupName("demo1");
		tairManager.setGroupName("main");
		tairManager.init();
		do {
			List<KeyCountPack> keyCountPacks = new ArrayList<KeyCountPack>();
			List<KeyValuePack> keyValuePacks = new ArrayList<KeyValuePack>();
			keyValuePacks.add(new KeyValuePack("t1", "test1"));
			keyValuePacks.add(new KeyValuePack("t2", "test2"));
			keyValuePacks.add(new KeyValuePack("t3", "test3"));
			keyCountPacks.add(new KeyCountPack("c1", 4));
			keyCountPacks.add(new KeyCountPack("c2", 5));
			keyCountPacks.add(new KeyCountPack("c3", 6));
			List<Serializable> skeyList = new ArrayList<Serializable>();
			skeyList.add("t1");
			skeyList.add("t2");
			skeyList.add("t3");
			skeyList.add("c1");
			skeyList.add("c2");
			skeyList.add("c3");
			//multi puts values and counters
			tairManager.prefixPuts(0, "a", keyValuePacks, keyCountPacks);
			tairManager.prefixIncr(0, "a", "c1", 5, 0, 0);
			tairManager.prefixDecr(0, "a", "c2", 3, 0, 0);
			Result<Map<Object, Result<DataEntry>>> result = tairManager.prefixGets(0, "a", skeyList);
			if (!result.isSuccess()) {
				System.out.println("prefix gets fail");
				break;
			}
			Map<Object, Result<DataEntry>> map = result.getValue();
			if (map.size() != 6) {
				System.out.println("prefix gets count error");
				break;
			}
			if (!map.get("t1").getValue().getValue().equals("test1")) {
				System.out.println("prefix puts fail1");
				break;
			}
			if (!map.get("t2").getValue().getValue().equals("test2")) {
				System.out.println("prefix puts fail2");
				break;
			}
			if (!map.get("t3").getValue().getValue().equals("test3")) {
				System.out.println("prefix puts fail3");
				break;
			}
			if (!map.get("c1").getValue().getValue().equals(9)) {
				System.out.println("prefix puts fail4");
				break;
			}
			if (!map.get("c2").getValue().getValue().equals(2)) {
				System.out.println("prefix puts fail5");
				break;
			}
			if (!map.get("c3").getValue().getValue().equals(6)) {
				System.out.println("prefix puts fail6");
				break;
			}
			System.out.println("prefix puts with counters succ");
		} while(false);
		{
			//multi delete with prefix
			int succ = 0;
			tairManager.prefixPut(0, "a", "1", "111");
			tairManager.prefixPut(0, "a", "2", "111");
			tairManager.prefixPut(0, "a", "3", "111");
			tairManager.prefixPut(0, "a", "4", "111");
			tairManager.prefixPut(0, "a", "5", "111");
			List<Serializable> skeyList = new ArrayList<Serializable>();
			skeyList.add("1");
			skeyList.add("2");
			skeyList.add("3");
			skeyList.add("4");
			skeyList.add("5");
			Result<Map<Object, Result<DataEntry>>> result = tairManager.prefixGets(0, "a", skeyList);
			if (result.getValue().size() == 5) {
				succ ++;
			}
			tairManager.prefixDeletes(0, "a", skeyList);
			result = tairManager.prefixGets(0, "a", skeyList);
			if (result.getRc() == ResultCode.DATANOTEXSITS || result.getValue().size() == 0) {
				succ ++;
			}
			if (succ == 2) {
				System.out.println("prefix deletes succ");
			}
		}
		{
			//get and update expire
			tairManager.put(0, "a", "test", 0, 1000);
			Result<DataEntry> result = tairManager.get(0, "a");
			if (result.isSuccess()) {
				int diff = (int) Math.abs(result.getValue().getEdate() - System.currentTimeMillis() / 1000 - 1000);
				if (diff < 100) {
					//succ
					tairManager.get(0, "a", 2000);
					result = tairManager.get(0, "a");
					diff = (int) Math.abs(result.getValue().getEdate() - System.currentTimeMillis() / 1000 - 2000);
					if (diff < 100 && result.getValue().getValue().equals("test")) {
						//succ
						System.out.println("expire test succ");
					} else {
						System.out.println("expire test failed in step 2");
					}
				} else {
					System.out.println("expire test failed");
				}
			}
		}
		{
			tairManager.put(0, "a", "asdf");
			tairManager.get(0, "a");
			tairManager.setCount(0, "a", 0);
			tairManager.get(0, "a");
			tairManager.incr(0, "a", 2, 0, 600);
			tairManager.get(0, "a");
			tairManager.lock(0, "a");
			tairManager.incr(0, "a", 2, 0, 600);
			tairManager.unlock(0, "a");
			Result<DataEntry> result = tairManager.get(0, "a");
			if (result.getValue().getValue().equals(4)) {
				System.out.println("addcount succ");
			} else {
				System.out.println("error");
			}
		}
		{
			tairManager.put(0, "a", "new value");
			ResultCode result = tairManager.lock(0, "a");
			if (!result.isSuccess()) {
				System.out.println("lock failed");
			} else {
				result = tairManager.lock(0, "a");
				if (result.isSuccess()) {
					System.out.println("relock succ, ERROR!");
				} else {
					result = tairManager.unlock(0, "a");
					if (!result.isSuccess()) {
						System.out.println("unlock failed");
					} else {
						result = tairManager.unlock(0, "a");
						if (result.isSuccess()) {
							System.out.println("reunlock succ, ERROR!");
						} else {
							System.out.println("normal key lock succ");
						}
					}
				}
			}
		}
		{
			tairManager.prefixPut(0, "a", "a", "new value");
			ResultCode result = tairManager.lock(0, "a", "a");
			if (!result.isSuccess()) {
				System.out.println("lock failed");
			} else {
				result = tairManager.lock(0, "a", "a");
				if (result.isSuccess()) {
					System.out.println("relock succ, ERROR!");
				} else {
					result = tairManager.unlock(0, "a", "a");
					if (!result.isSuccess()) {
						System.out.println("unlock failed");
					} else {
						result = tairManager.unlock(0, "a", "a");
						if (result.isSuccess()) {
							System.out.println("reunlock succ, ERROR!");
						} else {
							System.out.println("prefix key lock succ");
						}
					}
				}
			}
		}
		{
			String pkey = "c";
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("aa", String.valueOf((int)(Math.random() * 10000000)));
			map.put("bb", String.valueOf((int)(Math.random() * 10000000)));
			map.put("cc", String.valueOf((int)(Math.random() * 10000000)));
			map.put("dd", String.valueOf((int)(Math.random() * 10000000)));
			for (Entry<String, String> entry : map.entrySet()) {
				tairManager.prefixPut(0, pkey, entry.getKey(), entry.getValue());
			}
			boolean is_succ = true;
			for (Entry<String, String> entry : map.entrySet()) {
				Result<DataEntry> result = tairManager.prefixGet(0, pkey, entry.getKey());
				if (result.isSuccess() && result.getValue() != null && result.getValue().getKey().equals(entry.getKey()) && result.getValue().getValue().equals(entry.getValue())) {
					//succ
				} else {
					//fail
					is_succ = false;
					System.out.println("entry fail: " + entry.getKey() + " => " + entry.getValue());
				}
			}
			if (is_succ) {
				System.out.println("single prefix put/get succ");
			}
		}
		{
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("aa", String.valueOf((int)(Math.random() * 10000000)));
			map.put("bb", String.valueOf((int)(Math.random() * 10000000)));
			map.put("cc", String.valueOf((int)(Math.random() * 10000000)));
			map.put("dd", String.valueOf((int)(Math.random() * 10000000)));
			List<KeyValuePack> keyValuePacks = new ArrayList<KeyValuePack>();
			List<Serializable> skeyList = new ArrayList<Serializable>();
			for (Entry<String, String> entry : map.entrySet()) {
				keyValuePacks.add(new KeyValuePack(entry.getKey(), entry.getValue()));
				skeyList.add(entry.getKey());
			}
			tairManager.prefixPuts(0, "a", keyValuePacks);
			Result<Map<Object, Result<DataEntry>>> result = tairManager.prefixGets(0, "a", skeyList);
			boolean is_succ = true;
			if (result.isSuccess() && result.getValue() != null) {
				for (Entry<Object, Result<DataEntry>> entry : result.getValue().entrySet()) {
					if (map.get(entry.getKey()).equals(entry.getValue().getValue().getValue())) {
						//succ
					} else {
						is_succ = false;
						System.out.println("entry fail: " + entry.getKey() + " => " + entry.getValue().getValue().getValue());
					}
				}
			}
			if (is_succ) {
				System.out.println("multi prefix puts/gets succ");
			}
		}
		{
			Result<List<DataEntry>> result = tairManager.getRange(0, "c", "0", "z", 0, 100);
			System.out.println("get range return " + result.getValue().size() + " values");
		}
		System.exit(0);
	}
}
