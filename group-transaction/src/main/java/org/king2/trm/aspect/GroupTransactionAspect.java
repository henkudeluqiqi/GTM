package org.king2.trm.aspect;

import io.netty.channel.Channel;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.king2.trm.TransactionType;
import org.king2.trm.annotations.GroupTransaction;
import org.king2.trm.cache.TransactionCache;
import org.king2.trm.client.NettyClient;
import org.king2.trm.pojo.TransactionPojo;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * =======================================================
 * 说明:  GroupTransaction事务的切面类
 * 作者		                时间					    注释
 *
 * @author 俞烨             2020/1/7/11:02          创建
 * =======================================================
 */
@Component
@Aspect
public class GroupTransactionAspect {


    @Around("@annotation(org.king2.trm.annotations.GroupTransaction)")
    public void around(ProceedingJoinPoint pjp) throws Throwable {
        /**
         * 切入到这个最重要的切面类里面了
         * 我们需要在这里做很多事情，需要判断是创建事务组还是注册事务组
         */
        Method method = ((MethodSignature) pjp.getSignature ()).getMethod ();
        GroupTransaction annotation = method.getAnnotation (GroupTransaction.class);
        if (annotation == null) pjp.proceed ();

        // 取出注解的信息
        String groupId = "";
        String trmId = "";

        // 判断当前线程是否是由上游系统调用过来的
        String parentGroupId = TransactionCache.CURRENT_GROUP_ID.get ();
        if (parentGroupId == null || "".equals (parentGroupId)) {
            if (annotation.isCreate ()) {
                // 需要去创建事务组
                TransactionPojo trmGroup = createTrmGroup ();
                groupId = trmGroup.getGroupId ();
                trmId = trmGroup.getTrmId ();
            }
        } else {
            TransactionPojo register = register (parentGroupId);
            groupId = parentGroupId;
            trmId = register.getTrmId ();
        }

        /**
         * 去执行下一个切面或者方法，这样程序会去执行到你的程序，当你程序出现了异常以后你可以进行try cache
         * 然后并发出回滚的信息
         * 如果说执行的sql或者程序没有出现异常，那么就会进入到我们自己配置好的connection.commit()方法
         */
        try {
            pjp.proceed ();
            // 查看是否出了异常
            commit (groupId, trmId);
        } catch (Exception e) {
            rollback (groupId, trmId);
            e.printStackTrace ();
        } catch (Throwable e) {
            rollback (groupId, trmId);
            e.printStackTrace ();
        }


    }

    /**
     * 创建事务组
     *
     * @return
     */
    public TransactionPojo createTrmGroup() {
        Channel channel = NettyClient.client.getChannel ();
        String groupId = UUID.randomUUID ().toString ();
        String trmId = UUID.randomUUID ().toString ();
        // 封装参数
        TransactionPojo transactionPojo = new TransactionPojo (groupId, trmId, TransactionType.CREATE_TRM_GROUP);
        // 存入事务缓存中
        // 加锁
        transactionPojo.setRequestURL (TransactionCache.CURRENT_REQUEST_URL.get ());
        ReentrantReadWriteLock.WriteLock writeLock = TransactionCache.rwLock.writeLock ();
        writeLock.lock ();
        try {
            TransactionCache.TRM_POJO_CACHE.put (trmId, transactionPojo);
            TransactionCache.FINAL_TRM_ID.put (trmId, true);
        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            writeLock.unlock ();
        }
        TransactionCache.CURRENT_TD.set (transactionPojo);
        // 发送消息
        channel.writeAndFlush (transactionPojo);
        return transactionPojo;
    }

    /**
     * 注册事务组
     *
     * @return
     */
    public TransactionPojo register(String groupId) {
        Channel channel = NettyClient.client.getChannel ();
        String trmId = UUID.randomUUID ().toString ();
        // 封装参数
        TransactionPojo transactionPojo =
                new TransactionPojo (groupId, trmId, TransactionType.REGISTER_TRM);
        transactionPojo.setRequestURL (TransactionCache.CURRENT_REQUEST_URL.get ());
        // 存入事务缓存中
        tpcPut (trmId, transactionPojo);
        TransactionCache.CURRENT_TD.set (transactionPojo);
        // 发送消息
        channel.writeAndFlush (transactionPojo);
        return transactionPojo;
    }

    /**
     * 发出回滚事务的通知
     *
     * @return
     */
    public TransactionPojo rollback(String groupId, String trmId) {
        Channel channel = NettyClient.client.getChannel ();
        // 封装参数
        TransactionPojo transactionPojo =
                new TransactionPojo (groupId, trmId, TransactionType.ROLLBACK);
        // 存入事务缓存中
        tpcPut (trmId, transactionPojo);
        TransactionCache.CURRENT_TD.set (transactionPojo);
        // 发送消息
        channel.writeAndFlush (transactionPojo);
        return transactionPojo;
    }

    /**
     * 发出提交事务的通知
     *
     * @return
     */
    public void commit(String groupId, String trmId) {
        // 判断是否是最终性的COMMIT
        if (TransactionCache.FINAL_TRM_ID.get (trmId) == null) {
            return;
        }
        // 加锁
        /*ReentrantReadWriteLock.ReadLock readLock = TransactionCache.rwLock.readLock ();
        readLock.lock ();

        try {
            // 判断是否已经回滚了 回滚以后就不用再继续进行提交了
            String rollbackNoCommitFlag = TransactionCache.ROLLBACK_NO_COMMIT.get (groupId);
            if (rollbackNoCommitFlag != null && !"".equals (rollbackNoCommitFlag)) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            readLock.unlock ();
        }*/
        Channel channel = NettyClient.client.getChannel ();
        // 封装参数
        TransactionPojo transactionPojo =
                new TransactionPojo (groupId, trmId, TransactionType.COMMIT);
        // 存入事务缓存中
        tpcPut (trmId, transactionPojo);
        TransactionCache.CURRENT_TD.set (transactionPojo);
        // 发送消息
        channel.writeAndFlush (transactionPojo);
    }

    public void tpcPut(String trmId, TransactionPojo transactionPojo) {
        // 加锁
        ReentrantReadWriteLock.WriteLock writeLock = TransactionCache.rwLock.writeLock ();
        writeLock.lock ();
        try {
            TransactionCache.TRM_POJO_CACHE.put (trmId, transactionPojo);
        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            writeLock.unlock ();
        }
    }
}
