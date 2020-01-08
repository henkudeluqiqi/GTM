# TM
Group-Transcation-Manager
事务管理者，平常的单线程的事务，我们可以整合Spring，然后将事务交给Spring去管理，但是在分布式的环境下，这样的做法显然是不同的，
所以诞生出了TransactionManage(TM)事务管理者，由他向一个事务组里面所有的事务发送ROLLBACK||COMMIT信息。
