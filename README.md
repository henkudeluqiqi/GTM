# GTM
Group-Transcation-Manager
事务管理者，平常在一个线程上的事务我们可以教给Spring管理，但是在分布式的环境下，这样的做法显然是不行的，因为出了异常他只会回滚当前线程的事务，而不会回
滚调用链上的全部事务，这就产生了一个数据不一致的情况，所以就诞生了GTM分布式事务框架

# TM
Transaction-Manager
了TransactionManager(TM)事务管理者，由他向一个事务组里面所有的事务发送ROLLBACK||COMMIT信息。

# TMD
transaction-data 事务管理用到的一些信息和参数。

# AnnotationGTM
group-transaction 主要是创建了一个分布式的分布事务，使用者只需要在方法上标上@GroupTransaction注解，即可使用。

# 设计原则
无侵入，可插拔式，使用只需导入POM，以及@EnableGTM和@GroupTransaction注解，快速上手，暂时提供两种远程调用方式
1、HttpClient.get()方法
2、与RestTemplate进行了整合，使用者暂时只能使用getForObject(..)和postForObject(..)方法，如果使用其他的远程调用模板的话，那么分布式事务就不在为你服务。

# 分布式事务框架GTM的设计图
![Image text](https://github.com/henkudeluqiqi/GTM/raw/master/框架流程.png)

