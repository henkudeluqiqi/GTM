# TM
Group-Transcation-Manager
事务管理者，平常的单线程的事务，我们可以整合Spring，然后将事务交给Spring去管理，但是在分布式的环境下，这样的做法显然是不同的，
所以诞生出了TransactionManage(TM)事务管理者，由他向一个事务组里面所有的事务发送ROLLBACK||COMMIT信息。


# TMData
transaction-data 事务管理用到的一些信息和参数。

# GroupTransaction
group-transaction 主要是创建了一个分布式的分布事务，使用者只需要在方法上标上@GroupTransaction注解，即可使用。

# 分布式事务框架GTM的设计图
![Image text](https://github.com/henkudeluqiqi/GTM/raw/master/框架流程.png)
