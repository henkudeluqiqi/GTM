package org.king2.trm.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.king2.trm.client.NettyClient;
import org.king2.trm.connection.TrmConnection;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Connection;

/**
 * =======================================================
 * 说明:  事务连接的切面
 * 作者		                时间					    注释
 *
 * @author 俞烨             2020/1/7/10:59          创建
 * =======================================================
 */
@Component
@Aspect
public class TrmConnectionAspect {

    @Around("execution(* javax.sql.DataSource.getConnection(..))")
    public Connection connection(ProceedingJoinPoint pjp) throws Throwable {
        Connection oldConnection = (Connection) pjp.proceed ();
        return new TrmConnection (oldConnection);
    }

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        NettyClient client = new NettyClient ("localhost", 8888);
        client.start ();
        NettyClient.client = client;
    }
}
