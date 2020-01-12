package org.king2.trm.server;

/**
 * =======================================================
 * 说明:  服务端接收到信息的处理
 * 作者		                时间					    注释
 *
 * @author 俞烨             2020/1/7/10:20          创建
 * =======================================================
 */

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.king2.trm.TransactionType;
import org.king2.trm.cache.ServerTransactionCache;
import org.king2.trm.cache.TransactionCache;
import org.king2.trm.enums.PropertiesConfigEnum;
import org.king2.trm.pojo.RedisKey;
import org.king2.trm.pojo.TransactionPojo;
import org.king2.trm.rpc.RpcResponse;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static final ReentrantReadWriteLock readWrite = new ReentrantReadWriteLock ();
    private static final ReentrantReadWriteLock sizeReadWrite = new ReentrantReadWriteLock ();
    /**
     * 正常的调用链最大次数添加到缓存中去
     */
    public static Integer MAX_SIZE_ADD_CACHE = 100;

    /**
     * 当前添加的次数
     */
    public volatile static Integer CURRENT_ADD_SIZE = 0;

    //接受client发送的消息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        // 将消息强转成我们自己的数据
        TransactionPojo transactionPojo = (TransactionPojo) msg;
        if (transactionPojo == null) return;


        // 判断消息的类型
        TransactionType transactionType = transactionPojo.getTransactionType ();
        if (transactionType.equals (TransactionType.CREATE_TRM_GROUP)) {
            // 加锁
            readWrite.writeLock ().lock ();
            try {
                // 创建事务组
                List<RpcResponse> rpcResponses = new ArrayList<RpcResponse> ();
                rpcResponses.add (new RpcResponse (TransactionType.NONE, transactionPojo.getTrmId (), ctx,
                        transactionPojo.getGroupId ()));
                // 将事务组数据存入缓存中
                TransactionCache.TRM_GROUP_CACHE.put (transactionPojo.getGroupId (),
                        rpcResponses);
                // 将最终事务ID存入缓存中
                TransactionCache.FINAL_TRMID_CACHe.put (transactionPojo.getGroupId (), transactionPojo.getTrmId ());
                System.out.println ("创建事务组---->>>" + JSON.toJSONString (transactionType));
            } catch (Exception e) {
                e.printStackTrace ();
            } finally {
                readWrite.writeLock ().unlock ();
            }
        } else if (transactionType.equals (TransactionType.REGISTER_TRM)) {
            // 加锁
            readWrite.writeLock ().lock ();
            try {
                // 注册事务组
                // 取出原本的事务组的通讯管道
                List<RpcResponse> rpcResponses = TransactionCache.TRM_GROUP_CACHE.get (transactionPojo.getGroupId ());
                if (rpcResponses != null) {
                    rpcResponses.add (new RpcResponse (TransactionType.NONE, transactionPojo.getTrmId (), ctx,
                            transactionPojo.getGroupId ()));
                }
                System.out.println ("注册事务组---->>>" + JSON.toJSONString (transactionType));
            } catch (Exception e) {
                e.printStackTrace ();
            } finally {
                readWrite.writeLock ().unlock ();
            }
        } else if (transactionType.equals (TransactionType.COMMIT)) {
            readWrite.readLock ().lock ();
            setOneType (transactionPojo.getGroupId (), transactionPojo.getTrmId (), TransactionType.COMMIT);
            try {
                // 判断当前事务组是否已经进行了处理
                if (TransactionCache.CURRENT_TRM_GROUP_IS_ROLLBACK.get (transactionPojo.getGroupId ()) != null &&
                        TransactionCache.CURRENT_TRM_GROUP_IS_ROLLBACK.get (transactionPojo.getGroupId ()) != null &&
                        TransactionCache.CURRENT_TRM_GROUP_IS_ROLLBACK.get (transactionPojo.getGroupId ())) {
                    System.out.println ("---->>>>>> 事务组" + transactionPojo.getGroupId () + "--已经进行了ROLLBACK...");
                    return;
                }
                // COMMIT事务
                // 判断是否是最终性的事务id
                String trmId = TransactionCache.FINAL_TRMID_CACHe.get (transactionPojo.getGroupId ());
                if (trmId != null && !"".equals (trmId) && trmId.equals (transactionPojo.getTrmId ())) {
                    /**
                     * 需要判断最终一致性的状态
                     * 为什么需要判断呢？因为我们前面发生ROLLBACK的时候会去查看是否是最终的事务发出的，如果不是只是修改一下本次事务组的状态
                     */
                    TransactionType finalTrmType = TransactionCache.FINAL_TRM_TYPE.get (transactionPojo.getGroupId ());
                    if (finalTrmType == null || !finalTrmType.equals (TransactionType.ROLLBACK)) {
                        // 进行COMMIT
                        send (transactionPojo.getGroupId (), TransactionType.COMMIT);

                    } else {
                        // 进行ROLLBACK
                        send (transactionPojo.getGroupId (), TransactionType.ROLLBACK);
                    }


                    // 是最终事务发出的信息 ，那么就需要根据配置将一些缓存清除
                    clear (transactionPojo.getGroupId (), false);
                }
            } catch (Exception e) {
                e.printStackTrace ();
            } finally {
                readWrite.readLock ().unlock ();
            }
        } else if (transactionType.equals (TransactionType.ROLLBACK)) {
            readWrite.readLock ().lock ();
            try {
                setOneType (transactionPojo.getGroupId (), transactionPojo.getTrmId (), TransactionType.ROLLBACK);
                /**
                 * 需要判断是否是最终事务发过来的ROLLBACK，如果是ROLLBACK的话才发出rollback信息
                 * 如果不是最终事务发过来的ROLLBACK只需要将这个事务组的最终状态改为ROLLBACK就行
                 */
                String trmId = TransactionCache.FINAL_TRMID_CACHe.get (transactionPojo.getGroupId ());
                if (trmId != null && !"".equals (trmId) && trmId.equals (transactionPojo.getTrmId ())) {
                    // 告诉系统这个事务组已经进行处理
                    TransactionCache.CURRENT_TRM_GROUP_IS_ROLLBACK.put (transactionPojo.getGroupId (), true);
                    // 进行ROLLBACK
                    send (transactionPojo.getGroupId (), TransactionType.ROLLBACK);
                    // 是最终事务发出的信息 ，那么就需要根据配置将一些缓存清除
                    clear (transactionPojo.getGroupId (), true);
                } else {
                    // 修改最终一致性事务的状态
                    TransactionCache.FINAL_TRM_TYPE.put (transactionPojo.getGroupId (), TransactionType.ROLLBACK);
                    System.out.println ("下游系统发出ROLLBACK指令 >>>>>>>");
                }
            } catch (Exception e) {
                e.printStackTrace ();
            } finally {
                readWrite.readLock ().unlock ();
            }
        }
    }

    /**
     * 根据groupId拿出所有的事务，然后发出类型
     *
     * @param groupId
     * @param sendType
     */
    private static void send(String groupId, TransactionType sendType) {
        // 进行COMMIT
        for (RpcResponse rpcResponse : TransactionCache.TRM_GROUP_CACHE.get (groupId)) {
            rpcResponse.setFinalTransactionType (sendType);
            rpcResponse.getChx ().writeAndFlush (rpcResponse);
            System.out.println ("发出" + sendType + "---->>>" + JSON.toJSONString (rpcResponse));
        }
    }


    /**
     * 记录一个事务的状态
     *
     * @param groupId
     * @param trmId
     * @param transactionType
     */
    private static void setOneType(String groupId, String trmId, TransactionType transactionType) {
        List<RpcResponse> rpcResponses = TransactionCache.TRM_GROUP_CACHE.get (groupId);
        if (rpcResponses != null && rpcResponses.size () > 0) {
            for (RpcResponse rpcRespons : rpcResponses) {
                if (rpcRespons.getTrmId ().equals (trmId)) {
                    rpcRespons.setOneType (transactionType);
                    break;
                }
            }
        }
    }

    /**
     * 清空缓存
     *
     * @param groupId
     */
    private static void clear(String groupId, boolean flag) {
        // 判断默认是否需要清楚数据
        if (TransactionCache.clearFlag) {
            // 清空事务组对应的所有RpcResponse
            TransactionCache.TRM_GROUP_CACHE.remove (groupId);
            // 删除最终事务Id
            TransactionCache.FINAL_TRMID_CACHe.remove (groupId);
            // 删除最终事务是否已经ROLLBACK了
            TransactionCache.CURRENT_TRM_GROUP_IS_ROLLBACK.remove (groupId);
            // 删除最终事务的状态
            TransactionCache.FINAL_TRM_TYPE.remove (groupId);
        } else {
            // 开锁
            sizeReadWrite.writeLock ().lock ();
            try {
                /**
                 *  需要存入的话 我们需要判断是否是立马存入，因为这里区分了ROLLBACK和COMMIT的调用链
                 *  如果是ROLLBACK的话就需要立马存入缓存中，因为COMMIT的调用链没有那么重要
                 */
                // 判断是否存入Redis中
                if ("true".equals (TransactionCache.PROPERTIES_CONFIG.get (PropertiesConfigEnum.ACTIVE_REDIS_CACHE))) {
                    // 存入redis
                    if (flag) {
                        /**
                         * 立马存入说明已经发出ROLLBACK请求了 ，所以我们需要立马存入
                         */
                        addRedis (RedisKey.GTM_ROLLBACK_KEY + "", JSON.toJSONString (TransactionCache.TRM_GROUP_CACHE.get (groupId)));
                    } else {
                        // 非
                        if (CURRENT_ADD_SIZE++ > MAX_SIZE_ADD_CACHE) {
                            // 将当前添加的次数重置
                            CURRENT_ADD_SIZE = 0;
                            // 存入缓存中
                            addRedis (RedisKey.GTM_COMMIT_KEY + "", JSON.toJSONString (TransactionCache.TRM_GROUP_CACHE.get (groupId)));
                        }
                    }

                } else {
                    // 存入其他缓存
                    addElseCache ();
                }
            } catch (Exception e) {
                e.printStackTrace ();
            } finally {
                sizeReadWrite.writeLock ().unlock ();
            }
        }
    }

    /**
     * 将数据存入其他缓冲中
     */
    public static void addElseCache() {
    }

    /**
     * 将数据添加到Redis中
     */
    public static void addRedis(String key, String value) {
        Jedis jedis = null;
        JedisPool jedispool = ServerTransactionCache.JEDISPOOL;
        try {
            jedis = jedispool.getResource ();
            // 存入数据
            jedis.lpush (key, value);
        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            if (jedis != null) {
                jedis.close ();
            }
        }
    }

    //通知处理器最后的channelRead()是当前批处理中的最后一条消息时调用
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println ("服务端接收数据完毕..");
        ctx.flush ();
    }

    //读操作时捕获到异常时调用
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close ();
    }

    //客户端去和服务端连接成功时触发
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println ("连接成功");
    }
}
