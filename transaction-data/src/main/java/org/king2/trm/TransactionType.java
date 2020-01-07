package org.king2.trm;

import java.io.Serializable;

/**
 * =======================================================
 * 说明:  本次事务提交的类型
 * 作者		                时间					    注释
 *
 * @author 俞烨             2020/1/7/10:22          创建
 * =======================================================
 */
public enum TransactionType implements Serializable {

    /**
     * 创建事务组
     */
    CREATE_TRM_GROUP,
    /**
     * 注册事务
     */
    REGISTER_TRM,
    /**
     * 提交事务
     */
    COMMIT,
    /**
     * 回滚事务
     */
    ROLLBACK,
    /**
     * 无状态
     */
    NONE;
}
