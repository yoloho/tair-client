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

import com.taobao.tair.etc.CounterPack;
import com.taobao.tair.etc.KeyCountPack;
import com.taobao.tair.etc.KeyValuePack;

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
   * 获取指定的key值，并更新expireTime值，如果该值已过期，则无效
   *
   * @param namespace
   * @param key
   * @param expireTime
   * @return
   */
  Result<DataEntry> get(int namespace, Serializable key, int expireTime);


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
	 * @param namespace
	 *            数据所在的namespace
	 * @param key
	 * @param value
	 * @return
	 */
	ResultCode putAsync(int namespace, Serializable key, Serializable value,
			int version, int expireTime, boolean fillCache, TairCallback cb);
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
	 * @param fill_cache
	 *            是否插入cache（仅当server端存储引擎支持内嵌cache时有效）
	 * @return
	 */
	ResultCode put(int namespace, Serializable key, Serializable value,
			   int version, int expireTime, boolean fillCache);

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
	 * same as invalid(int, Serializable), but asynchronously
	 *
	 * @param namespace
	 *            area/namespace the key belongs to.
	 * @param key
	 *            key to invalid.
	 * @return
	 */
	ResultCode invalid(int namespace, Serializable key, CallMode callMode);

	/**
	 * hide the key/value, after which 'get' would return 'item is hidden'
	 *
	 * @param namespace
	 *            area, or namespace the key belongs to.
	 * @param key
	 *            key to hide.
	 * @return
	 */
	ResultCode hide(int namespace, Serializable key);

	/**
	 * same as hide(int, Serializable), but through 'invalid server'
	 *
	 * @param namespace
	 *            area, or namespace the key belongs to.
	 * @param key
	 *            key to hide.
	 * @return
	 */
	ResultCode hideByProxy(int namespace, Serializable key);

	/**
	 * same as hideByProxy(int, Serializable), but could be asynchronous.
	 *
	 * @param namespace
	 *            area, or namespace the key belongs to.
	 * @param key
	 *            key to hide.
	 * @return
	 */
	ResultCode hideByProxy(int namespace, Serializable key, CallMode callMode);

	/**
	 * get hidden item
	 *
	 * @param namespace
	 *            area, or namespace the key belongs to.
	 * @param key
	 *            key to get.
	 * @return
     *            返回被隐藏的数据
     *            SUCCESS，如果数据未被隐藏
     *            ITEM_HIDDEN，如果数据已被隐藏
     *            DATANOTEXISTS,数据不存在
     *            其他
	 */
	Result<DataEntry> getHidden(int namespace, Serializable key);

    /**
     * 前缀put
     * @param namespace 数据所在的namespace
     * @param pkey/skey 主/子key
     * @param value 值
     */
    public ResultCode prefixPut(int namespace, Serializable pkey, Serializable skey, Serializable value);

    /**
     * 前缀put
     * @param namespace 数据所在的namespace
     * @param pkey/skey 主/子key
     * @param value 值
     * @param version 数据版本号
     */
    public ResultCode prefixPut(int namespace, Serializable pkey, Serializable skey, Serializable value, int version);

    /**
     * 前缀put
     * @param namespace 数据所在的namespace
     * @param pkey/skey 主/子key
     * @param value 值
     * @param version 数据版本号
     * @param expireTime 过期时间，单位秒
     *                   小于当前时间戳时为相对时间，否则为绝对时间
     * @return SUCCESS
     *         VERERROR 版本号不匹配
     *         其他
     */
    public ResultCode prefixPut(int namespace, Serializable pkey, Serializable sKey, Serializable value, int version, int expireTime);

    /**
     * 多子key前缀put
     * @param pkey: 主key
     * @param keyValuePacks
     *          子key/版本号/过期时间的列表
     * @return
     *          整体返回码：SUCCESS/PARTSUCC，如果全部失败则为server处理的最后一个key的返回码
     *          若有失败：每一个子key的返回码
     */
    public Result<Map<Object, ResultCode>> prefixPuts(int namespace, Serializable pkey, List<KeyValuePack> keyValuePacks);

    /**
     * 多子key前缀put
     * @param pkey: 主key
     * @param keyValuePacks
     *          子key/版本号/过期时间的列表
     * @param keyCountPacks
     *          子key/版本号/过期时间的列表/计数器初始值
     * @return
     *          整体返回码：SUCCESS/PARTSUCC，如果全部失败则为server处理的最后一个key的返回码
     *          若有失败：每一个子key的返回码
     */
    public Result<Map<Object, ResultCode>> prefixPuts(int namespace,
            Serializable pkey, List<KeyValuePack> keyValuePacks, List<KeyCountPack> keyCountPacks);

    /**
     * @param namespace 数据所在的namespace
     * @param pkey/skey 主/子key
     * @return 返回数据
     *         SUCCESS，若数据未被隐藏
     *         ITEM_HIDDEN，数据被隐藏
     *         DATANOTEXISTS，数据不存在
     *         其他
     */
    public Result<DataEntry> prefixGet(int namespace, Serializable pkey, Serializable skey);

    /**
     * 多子key前缀get
     * @param pkey: 主key
     * @param keyValuePacks
     *          子key/版本号/过期时间的列表
     * @return
     *          整体返回码：SUCCESS/PARTSUCC，如果全部失败则为server处理的最后一个key的返回码
     *          若成功Result<DataEntry>中保存skey/value
     */
    public Result<Map<Object, Result<DataEntry>>> prefixGets(int namespace, Serializable pkey, List<? extends Serializable> skeyList);

    /**
     * 前缀delete
     * @param namespace 数据所在的namespace
     * @param pkey/skey 主/子key
     * @return SUCCESS/DATANOTEXITS或其他
     */
    public ResultCode prefixDelete(int namespace, Serializable pkey, Serializable sKey);

    /**
     * 多子key前缀put
     * @param pkey: 主key
     * @param skeys
     *          子key列表
     * @return
     *          整体返回码：SUCCESS/PARTSUCC，如果全部失败则为server处理的最后一个key的返回码
     *          若有失败：每一个子key的返回码
     */
    public ResultCode prefixDeletes(int namespace, Serializable pkey, List<? extends Serializable> skeys);

    /**
     * 前缀计数器递增
     * @param namespace 数据所在的namespace
     * @param pkey/skey 主/子key
     * @param value 增量，须非负
     * @param defaultValue 数据不存在时的默认基量
     * @param expireTime 过期时间，单位秒
     *                   小于当前时间戳时为相对时间，否则为绝对时间
     * @return 递增后的结果
     *         数据不存在时结果为defaultValue+value
     *         CANNT_OVERRIDE
     */
    public Result<Integer> prefixIncr(int namespace, Serializable pkey, Serializable skey, int value, int defaultValue, int expireTime);
    public Result<Integer> prefixIncr(int namespace, Serializable pkey, Serializable skey, int value, int defaultValue, int expireTime, int lowBound, int upperBound);

    /**
     * 前缀计数器递增操作的多子key版本
     * @param namespace 数据所在的namespace
     * @param pkey 主key
     * @param packList value/defaultValue/expireTime的列表
     * @return 整体返回码：SUCCESS/PARTSUCC, 若全部失败则为server处理的最后一个返回码
     *         每一个子key对应的计数器递增后的结果
     */
    public Result<Map<Object, Result<Integer>>> prefixIncrs(int namespace, Serializable pkey, List<CounterPack> packList);
    public Result<Map<Object, Result<Integer>>> prefixIncrs(int namespace, Serializable pkey, List<CounterPack> packList, int lowBound, int upperBound);

    /**
     * 前缀计数器递减
     * @param namespace 数据所在的namespace
     * @param pkey/skey 主/子key
     * @param value 递减量，须非负
     * @param defaultValue 数据不存在时的默认基量
     * @param expireTime 过期时间，单位秒
     *                   小于当前时间戳时为相对时间，否则为绝对时间
     * @return 递减后的结果
     *         数据不存在时结果为defaultValue-value
     *         CANNT_OVERRIDE
     */
    public Result<Integer> prefixDecr(int namespace, Serializable pkey, Serializable skey, int value, int defaultValue, int expireTime);
    public Result<Integer> prefixDecr(int namespace, Serializable pkey, Serializable skey, int value, int defaultValue, int expireTime, int lowBound, int upperBound);

    /**
     * 前缀计数器递减操作的多子key版本
     * @param pkey 主key
     * @param namespace 数据所在的namespace
     * @param packList value/defaultValue/expireTime的列表
     * @return 整体返回码：SUCCESS/PARTSUCC, 若全部失败则为server处理的最后一个返回码
     *         每一个子key对应的计数器递减后的结果
     *         CounterPack.value会被修改为其相反数
     */
    public Result<Map<Object, Result<Integer>>> prefixDecrs(int namespace, Serializable pkey, List<CounterPack> packList);
    public Result<Map<Object, Result<Integer>>> prefixDecrs(int namespace, Serializable pkey, List<CounterPack> packList, int lowBound, int upperBound);

    /**
     * 设置计数器的值，通常用来将普通kv转为计数器类型
     * @param namespace 数据所在的namespace
     * @param pkey/skey 主/子key
     * @param count 计数器的值
     */
    public ResultCode prefixSetCount(int namespace, Serializable pkey, Serializable skey, int count);

    /**
     * 设置计数器的值，通常用来将普通kv转为计数器类型
     * @param namespace 数据所在的namespace
     * @param pkey/skey 主/子key
     * @param count 计数器的值
     * @param version 数据版本号
     * @param expireTime 过期时间
     * @return SUCCESS/VERERROR或其他
     */
    public ResultCode prefixSetCount(int namespace, Serializable pkey, Serializable skey, int count, int version, int expireTime);
    public Result<Map<Object, ResultCode>> prefixSetCounts(int namespace, Serializable pkey, List<KeyCountPack> keyCountPacks);

    /**
     * 前缀hide
     * @param namespace 数据所在的namespace
     * @param pkey/skey 主/子key
     */
	public ResultCode prefixHide(int namespace, Serializable pkey, Serializable skey);

    /**
     * 获取（可能）被隐藏的数据
     * @param namespace 数据所在的namespace
     * @param pkey/skey 主/子key
     * @return 返回数据
     *         SUCCESS，若数据未被隐藏
     *         ITEM_HIDDEN，数据被隐藏
     *         DATANOTEXISTS，数据不存在
     *         其他
     */
    public Result<DataEntry> prefixGetHidden(int namespace, Serializable pkey, Serializable skey);

    /**
     * 多子key版本的prefixGetHidden
     * @param namespace 数据所在的namespace
     * @param skeys 子key列表
     * @return 整体返回码：SUCCESS/PARTSUCC, 若全部失败则为server处理的最后一个返回码
     *         每一个子key的value
     */
    public Result<Map<Object, Result<DataEntry>>>
        prefixGetHiddens(int namespace, Serializable pkey, List<? extends Serializable> skeys);

    /**
     * 前缀hide多子key版本
     * @param namespace 数据所在的namespace
     * @param pkey 主key
     * @param skeys 子key列表
     * @return 整体返回码：SUCCESS/PARTSUCC, 若全部失败则为server处理的最后一个返回码
     *         若有失败：每一个子key对应的返回码
     */
    public Result<Map<Object, ResultCode>> prefixHides(int namespace, Serializable pkey, List<? extends Serializable> skeys);

    /**
     * 前缀invalid，即经由invalid server代理delete
     * @param namespace 数据所在的namespace
     * @param pkey/skey 主/子key
     * @param callMode 同步/异步
     * @return 如果参数合法且客户端/invalid server通信正常，返回SUCCESS，除非invalid server启动错误，返回INVAL_CONN_ERROR
     *         若callMode为异步，立即返回SUCCESS，否则invalid server处理完请求才返回SUCCESS
     */
    public ResultCode prefixInvalid(int namespace, Serializable pkey, Serializable skey, CallMode callMode);

    /**
     * 前缀hide，经由invalid server代理hide
     * @param namespace 数据所在的namespace
     * @param pkey/skey 主/子key
     * @param callMode 同步/异步
     * @return 如果参数合法且客户端/invalid server通信正常，返回SUCCESS，除非invalid server启动错误，返回INVAL_CONN_ERROR
     *         若callMode为异步，立即返回SUCCESS，否则invalid server处理完请求才返回SUCCESS
     */
    public ResultCode prefixHideByProxy(int namespace, Serializable pkey, Serializable skey, CallMode callMode);

    /**
     * 多子key版本的prefixHideByProxy，经由invalid server代理hide
     * @param namespace 数据所在的namespace
     * @param pkey 主key
     * @param skeys 子key列表
     * @return 如果参数合法且客户端/invalid server通信正常，返回SUCCESS，除非invalid server启动错误，返回INVAL_CONN_ERROR
     *         若callMode为异步，立即返回SUCCESS，否则invalid server处理完请求才返回SUCCESS
     */
    public Result<Map<Object, ResultCode>> prefixHidesByProxy(int namespace, Serializable pkey, List<? extends Serializable> skeys, CallMode callMode);

    /**
     * 多子key版本的prefixInvalid，即经由invalid server代理delete
     * @param namespace 数据所在的namespace
     * @param pkey 主key
     * @param skeys 子key列表
     * @return 如果参数合法且客户端/invalid server通信正常，返回SUCCESS，除非invalid server启动错误，返回INVAL_CONN_ERROR
     *         若callMode为异步，立即返回SUCCESS，否则invalid server处理完请求才返回SUCCESS
     */
    public Result<Map<Object, ResultCode>> prefixInvalids(int namespace, Serializable pkey, List<? extends Serializable> skeys, CallMode callMode);

    /**
     * 多主key多子key版本的prefixGets
     * @param namespace 数据所在的namespace
     * @param pkeySkeyListMap 每个主key对应的子key列表
     * @return 整体返回码：SUCCESS/PARTSUCC, 若全部失败则为server处理的最后一个返回码
     *         每一个主key下对应的每一个子key的value
     */
   // public Result<Map<Object, Map<Object, Result<DataEntry>>>>
     //   mprefixGets(int namespace, Map<? extends Serializable, ? extends List<? extends Serializable>> pkeySkeyListMap );

    /**
     * 多主key多子key版本的prefixGetHidden
     * @param namespace 数据所在的namespace
     * @param pkeySkeyListMap 每个主key对应的子key列表
     * @return 整体返回码：SUCCESS/PARTSUCC, 若全部失败则为server处理的最后一个返回码
     *         每一个主key下对应的每一个子key的value
     */
    public Result<Map<Object, Map<Object, Result<DataEntry>>>>
        mprefixGetHiddens(int namespace, Map<? extends Serializable, ? extends List<? extends Serializable>> pkeySkeyListMap );

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
   //Result<List<DataEntry>> getRange(int namespace, Serializable prefix,
   //   Serializable key_start, Serializable key_end, int offset, int limit, boolean reverse);

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
  //Result<List<DataEntry>> getRangeOnlyValue(int namespace, Serializable prefix,
      //Serializable key_start, Serializable key_end, int offset, int limit, boolean reverse);

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

  //Result<List<DataEntry>> getRangeOnlyKey(int namespace, Serializable prefix,
      //Serializable key_start, Serializable key_end, int offset, int limit, boolean reverse);


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
   //Result<List<DataEntry>> delRange(int namespace, Serializable prefix,
      //Serializable key_start, Serializable key_end, int offset, int limit, boolean reverse);

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
    //Result<Integer> incr(int namespace, Serializable key, int value,
			//int defaultValue, int expireTime, int lowBound, int upperBound);

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
    //Result<Integer> decr(int namespace, Serializable key, int value,
			//int defaultValue, int expireTime, int lowBound, int upperBound);


	/**
	 * 将key对应的计数设置成count，忽略key原来是否存在以及是否是计数类型。
	 * 因为Tair中计数的数据有特别标志，所以不能直接使用put设置计数值。
	 *
	 * @param namespace
	 *            数据所在的namspace
	 * @param key
	 *            数据的key
	 * @param count
	 *            要设置的值
	 */
	ResultCode setCount(int namespace, Serializable key, int count);

	/**
	 * 将key对应的计数设置成count，忽略key原来是否存在以及是否是计数类型。
	 * 因为Tair中计数的数据有特别标志，所以不能直接使用put设置计数值。
	 *
	 * @param namespace
	 *            数据所在的namspace
	 * @param key
	 *            数据的key
	 * @param count
	 *            要设置的值
	 * @param version
	 *            版本，不关心并发，传入0
	 * @param expireTime
	 *            过期时间，不使用传入0
	 */
	ResultCode setCount(int namespace, Serializable key, int count, int version, int expireTime);

	/**
	 * 锁住一个key，不再允许更新, 允许读和删除。
	 * @param namespace  数据所在的namespace
	 * @param key  数据的key
	 * @return 如果数据不存在，返回不存在；如果数据存在但已经被lock，返回lock已经存在的错误码；
	 *         否则成功。
	 */
	ResultCode lock(int namespace, Serializable key);


	/**
	 * 解锁一个key。
	 * @param namespace  数据所在的namespace
	 * @param key  数据的key
	 * @return 如果数据不存在，返回不存在；如果数据存在但未被lock，返回lock不存在的错误码；
	 *         否则成功。
	 */
	ResultCode unlock(int namespace, Serializable key);

	/**
	 * 批量锁key。
	 * @param namespace  数据所在的namespace
	 * @param keys  数据的key
	 * @return Result.getRc()是返回的ResultCode, 如果都成功, 返回成功；
	 *         如果返回PARTSUCC, 则Result.getValue()为成功的key.
	 */
	Result<List<Object>> mlock(int namespace, List<? extends Object> keys);

	/**
	 * 批量锁key。
	 * @param namespace  数据所在的namespace
	 * @param keys  数据的key
	 * @param failKeysMap 传入保存失败的key
	 * @return Result.getRc()是返回的ResultCode, 如果都成功, 返回成功；
	 *         如果返回PARTSUCC, 则Result.getValue()为成功的key,并且如果传入failKeysMap不为null，
	 *         failKeysMap为失败的key以及对应的错误码。
	 */
	Result<List<Object>> mlock(int namespace, List<? extends Object> keys, Map<Object, ResultCode> failKeysMap);

	/**
	 * 批量解锁key。
	 * @param namespace  数据所在的namespace
	 * @param keys  数据的key
	 * @return Result.getRc()是返回的ResultCode, 如果都成功, 返回成功；
	 *         如果返回PARTSUCC, 则Result.getValue()为成功的key.
	 */
	Result<List<Object>> munlock(int namespace, List<? extends Object> keys);

	/**
	 * 批量解锁key。
	 * @param namespace  数据所在的namespace
	 * @param keys  数据的key
	 * @param failKeysMap 传入保存失败的key
	 * @return Result.getRc()是返回的ResultCode, 如果都成功, 返回成功；
	 *         如果返回PARTSUCC, 则Result.getValue()为成功的key,并且如果传入failKeysMap不为null，
	 *         failKeysMap为失败的key以及对应的错误码。
	 */
	Result<List<Object>> munlock(int namespace, List<? extends Object> keys, Map<Object, ResultCode> failKeysMap);

	/**
	 * append value to key work with withHeader == false, and destination key/value must be byte[]/byte[]
	 * @param namespace  data's namespace
	 * @param key        data's key
	 * @param value      data's value
	 * @return return ResultCode.SUCCESS mean request success, otherwise fail
	 */
	ResultCode append(int namespace, byte[] key, byte[] value);
	 
	/**
	 * 得到统计信息
	 * @param qtype 统计类型
	 * @param groupName 统计的group name
	 * @param serverId 统计的服务器
	 * @return 统计的 结果:统计项和统计值对
	 */
	Map<String,String> getStat(int qtype, String groupName, long serverId);

    /**
     * 设置最大失败次数，当失败次数超过该值
     * 会主动向CS获取group信息
     */
    public void setMaxFailCount(int failCount);

    /**
     * 获取最大失败次数，当失败次数超过该值
     * 会主动向CS获取group信息
     */
    public int getMaxFailCount();

	/**
	 * 获取客户端的版本
	 */
	String getVersion();
}
