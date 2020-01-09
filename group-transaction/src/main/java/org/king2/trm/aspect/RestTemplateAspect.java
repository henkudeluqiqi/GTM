package org.king2.trm.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.king2.trm.cache.TransactionCache;
import org.king2.trm.pojo.TransactionPojo;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.Map;

/**
 * =======================================================
 * 说明:  RestTemplate切面类
 * 作者		                时间					    注释
 *
 * @author 俞烨             2020/1/9/9:06          创建
 * =======================================================
 */
@Aspect
@Component
public class RestTemplateAspect implements Ordered {

    @Around("execution(* " +
            "org.springframework.web.client.RestTemplate.getForObject( ..))")
    public void getForObject(ProceedingJoinPoint pjp) throws Exception, Throwable {

        /**
         * 取出方法 判断方法的参数个数，好知道如何调用
         */
        Method invokeMethod = ((MethodSignature) pjp.getSignature ()).getMethod ();
        Class<?>[] parameterTypes = invokeMethod.getParameterTypes ();
        // 定义最后的参数是Map还是Object...
        boolean finalParaTypeIsMap = false;

        // 经过测试，调用的都是三个参数的数据，所以我们只能通过类型去判断
        if (parameterTypes[2].getName ().equals (Map.class.getName ())) {
            // 是map类型
            finalParaTypeIsMap = true;
        }

        // 我们需要做的就是将groupId带入到下游系统去
        TransactionPojo transactionPojo = TransactionCache.CURRENT_TD.get ();
        if (transactionPojo == null) {
            throw new RuntimeException ("获取上游系统事务组的ID，失败....");
        }
        String groupId = transactionPojo.getGroupId ();
        // 将事务组的ID带入参数中
        Object[] args = pjp.getArgs ();
        // 那么就需要动态改变URL的参数了
        String url = (String) args[0];
        if (url.indexOf ("?") > 0) {
            args[0] = url + "&groupId=" + groupId;
        } else {
            args[0] = url + "?groupId=" + groupId;
        }


        /*if (finalParaTypeIsMap) {
            // 为Map添加参数信息
            Map<String, Object> maps = (Map<String, Object>) args[2];
            maps.put ("groupId", groupId);
        } else {
            // 那么就需要动态改变URL的参数了
            String url = (String) args[0];
            if (url.indexOf ("?") > 0) {
                args[0] = url + "&groupId=" + groupId;
            } else {
                args[0] = url + "?groupId=" + groupId;
            }
        }*/

        // 执行方法
        pjp.proceed (args);
    }


    /**
     * 让我们这个切面高于别人的切面
     *
     * @return
     */
    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
