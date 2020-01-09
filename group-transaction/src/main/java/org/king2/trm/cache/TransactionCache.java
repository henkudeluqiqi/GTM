package org.king2.trm.cache;

import org.king2.trm.TransactionType;
import org.king2.trm.pojo.TransactionPojo;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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

    /**
     * 读写锁
     */
    public static final ReentrantReadWriteLock rwLock =
            new ReentrantReadWriteLock ();

    /**
     * rollback以后就不需要commit的数据
     * group->group
     */
    public static final Map<String, String> ROLLBACK_NO_COMMIT =
            new ConcurrentHashMap<> ();


    /**
     * spring容器
     */
    public static ApplicationContext SPRING_CONTEXT;

}
