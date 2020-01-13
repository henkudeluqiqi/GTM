package org.king2.trm.pojo;

/**
 * =======================================================
 * 说明:  Redis中的key
 * 作者		                时间					    注释
 *
 * @author 俞烨             2020/1/12/12:49          创建
 * =======================================================
 */
public enum RedisKey {

    /**
     * GTM发出了ROLLBACK的key
     */
    GTM_ROLLBACK_KEY,
    /**
     * GTM发出COMMIT的key
     */
    GTM_COMMIT_KEY;
}
