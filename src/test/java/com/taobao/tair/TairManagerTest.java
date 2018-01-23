package com.taobao.tair;

import com.taobao.tair.impl.DefaultTairManager;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mei
 * @date 22/01/2018
 */
@RunWith(JUnit4.class)
public class TairManagerTest {

    /*

	<bean name="tairManager" class="com.taobao.tair.impl.DefaultTairManager" init-method="init">
		<property name="configServers" value="${tair.hosts}"/>
		<property name="groupName" value="${tair.groupname}"/>
	</bean>
     */
    private static TairManager tairManager;

    @BeforeClass
    public static void init(){
        DefaultTairManager defaultTairManager = new DefaultTairManager();
        defaultTairManager.setConfigServers("192.168.123.231:5198,192.168.123.232:5198");
        defaultTairManager.setGroupName("main");
        defaultTairManager.init();
        tairManager = defaultTairManager;
    }


    @Test
    public void put() {
        TestData testData = new TestData();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 1024; i++) {
            stringBuilder.append("abc");
        }
        testData.setTest(stringBuilder.toString());
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < 1024; i++) {
            stringList.add(String.valueOf(i));
        }
        testData.setTestList(stringList);
        Map<Integer, String> stringMap = new HashMap<>();
        for (int i = 0; i < 1024; i++) {
            stringMap.put(i, String.valueOf(i));
        }
        testData.setTestMap(stringMap);
        ResultCode resultCode = tairManager.put(1, "tairPutTest", testData);
        System.out.println(resultCode.isSuccess() + resultCode.getMessage());

        Result<DataEntry> result = tairManager.get(1, "tairPutTest");
        if (result.getRc() == ResultCode.SUCCESS){
            TestData data = (TestData)result.getValue().getValue();
            System.out.println(data);
        }
    }

    private static class TestData implements Serializable{
        private static final long serialVersionUID = 1L;

        private String test;
        private List<String> testList;
        private Map<Integer, String> testMap;

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }

        public List<String> getTestList() {
            return testList;
        }

        public void setTestList(List<String> testList) {
            this.testList = testList;
        }

        public Map<Integer, String> getTestMap() {
            return testMap;
        }

        public void setTestMap(Map<Integer, String> testMap) {
            this.testMap = testMap;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("TestData{");
            sb.append("test='").append(test).append('\'');
            sb.append(", testList=").append(testList);
            sb.append(", testMap=").append(testMap);
            sb.append('}');
            return sb.toString();
        }
    }
}