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

public class ClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    //处理服务端返回的数据
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {

        ThreadPool.POOL.execute (new Runnable () {
            @Override
            public void run() {
                try {
                    response.setChx (null);
                    System.out.println (JSON.toJSONString (response));
                    // 处理信息
                    TransactionPojo transactionPojo = TransactionCache.TRM_POJO_CACHE.get (response.getTrmId ());
                    transactionPojo.setTransactionType (response.getFinalTransactionType ());
                    Task task = transactionPojo.getTask ();
                    if (!task.getFlag ()) {
                        Thread.sleep (2000);
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
