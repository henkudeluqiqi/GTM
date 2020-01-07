package org.king2.trm.rpc;

import io.netty.channel.ChannelHandlerContext;
import org.king2.trm.TransactionType;

import java.io.Serializable;

/**
 * =======================================================
 * 说明:  响应回去的对象
 * 作者		                时间					    注释
 *
 * @author 俞烨             2020/1/7/10:40          创建
 * =======================================================
 */
public class RpcResponse implements Serializable {

    /**
     * 本次事务的最终状态
     */
    private TransactionType finalTransactionType;
    /**
     * 本次事务的id
     */
    private String trmId;
    /**
     * 通讯的管道
     */
    private ChannelHandlerContext chx;

    public RpcResponse(TransactionType finalTransactionType, String trmId, ChannelHandlerContext chx) {
        this.finalTransactionType = finalTransactionType;
        this.trmId = trmId;
        this.chx = chx;
    }

    public String getTrmId() {
        return trmId;
    }

    public void setTrmId(String trmId) {
        this.trmId = trmId;
    }

    public ChannelHandlerContext getChx() {
        return chx;
    }

    public void setChx(ChannelHandlerContext chx) {
        this.chx = chx;
    }

    public TransactionType getFinalTransactionType() {
        return finalTransactionType;
    }

    public void setFinalTransactionType(TransactionType finalTransactionType) {
        this.finalTransactionType = finalTransactionType;
    }
}
