# SocketPro
## 简介
`SocketPro`是一个提供 `socket` 通讯的工具库,当开发者需要做服务端或客户端的调试时，可以接入本库快速实现一个简单的`客户端`或`服务端`，以帮助开发者实现socket通讯联调。  
`SocketPro`包含的通讯类型有:`TCP`,`UDP`.

### 使用说明
#### 一. 库依赖
在你project对应的buid.gradle中添加如下代码：
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
在你要使用的module对应的buid.gradle中添加如下代码(以0.0.1版本为例)：
```
	dependencies {
	        implementation 'com.github.ShaoqiangPei:SocketPro:0.0.1'
	}
```
在你项目的自定义Application类中对本库`Log`打印做控制：
```
   //开启log打印
   SocketUtil.setDebug(true);
```
#### 二. 主要功能类
##### TCP
[TcpClient](https://github.com/ShaoqiangPei/SocketPro/blob/master/read/TcpClient%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E.md) ———— tcp客户端工具类  





