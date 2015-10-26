/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Tair的接口，支持持久化存储和非持久化（即cache）存储
 *
 * @author ruohai
 *
 */
public interface TairManager {

	/**
	 * 获取数据
	 *
	 * @param namespace
	 *            数据所在的namespace
	 * @param key
	 *            要获取的数据的key
	 * @return
	 */
	Result<DataEntry> get(int namespace, Serializable key);

	/**
	 * 批量获取数据
	 *
	 * @param namespace
	 *            数据所在的namespace
	 * @param keys
	 *            要获取的数据的key列表
	 * @return 如果成功，返回的数据对象为一个Map<Key, Value>
	 */
	Result<List<DataEntry>> mget(int namespace, List<? extends Object> keys);

	/**
	 * 设置数据，如果数据已经存在，则覆盖，如果不存在，则新增 如果是新增，则有效时间为0，即不失效 如果是更新，则不检查版本，强制更新
	 *
	 * @param namespace
	 *            数据所在的namespace
	 * @param key
	 * @param value
	 * @return
	 */
	ResultCode put(int namespace, Serializable key, Serializable value);
	/**
	 * 设置数据，如果数据已经存在，则覆盖，如果不存在，则新增
	 *
	 * @param namespace
	 *            数据所在的namespace
	 * @param key
	 *            数据的key
	 * @param value
	 *            数据的value
	 * @param version
	 *            数据的版本，如果和系统中数据的版本不一致，则更新失败
	 * @return
	 */
	ResultCode put(int namespace, Serializable key, Serializable value,
			int version);

	/**
	 * 设置数据，如果数据已经存在，则覆盖，如果不存在，则新增
	 *
	 * @param namespace
	 *            数据所在的namespace
	 * @param key
	 *            数据的key
	 * @param value
	 *            数据的value
	 * @param version
	 *            数据的版本，如果和系统中数据的版本不一致，则更新失败
	 * @param expireTime
	 *            数据的有效时间，单位为秒
	 * @return
	 */
	ResultCode put(int namespace, Serializable key, Serializable value,
			int version, int expireTime);
	/**
	 * 删除key对应的数据
	 *
	 * @param namespace
	 *            数据所在的namespace
	 * @param key
	 *            数据的key
	 * @return
	 */
	ResultCode delete(int namespace, Serializable key);

	/**
	 * delete through 'invalid server', the key/values with the groupname
	 * in several corresponding clusters would all be deleted.
	 *
	 * @param namespace
	 *            area/namespace the key belongs to.
	 * @param key
	 *            key to invalid.
	 * @return
	 */
	ResultCode invalid(int namespace, Serializable key);
	/**
	 * 批量失效数据，该方法将失效由失效服务器配置的多个实例中当前group下的数据
	 *
	 * @param namespace
	 *            数据所在的namespace
	 * @param keys
	 *            要失效的key列表
	 * @return
	 */

	ResultCode minvalid(int namespace, List<? extends Object> keys);

	/**
	 * 批量删除，如果全部删除成功，返回成功，否则返回失败
	 *
	 * @param namespace
	 *            数据所在的namespace
	 * @param keys
	 *            要删除数据的key列表
	 * @return
	 */
	ResultCode mdelete(int namespace, List<? extends Object> keys);
	/**
	 * 将key对应的数据加上value，如果key对应的数据不存在，则新增，并将值设置为defaultValue
	 * 如果key对应的数据不是int型，则返回失败
	 *
	 * @param namespace
	 *            数据所在的namspace
	 * @param key
	 *            数据的key
	 * @param value
	 *            要加的值
	 * @param defaultValue
	 *            不存在时的默认值
	 * @return 更新后的值
	 */
	Result<Integer> incr(int namespace, Serializable key, int value,
			int defaultValue, int expireTime);

	/**
	 * 将key对应的数据减去value，如果key对应的数据不存在，则新增，并将值设置为defaultValue
	 * 如果key对应的数据不是int型，则返回失败
	 *
	 * @param namespace
	 *            数据所在的namspace
	 * @param key
	 *            数据的key
	 * @param value
	 *            要减去的值
	 * @param defaultValue
	 *            不存在时的默认值
	 * @return 更新后的值
	 */
	Result<Integer> decr(int namespace, Serializable key, int value,
			int defaultValue, int expireTime);

 	/**
	 * 获取prefix为前缀的数据:  获取以prefix为前缀，子key大于等于key_start，
   *          小于等于key_end范围中从offset个开始最多limit个KV的数据(仅ldb有效)
	 * @param namespace  数据所在的namespace
	 * @param prefix  前缀key
	 * @param key_start  起始子key，如果为null表示从第一个开始获取
	 * @param key_end   结束子key，如果为null表示获取到前缀key下最后一个
	 * @param offset 跳过前offset个key
	 * @param limit  返回的数据数量限制
	 * @param reverse 是否要逆序查找，建议使用false，逆序遍历代价比顺序要高
	 * @return getValue():KV对的List
	 */
   Result<List<DataEntry>> getRange(int namespace, Serializable prefix,
      Serializable key_start, Serializable key_end, int offset, int limit, boolean reverse);

	/**
	 * 获取prefix为前缀的数据(只有value)(仅ldb有效)
	 * @param namespace  数据所在的namespace
	 * @param prefix  前缀key
	 * @param key_start  起始子key，如果为null表示从第一个开始获取
	 * @param key_end   结束子key，如果为null表示获取到前缀key下最后一个
	 * @param offset 跳过前offset个key
	 * @param limit  返回的数据数量限制
	 * @param reverse 是否要逆序查找，建议使用false，逆序遍历代价比顺序要高
	 * @return getValue(): Value的List
	 */
  Result<List<DataEntry>> getRangeOnlyValue(int namespace, Serializable prefix,
      Serializable key_start, Serializable key_end, int offset, int limit, boolean reverse);

	/**
	 * 获取prefix为前缀的Key(仅ldb有效)
	 * @param namespace  数据所在的namespace
	 * @param prefix  前缀key
	 * @param key_start  起始子key，如果为null表示从第一个开始获取
	 * @param key_end   结束子key，如果为null表示获取到前缀key下最后一个
	 * @param offset 跳过前offset个key
	 * @param limit  返回的数据数量限制
	 * @param reverse 是否要逆序查找，建议使用false，逆序遍历代价比顺序要高
	 * @return getValue(): Key的List
	 */

  Result<List<DataEntry>> getRangeOnlyKey(int namespace, Serializable prefix,
      Serializable key_start, Serializable key_end, int offset, int limit, boolean reverse);


  //* 老的getRange接口 为兼容性保留  *//
 	/**
	 * 获取prefix为前缀的数据:  获取以prefix为前缀，子key大于等于key_start，
   *          小于等于key_end范围中从offset个开始最多limit个KV的数据(仅ldb有效)
	 * @param namespace  数据所在的namespace
	 * @param prefix  前缀key
	 * @param key_start  起始子key，如果为null表示从第一个开始获取
	 * @param key_end   结束子key，如果为null表示获取到前缀key下最后一个
	 * @param offset 跳过前offset个key
	 * @param limit  返回的数据数量限制
	 * @return getValue():KV对的List
	 */
   Result<List<DataEntry>> getRange(int namespace, Serializable prefix,
      Serializable key_start, Serializable key_end, int offset, int limit);

	/**
	 * 获取prefix为前缀的数据(只有value)(仅ldb有效)
	 * @param namespace  数据所在的namespace
	 * @param prefix  前缀key
	 * @param key_start  起始子key，如果为null表示从第一个开始获取
	 * @param key_end   结束子key，如果为null表示获取到前缀key下最后一个
	 * @param offset 跳过前offset个key
	 * @param limit  返回的数据数量限制
	 * @return getValue(): Value的List
	 */
  Result<List<DataEntry>> getRangeOnlyValue(int namespace, Serializable prefix,
      Serializable key_start, Serializable key_end, int offset, int limit);

	/**
	 * 获取prefix为前缀的Key(仅ldb有效)
	 * @param namespace  数据所在的namespace
	 * @param prefix  前缀key
	 * @param key_start  起始子key，如果为null表示从第一个开始获取
	 * @param key_end   结束子key，如果为null表示获取到前缀key下最后一个
	 * @param offset 跳过前offset个key
	 * @param limit  返回的数据数量限制
	 * @return getValue(): Key的List
	 */

  Result<List<DataEntry>> getRangeOnlyKey(int namespace, Serializable prefix,
      Serializable key_start, Serializable key_end, int offset, int limit);

 	/**
	 * 删除prefix为前缀的数据:  获取以prefix为前缀，子key大于等于key_start，
   *          小于等于key_end范围中从offset个开始最多limit个KV的数据(仅ldb有效)
	 * @param namespace  数据所在的namespace
	 * @param prefix  前缀key
	 * @param key_start  起始子key，如果为null表示从第一个开始获取
	 * @param key_end   结束子key，如果为null表示获取到前缀key下最后一个
	 * @param offset 跳过前offset个key
	 * @param limit  返回的数据数量限制
	 * @param reverse 是否要逆序查找，建议使用false，逆序遍历代价比顺序要高
	 * @return getValue(): 被删除key的list
	 */
   Result<List<DataEntry>> delRange(int namespace, Serializable prefix,
      Serializable key_start, Serializable key_end, int offset, int limit, boolean reverse);

	/**
	 * 增加集合数据类型，如果原集合数据不存在，则执行insert操作
	 * @param namespace 数据所在的namespace
	 * @param key 数据的key
	 * @param items 要增加的value，当前值接受基本类型，详情参见Json.checkType
	 * @param maxCount 集合允许的最大条目数量，超过这个数量，系统将直接删除相应数量的最早放入的条目
	 * @param version 版本号，如果非0，当传入的版本号和系统中的版本号不同时，返回版本错误
	 * @param expireTime 超时时间
	 * @return 返回代码
	 * @deprecated
	 */
	ResultCode addItems(int namespace, Serializable key,
			List<? extends Object> items, int maxCount, int version,
			int expireTime);

	/**
	 * 获取集合数据
	 * @param namespace 数据所在的namespace
	 * @param key 数据的key
	 * @param offset 要获取的数据的偏移量
	 * @param count 要获取的数据的条数
	 * @return 如果数据不存在，返回DATANOTEXIST，否则成功返回相应的条数，失败返回相应的错误代码
	 * @deprecated
	 */
	Result<DataEntry> getItems(int namespace, Serializable key,
			int offset, int count);

	/**
	 * 删除集合中的数据
	 * @param namespace 数据所在的namespace
	 * @param key 数据的key
	 * @param offset 要删除的数据的偏移量
	 * @param count 要删除的数据的条数
	 * @return 删除是否成功
	 * @deprecated
	 */
	ResultCode removeItems(int namespace, Serializable key, int offset,
			int count);

	/**
	 * 删除并返回集合中的数据
	 * @param namespace 数据所在的namespace
	 * @param key 数据的key
	 * @param offset 要删除的数据的偏移量
	 * @param count 要删除的数据的条数
	 * @return 如果删除成功，返回本次删除成功删除的数据
	 * @deprecated
	 */
	Result<DataEntry> getAndRemove(int namespace,
			Serializable key, int offset, int count);

	/**
	 * 获取key对应的集合中的条目数量
	 * @param namespace  数据所在的namespace
	 * @param key  数据的key
	 * @deprecated
	 * @return 如果数据不存在，返回不存在；否则成功返回集合的条目数量，失败返回相应的错误代码
	 */
	Result<Integer> getItemCount(int namespace, Serializable key);


	Map<String, String> getStat(int qtype, String groupName, long serverId);

	/**
	 * 获取客户端的版本
	 */
	String getVersion();
}
