package org.king2.trm.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * =======================================================
 * 说明:  线程池
 * 作者		                时间					    注释
 *
 * @author 俞烨             2020/1/7/11:08          创建
 * =======================================================
 */
public class ThreadPool {
    private static ThreadPool ourInstance = new ThreadPool ();

    public static ThreadPool getInstance() {
        return ourInstance;
    }

    private ThreadPool() {
    }

    /**
     * 100线程的线程池
     */
    public static final ExecutorService POOL = Executors.newFixedThreadPool (100);
}
