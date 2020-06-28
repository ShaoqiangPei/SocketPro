## TmServerManager使用说明

### 概述
`TmServerManager`是`mina实现tcp长连接`的服务端封装。利用它可以快速新建一个服务端用于测试。  
要了解对应客户端，可参看[TmClientManager使用说明](https://github.com/ShaoqiangPei/SocketPro/blob/master/read/TmClientManager%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E.md)

### 使用说明
#### 一. jar包依赖
涉及到mina的jar包有：
```
mina-core-2.0.16.jar
slf4j-jdk14-1.7.21.jar
slf4j-api-1.7.21.jar
```
此三个jar包已经在库中集成了，大家只需要了解就行。
#### 二. 在 主函数 中的使用
在主函数中，你可以像下面这样新启一个服务端:
```
public class TestJava {


    public static void main(String[] args) {
        SocketUtil.setDebug(true);

        TmServerManager tmServerManager = new TmServerManager();
        tmServerManager.setPort(9124)
                .setCharsetName(SocketConfig.UTF_8)//设置字符编码集，若不设置则默认 UTF-8
                .setReadBufferSize(2048)//设置接收缓存区大小，不设置的话默认为 2048
                .setIdleTime(10)//设置服务回到空闲状态时间间隔,不设置则默认10秒
//                .setHeartBeat(true)//是否开启心跳机制，默认不开启
//                .setHbDelayTime(10)//设置接收心跳时间间隔(单位秒),若不设置则默认10秒(心跳开启生效)
//                //心跳反馈(心跳开启生效)
//                .setOnServerHeartBeatListener(new ServerHeartBeatFactory.OnServerHeartBeatListener() {
//                    @Override
//                    public boolean isResponse(IoSession ioSession, Object obj) {
//                        if("MainClient".equals(obj.toString())){
//                            return true;
//                        }
//                        return false;
//                    }
//
//                    @Override
//                    public Object getRequest(IoSession ioSession) {
//                        return "MainServer";
//                    }
//                })
                //通讯应答
                .setOnTmsHandlerListener(new TmServerHandler.OnTmsHandlerListener() {
                    @Override
                    public String messageHandler(IoSession session, Object message) {

                        System.out.println("======我是收到消息====message="+message.toString());
                        if(null!=message.toString()){
                            //服务端做出应答
                            return "我是服务端的亚瑟";
                        }
                        return null;
                    }
                }).start();//启动服务
    }
}
```
其中心跳机制是默认不开启的，可以不设置，当需要的时候则仔细那个设置。

#### 三. 需要注意的问题
若与客户端建立连接失败，可参考以下几方面:
```
1.mina_tcp客户端与服务端链接地址(ip)不一致
2.mina_tcp客户端与服务端链接端口(port)不一致
3.mina_tcp服务端未开启
4.mina_tcp客户端未联网或未开联网权限
5.mina_tcp客户端与服务端不在一个网段
```
若服务端收不到客户端发送的消息，也可能是两端字符编码或数据加密解密不一致造成的，因为服户端已经使用了自定义编码过滤器
```
MessageCodecFactory
```
对数据接收做了处理，故客户端也要使用此类对数据进行处理。

