package org.king2.trm.config;

import org.king2.trm.cache.TransactionCache;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * =======================================================
 * 说明:  动态扫描包路径，使用的技术就是Import，在Import一个普通类的时候，Spring还会进行解析一遍普通类。
 * 作者		                时间					    注释
 *
 * @author 俞烨             2020/1/9/10:49          创建
 * =======================================================
 */
@Configuration
@ComponentScan("org.king2.trm")
public class GTMConfig implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        TransactionCache.SPRING_CONTEXT = applicationContext;
    }
}
