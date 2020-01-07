package org.king2.trm.server;

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
        new TrmServer ().bind (PORT);
    }
}
