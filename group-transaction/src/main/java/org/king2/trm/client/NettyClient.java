package org.king2.trm.client;

/**
 * =======================================================
 * 说明:  服务端
 * 作者		                时间					    注释
 *
 * @author 俞烨             2020/1/7/11:12          创建
 * =======================================================
 */

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.king2.trm.pojo.TransactionPojo;
import org.king2.trm.rpc.RpcDecoder;
import org.king2.trm.rpc.RpcEncoder;
import org.king2.trm.rpc.RpcResponse;

public class NettyClient {

    private final String host;
    private final int port;
    private Channel channel;

    public static NettyClient client;

    //连接服务端的端口号地址和端口号
    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        final EventLoopGroup group = new NioEventLoopGroup ();

        Bootstrap b = new Bootstrap ();
        b.group (group).channel (NioSocketChannel.class)  // 使用NioSocketChannel来作为连接用的channel类
                .handler (new ChannelInitializer<SocketChannel> () { // 绑定连接初始化器
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        System.out.println ("正在连接TM事务管理器中...");
                        ChannelPipeline pipeline = ch.pipeline ();
                        pipeline.addLast (new RpcEncoder (TransactionPojo.class)); //编码request
                        pipeline.addLast (new RpcDecoder (RpcResponse.class)); //解码response
                        pipeline.addLast (new ClientHandler ()); //客户端处理类

                    }
                });
        //发起异步连接请求，绑定连接端口和host信息
        final ChannelFuture future = b.connect (host, port).sync ();

        future.addListener (new ChannelFutureListener () {

            @Override
            public void operationComplete(ChannelFuture arg0) throws Exception {
                if (future.isSuccess ()) {
                    System.out.println ("连接TM事务管理器成功");

                } else {
                    System.out.println ("连接TM事务管理器失败");
                    future.cause ().printStackTrace ();
                    group.shutdownGracefully (); //关闭线程组
                }
            }
        });

        this.channel = future.channel ();
    }

    public Channel getChannel() {
        return channel;
    }
}
