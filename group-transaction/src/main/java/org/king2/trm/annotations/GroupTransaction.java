package org.king2.trm.annotations;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * =======================================================
 * 说明:  分布式事务注解
 * 作者		                时间					    注释
 *
 * @author 俞烨             2020/1/7/10:55          创建
 * =======================================================
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Transactional
public @interface GroupTransaction {

    /**
     * 是否创建事务组
     *
     * @return
     */
    boolean isCreate() default false;

}
