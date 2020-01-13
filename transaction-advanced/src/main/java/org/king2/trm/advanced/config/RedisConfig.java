package org.king2.trm.advanced.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis配置中心
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisProperties redisProperties() {
        return new RedisProperties();
    }

    @Bean
    public JedisPool jedisPool() {
        RedisProperties redisProperties = redisProperties();
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(redisProperties.getJedis().getPool().getMaxIdle());
        config.setMaxTotal(redisProperties.getJedis().getPool().getMaxActive());
        config.setMaxWaitMillis(redisProperties.getJedis().getPool().getMaxWait().toMillis());
        JedisPool pool = new JedisPool(config, redisProperties.getHost(), redisProperties.getPort(), 10000);
        return pool;
    }
}
