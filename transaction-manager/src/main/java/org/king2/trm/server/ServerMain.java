package org.king2.trm.server;

import java.io.*;

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
    public static Integer PORT = 8888;

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
        // 获取到配置文件的路径
        String propertiesStr = Class.class.getClass ().getResource ("/").getPath () + "tm.properties";
        File proFile = new File (propertiesStr);
        if (!proFile.exists ()) {
            throw new RuntimeException ("读取配置文件失败，请在··resources··目录下创建名为``tm.properties``的文件");
        }
        // 开始读取配置文件
        InputStream stream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            stream = new FileInputStream (proFile);
            inputStreamReader = new InputStreamReader (stream);
            bufferedReader = new BufferedReader (inputStreamReader);
        } catch (Exception e) {

        } finally {
            if (stream != null) stream.close ();
            if (inputStreamReader != null) inputStreamReader.close ();
            if (bufferedReader != null) bufferedReader.close ();
        }

    }
}
