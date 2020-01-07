package org.king2.trm.rpc;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * =======================================================
 * 说明:  Rpc请求的编码器
 * 作者		                时间					    注释
 *
 * @author 俞烨             2020/1/7/10:15          创建
 * =======================================================
 */
public class RpcEncoder extends MessageToByteEncoder {

    //目标对象类型进行编码
    private Class<?> target;

    public RpcEncoder(Class target) {
        this.target = target;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (target.isInstance (msg)) {
            byte[] data = JSON.toJSONBytes (msg);
            out.writeInt (data.length);
            out.writeBytes (data);
        }
    }

}
