package org.king2.trm.pojo;

import org.king2.trm.TransactionType;

import java.io.Serializable;

/**
 * =======================================================
 * 说明:  事务的返回和响应的信息
 * 作者		                时间					    注释
 *
 * @author 俞烨             2020/1/7/10:19          创建
 * =======================================================
 */
public class TransactionPojo implements Serializable {


    /**
     * 本次事务组的id
     */
    private String groupId;
    /**
     * 本次事务的id
     */
    private String trmId;
    /**
     * 本次事务的状态
     */
    private TransactionType transactionType;

    /**
     * 请求路径
     */
    private String requestURL;

    /**
     * 任务阻塞
     */
    private Task task;

    public TransactionPojo() {
    }


    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public TransactionPojo(String groupId, String trmId, TransactionType transactionType) {
        this.groupId = groupId;
        this.trmId = trmId;
        this.transactionType = transactionType;
        this.task = new Task ();
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
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

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
}
