package org.king2.trm.cache;

import org.king2.trm.pojo.TransactionPojo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * =======================================================
 * 说明:  事务缓存
 * 作者		                时间					    注释
 *
 * @author 俞烨             2020/1/7/10:59          创建
 * =======================================================
 */
public class TransactionCache {
    private static TransactionCache ourInstance = new TransactionCache ();

    public static TransactionCache getInstance() {
        return ourInstance;
    }

    private TransactionCache() {
    }

    /**
     * 事务缓存
     * 主要用于后面根据事务id取出本次的数据
     */
    public static final Map<String, TransactionPojo> TRM_POJO_CACHE =
            new ConcurrentHashMap<> ();

    /**
     * 当前线程的ThreadLocal
     */
    public static final ThreadLocal<TransactionPojo> CURRENT_TD =
            new ThreadLocal<> ();


    /**
     * 事务的最终性ID
     */
    public static final Map<String, Boolean> FINAL_TRM_ID =
            new ConcurrentHashMap<> ();

    /**
     * 当前的groupId
     */
    public static final ThreadLocal<String> CURRENT_GROUP_ID =
            new ThreadLocal<> ();

}
