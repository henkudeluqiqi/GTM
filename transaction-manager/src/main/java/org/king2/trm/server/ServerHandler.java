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
import org.king2.trm.cache.TransactionCache;
import org.king2.trm.pojo.TransactionPojo;
import org.king2.trm.rpc.RpcResponse;

import java.util.ArrayList;
import java.util.List;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    //接受client发送的消息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        // 将消息强转成我们自己的数据
        TransactionPojo transactionPojo = (TransactionPojo) msg;
        if (transactionPojo == null) return;


        // 判断消息的类型
        TransactionType transactionType = transactionPojo.getTransactionType ();
        if (transactionType.equals (TransactionType.CREATE_TRM_GROUP)) {
            // 创建事务组
            List<RpcResponse> rpcResponses = new ArrayList<RpcResponse> ();
            rpcResponses.add (new RpcResponse (TransactionType.NONE, transactionPojo.getTrmId (), ctx));
            // 将事务组数据存入缓存中
            TransactionCache.TRM_GROUP_CACHE.put (transactionPojo.getGroupId (),
                    rpcResponses);
            // 将最终事务ID存入缓存中
            TransactionCache.FINAL_TRMID_CACHe.put (transactionPojo.getGroupId (), transactionPojo.getTrmId ());
            System.out.println ("创建事务组---->>>" + JSON.toJSONString (transactionType));
        } else if (transactionType.equals (TransactionType.REGISTER_TRM)) {
            // 注册事务组
            // 取出原本的事务组的通讯管道
            TransactionCache.TRM_GROUP_CACHE.get (transactionPojo.getGroupId ())
                    .add (new RpcResponse (TransactionType.NONE, transactionPojo.getTrmId (), ctx));
            System.out.println ("注册事务组---->>>" + JSON.toJSONString (transactionType));
        } else if (transactionType.equals (TransactionType.COMMIT)) {
            // 判断当前事务组是否已经进行了处理
            if (TransactionCache.CURRENT_TRM_GROUP_IS_ROLLBACK.get (transactionPojo.getGroupId ()) != null &&
                    TransactionCache.CURRENT_TRM_GROUP_IS_ROLLBACK.get (transactionPojo.getGroupId ())) {
                System.out.println ("---->>>>>> 事务组" + transactionPojo.getGroupId () + "--已经进行了ROLLBACK...");
                return;
            }
            // COMMIT事务
            // 判断是否是最终性的事务id
            String trmId = TransactionCache.FINAL_TRMID_CACHe.get (transactionPojo.getGroupId ());
            if (trmId != null && !"".equals (trmId)) {
                // 说明是最终一致性
                for (RpcResponse rpcResponse : TransactionCache.TRM_GROUP_CACHE.get (transactionPojo.getGroupId ())) {
                    rpcResponse.setFinalTransactionType (TransactionType.COMMIT);
                    rpcResponse.getChx ().writeAndFlush (rpcResponse);
                    System.out.println ("发出COMMIT---->>>" + JSON.toJSONString (rpcResponse));
                }
            }
        } else if (transactionType.equals (TransactionType.ROLLBACK)) {
            // ROLLBACK事务
            // 告诉系统这个事务组已经进行处理
            TransactionCache.CURRENT_TRM_GROUP_IS_ROLLBACK.put (transactionPojo.getGroupId (), true);
            // 通过groupId取出所有的通讯管道
            for (RpcResponse rpcResponse : TransactionCache.TRM_GROUP_CACHE.get (transactionPojo.getGroupId ())) {
                rpcResponse.setFinalTransactionType (TransactionType.ROLLBACK);
                rpcResponse.getChx ().writeAndFlush (rpcResponse);
                System.out.println ("发出ROLLBACK---->>>" + JSON.toJSONString (rpcResponse));
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
