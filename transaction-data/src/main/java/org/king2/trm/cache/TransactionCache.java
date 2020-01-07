package org.king2.trm.cache;

import io.netty.channel.ChannelHandlerContext;
import org.king2.trm.rpc.RpcResponse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * =======================================================
 * 说明:  事务的缓存数据
 * 作者		                时间					    注释
 *
 * @author 俞烨             2020/1/7/10:19          创建
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
     * 存放着本次事务组的所有通讯管道
     * 主要用于，出现了回滚或者最终的COMMIT，需要通过事务组Id获取到所有的通讯管道，并发出对应的消息
     */
    public static final Map<String, List<RpcResponse>> TRM_GROUP_CACHE =
            new ConcurrentHashMap<String, List<RpcResponse>> ();

    /**
     * 最终的事务id
     * 主要用于，区分是否是最终的一个COMMIT_ID
     */
    @SuppressWarnings("all")
    public static final Map<String, String> FINAL_TRMID_CACHe =
            new ConcurrentHashMap<String, String> ();


    /**
     * 当前事务组是否已经进行了ROLLBACK
     */
    public static final Map<String, Boolean> CURRENT_TRM_GROUP_IS_ROLLBACK =
            new ConcurrentHashMap<String, Boolean> ();
}
