# Seata

## 学习地址
- https://blog.csdn.net/zhuocailing3390/article/details/136999236

## seata 是什么

Seata 是一款开源的分布式事务解决方案，致力于提供高性能和简单易用的分布式事务服务。Seata 将为用户提供了 AT、TCC、SAGA 和 XA 事务模式，为用户打造一站式的分布式解决方案。

![img.png](img/img.png)

## 整体机制

两阶段提交协议的演变：
一阶段：
* 业务数据和回滚日志记录在同一个本地事务中提交，释放本地锁和连接资源。

二阶段：
* 提交异步化，非常快速地完成。
* 回滚通过一阶段的回滚日志进行反向补偿。


## 术语

TC (Transaction Coordinator) - 事务协调者
维护全局和分支事务的状态，驱动全局事务提交或回滚。

TM (Transaction Manager) - 事务管理器
定义全局事务的范围：开始全局事务、提交或回滚全局事务。

RM (Resource Manager) - 资源管理器
管理分支事务处理的资源，与TC交谈以注册分支事务da't和报告分支事务的状态，并驱动分支事务提交或回滚。


## 部署

https://blog.csdn.net/zhuocailing3390/article/details/136999236