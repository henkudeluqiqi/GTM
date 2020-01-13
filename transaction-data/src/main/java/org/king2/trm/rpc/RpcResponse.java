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
     * 本次事务的最终状态，这是迎合全部的一个状态
     */
    private TransactionType finalTransactionType;

    /**
     * 他自己的一个状态
     */
    private TransactionType oneType = TransactionType.COMMIT;
    /**
     * 本次事务的id
     */
    private String trmId;
    /**
     * 通讯的管道
     */
    private ChannelHandlerContext chx;
    /**
     * 这次事务的分组ID
     */
    private String groupId;

    /**
     * 请求的路径
     */
    private String requestURL;

    public RpcResponse(TransactionType finalTransactionType, String trmId, ChannelHandlerContext chx, String groupId,
                       String requestURL) {
        this.finalTransactionType = finalTransactionType;
        this.trmId = trmId;
        this.chx = chx;
        this.groupId = groupId;
        this.requestURL = requestURL;
    }


    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public TransactionType getOneType() {
        return oneType;
    }

    public void setOneType(TransactionType oneType) {
        this.oneType = oneType;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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
