## TmClientManager使用说明

### 概述
`TmClientManager`是一个利用`mina`实现`tcp长连接`客户端的封装类。利用此类可快速建立一个客户端连接。

### 使用说明
#### 一. jar包依赖
涉及到`mina`的jar包有：
```
mina-core-2.0.16.jar
slf4j-android-1.7.21.jar
slf4j-api-1.7.21.jar
```
此三个jar包已经在库中集成呢共，大家只需要了解就行。
#### 二. 在 Activity 中的使用
在`Activity`中使用时，其实是涉及到两个类的：
