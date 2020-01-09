package org.king2.trm.annotations;

import org.king2.trm.config.GTMConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * =======================================================
 * 说明: 启用GTM分布式事务注解
 * 作者		                时间					    注释
 *
 * @author 俞烨             2020/1/9/10:48          创建
 * =======================================================
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(GTMConfig.class)
public @interface EnableGTM {
}
