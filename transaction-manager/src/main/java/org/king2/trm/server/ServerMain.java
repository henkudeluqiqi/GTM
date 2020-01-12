package org.king2.trm.server;

import org.king2.trm.cache.ServerTransactionCache;
import org.king2.trm.cache.TransactionCache;
import org.king2.trm.enums.PropertiesConfigEnum;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * =======================================================
 * 说明:  启动类
 * 作者		                时间					    注释
 *
 * @author 俞烨             2020/1/7/10:50          创建
 * =======================================================
 */
public class ServerMain {

    /**
     * 端口号
     */
    public static Integer PORT;

    public static void main(String[] args) throws Exception {
        // 初始化配置文件
        properties ();
        // 启动
        new TrmServer ().bind (PORT);
    }

    /**
     * 初始化配置文件
     */
    public static void properties() throws IOException {


        // 开始读取配置文件
        File proFile = getFile ();
        InputStream stream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            stream = new FileInputStream (proFile);
            inputStreamReader = new InputStreamReader (stream);
            bufferedReader = new BufferedReader (inputStreamReader);

            /**
             * 获取完流以后要开始读取信息了
             * 定义一个存储配置信息的集合，我们将所有的配置文件读取到list当中后，统一进行处理
             */
            List<String> proConfigs = new ArrayList<String> ();
            String config = null;
            while ((config = bufferedReader.readLine ()) != null) {
                proConfigs.add (config);
            }

            /**
             * 初始化服务器默认的信息
             */
            initDefaultInfo ();

            /**
             * 读取完成后开始解析配置文件
             */
            parseConfig (proConfigs);

            /**
             * 解析完成 开始处理配置文件
             */
            processor ();

        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            if (stream != null) stream.close ();
            if (inputStreamReader != null) inputStreamReader.close ();
            if (bufferedReader != null) bufferedReader.close ();
        }

    }

    /**
     * 获取到File文件
     *
     * @return
     */
    public static File getFile() {
        // 首先先去环境中去取出参数
        String property = System.getProperty ("tm-config");
        File file = null;
        if (property != null && !"".equals (property)) {
            file = new File (property);
            if (!file.exists ()) {
                return file;
            }
        }

        // 获取到配置文件的路径
        String propertiesStr = Class.class.getClass ().getResource ("/").getPath () + "tm.properties";
        File proFile = new File (propertiesStr);
        if (!proFile.exists ()) {
            throw new RuntimeException ("读取配置文件失败，请在··resources··目录下创建名为``tm.properties``的文件");
        }

        return proFile;
    }


    /**
     * 解析配置文件
     */
    public static void parseConfig(List<String> proConfigs) {

        /**
         *  解析配置文件需要跳过解析的有
         *  #开头
         *  键值对以=号分割
         */
        for (String proConfig : proConfigs) {
            if (proConfig == null || "".equals (proConfig)) {
                continue;
            } else if (proConfig.substring (0, 1).equals ("#")) {
                continue;
            }

            // 分割配置信息
            String[] configs = proConfig.split ("=");
            if (configs.length != 2) {
                throw new RuntimeException ("配置文件出错，配置信息中只能使用一个=，[" + proConfig + "]");
            }

            String key = configs[0].trim ();
            String value = configs[1];

            // 判断value是否存在·#·
            String[] valueSplit = value.split ("#");
            value = valueSplit[0].trim ();

            // 将信息存入Map中
            TransactionCache.PROPERTIES_CONFIG.put (key, value);
        }
    }

    /**
     * 初始化服务器默认的信息
     */
    private static void initDefaultInfo() {

        // 初始化默认的端口
        TransactionCache.PROPERTIES_CONFIG.put (PropertiesConfigEnum.GTM_SERVER_PORT, "8888");
        TransactionCache.PROPERTIES_CONFIG.put (PropertiesConfigEnum.ADD_REDIS_FLAG, "true");
    }

    /**
     * 开始处理配置文件
     */
    private static void processor() {

        // 取出服务端的端口
        String gtmPort = TransactionCache.PROPERTIES_CONFIG.get (PropertiesConfigEnum.GTM_SERVER_PORT);
        if (!gtmPort.matches ("[0-9]{1,}")) {
            throw new RuntimeException ("端口号配置错误,端口应该为int类型[gtm-server-port]");
        }
        ServerMain.PORT = Integer.parseInt (gtmPort);

        // 判断是否需要解析Redis的配置文件
        processorRedis ();
    }

    /**
     * 处理Redis的信息
     */
    private static void processorRedis() {
        String activeRedis = TransactionCache.PROPERTIES_CONFIG.get (PropertiesConfigEnum.ACTIVE_REDIS_CACHE);
        if (!"true".equals (activeRedis)) {
            return;
        }

        // 开始处理Redis的一些信息
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig ();
        // 设置一些参数信息
        String redisPoolMaxIdle = TransactionCache.PROPERTIES_CONFIG.get (PropertiesConfigEnum.REDIS_POOL_MAX_IDLE);
        checkInfo (PropertiesConfigEnum.REDIS_POOL_MAX_IDLE, redisPoolMaxIdle);
        jedisPoolConfig.setMaxIdle (Integer.parseInt (redisPoolMaxIdle));

        String redisPoolMinIdle = TransactionCache.PROPERTIES_CONFIG.get (PropertiesConfigEnum.REDIS_POOL_MIN_IDLE);
        checkInfo (PropertiesConfigEnum.REDIS_POOL_MIN_IDLE, redisPoolMinIdle);
        jedisPoolConfig.setMinIdle (Integer.parseInt (redisPoolMaxIdle));

        String redisPoolMaxActive = TransactionCache.PROPERTIES_CONFIG.get (PropertiesConfigEnum.REDIS_POOL_MAX_ACTIVE);
        checkInfo (PropertiesConfigEnum.REDIS_POOL_MAX_ACTIVE, redisPoolMaxActive);
        jedisPoolConfig.setMaxTotal (Integer.parseInt (redisPoolMaxActive));

        String redisPoolMaxWait = TransactionCache.PROPERTIES_CONFIG.get (PropertiesConfigEnum.REDIS_POOL_MAX_WAIT);
        checkInfo (PropertiesConfigEnum.REDIS_POOL_MAX_WAIT, redisPoolMaxWait);
        jedisPoolConfig.setMaxWaitMillis (Long.parseLong (redisPoolMaxWait));

        // 创建JedisPool
        String redisPort = TransactionCache.PROPERTIES_CONFIG.get (PropertiesConfigEnum.REDIS_PORT);
        checkInfo (PropertiesConfigEnum.REDIS_PORT, redisPort);
        JedisPool jedisPool = new JedisPool (jedisPoolConfig, TransactionCache.PROPERTIES_CONFIG.get (PropertiesConfigEnum.REDIS_HOST
        ), Integer.parseInt (redisPort), 10000);

        // 测试redis是否连接成功
        try {
            Jedis resource = jedisPool.getResource ();
            resource.close ();
        } catch (Exception e) {
            e.printStackTrace ();
        }

        // 将RedisPool存入缓存中，供后面的调用
        ServerTransactionCache.JEDISPOOL = jedisPool;

        System.out.println ("---->>>>>>>>>>>>>>>>> Redis连接成功");
    }

    /**
     * 校验int类型的参数信息
     *
     * @param key
     * @param info
     */
    private static void checkInfo(String key, String info) {
        if (info == null || "".equals (info)) {
            throw new RuntimeException (key + "不能为空");
        }

        try {
            Integer.parseInt (info);
        } catch (Exception e) {
            throw new RuntimeException (key + "必须为int类型");
        }
    }


}
