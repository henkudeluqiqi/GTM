package org.king2.trm.client;

/**
 * =======================================================
 * 说明:  服务端处理器
 * 作者		                时间					    注释
 *
 * @author 俞烨             2020/1/7/11:14          创建
 * =======================================================
 */

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.king2.trm.cache.TransactionCache;
import org.king2.trm.pojo.Task;
import org.king2.trm.pojo.TransactionPojo;
import org.king2.trm.pool.ThreadPool;
import org.king2.trm.rpc.RpcResponse;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    //处理服务端返回的数据
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {

        ThreadPool.POOL.execute (new Runnable () {
            @Override
            public void run() {
                try {
                    Thread.sleep (200);
                    response.setChx (null);
                    // 处理信息
                    TransactionPojo transactionPojo = null;

                    // 加锁获取信息
                    ReentrantReadWriteLock.ReadLock readLock = TransactionCache.rwLock.readLock ();
                    readLock.lock ();
                    try {
                        /*// 判断是否是ROLLBACK，如果是ROLLBACK的话需要将最终性的
                        if (!TransactionCache.ROLLBACK_NO_COMMIT.containsKey (response.getGroupId ()))
                            TransactionCache.ROLLBACK_NO_COMMIT.put (response.getGroupId (), response.getGroupId ());*/
                        transactionPojo = TransactionCache.TRM_POJO_CACHE.get (response.getTrmId ());
                    } catch (Exception e) {
                        e.printStackTrace ();
                    } finally {
                        readLock.unlock ();
                    }
                    transactionPojo.setTransactionType (response.getFinalTransactionType ());
                    Task task = transactionPojo.getTask ();
                    if (!task.getFlag ()) {
                        System.out.println ("先发送了消息");
                    }
                    task.signalTask ();
                } catch (Exception e) {
                    e.printStackTrace ();
                }
            }
        });
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive (ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close ();
    }


}
