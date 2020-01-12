package org.king2.trm.enums;

/**
 * =======================================================
 * 说明:  配置文件枚举
 * 作者		                时间					    注释
 *
 * @author 俞烨             2020/1/12/10:41          创建
 * =======================================================
 */
public class PropertiesConfigEnum {

    /**
     * redis的地址
     */
    public static final String REDIS_HOST = "redis-host";
    /**
     * redis端口号
     */
    public static final String REDIS_PORT = "redis-port";

    /**
     * 默认超时时间
     */
    public static final String REDIS_TIME_OUT = "redis-timeout";

    /**
     * 连接池最大连接数（使用负值表示没有限制）
     */
    public static final String REDIS_POOL_MAX_ACTIVE = "redis-pool-max-active";

    /**
     * 连接池最大阻塞等待时间（使用负值表示没有限制）
     */
    public static final String REDIS_POOL_MAX_WAIT = "redis-pool-max-wait";

    /**
     * 连接池中的最大空闲连接
     */
    public static final String REDIS_POOL_MAX_IDLE = "redis-pool-max-idle";

    /**
     * 连接池中的最小空闲连接
     */
    public static final String REDIS_POOL_MIN_IDLE = "redis-pool-min-idle";

    /**
     * 是否启用redis作为缓存中心
     */
    public static final String ACTIVE_REDIS_CACHE = "active-redis-cache";

    /**
     * GTM服务端的端口
     */
    public static final String GTM_SERVER_PORT = "gtm-server-port";

    /**
     * 是否将调用链存入缓冲中
     */
    public static final String ADD_REDIS_FLAG = "add-redis-flag";
}
