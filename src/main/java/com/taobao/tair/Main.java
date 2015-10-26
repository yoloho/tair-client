package com.taobao.tair;

import com.taobao.tair.impl.DefaultTairManager;

import java.util.ArrayList;
import java.util.List;

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
		tairManager.put(0, "test", "dddd");
		tairManager.delete(0, "test");
		tairManager.put(0, "a", "dddd");
		Result<DataEntry> a = tairManager.get(0, "a");
		System.out.println(a.getValue().getVersion());
		System.out.println(a.getValue().getKey());
		System.out.println(a.getValue().getValue());
		System.out.println(a.getRc().toString());
		System.out.println(tairManager.getItems(0, "a", 0, 10));

		//System.out.println(tairManager.get(0, "a"));
		//if (1==1) return;
		/*Result<DataEntry> result = tairManager.get(0, "23423");
		if (result.isSuccess()) {
		    DataEntry entry = result.getValue();
		    if(entry != null) {
		        // 数据存在
		    	System.out.println(entry.getValue());
		    } else {
		        // 数据不存在
		    	System.out.println("no");
		    }
		} else {
		    // 异常处理
			System.out.println(result.toString());
			System.out.println("exception!!!!!!");
		}
		if (1==1) return;*/
//		double begin = System.currentTimeMillis();
//		for (int i = 0; i < 100; i ++) {
//			new Worker().start();
//		}
//		is_begin = true;
//		double begin1 = System.currentTimeMillis();
//		while (total_count < 100) {
//			try {
//				Thread.sleep(1000);
//				System.out.println(total_time / 1000 + ": " + total_count);
//			} catch (InterruptedException e) {
//			}
//		}
//		System.out.println(total_time / total_count);
//		System.out.println(total_time / 1000 + ": " + total_count);
//		System.out.println((begin1 - begin) / 1000 + ": " + (System.currentTimeMillis() - begin) / 1000);
		System.exit(0);
	}
}
