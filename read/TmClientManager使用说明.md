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
在`Activity`中使用时，其实是涉及到两个类的：`TmClientConfig`与`TmClientManager`，其中`TmClientConfig`是做通讯的一些基本设置，`TmClientManager`是处理通讯的连接和断开的。  
下面看看在`Activity`中具体使用吧:
```
    //声明tcp客户端操作对象和配置对象
    private TmClientConfig mConfig;
    private TmClientManager mTmClientManager;
    
        /**初始化数据**/
    private void initData() {
        //初始化tcp配置
        mConfig = new TmClientConfig.Builder(TempActivity.this)
                .setIp("192.168.50.152")//设置服务端ip
                .setPort(9124)//设置端口(0-65535)之间
//                .setConnectTimeOut(30000)//设置连接超时时间，单位毫秒,默认30000，即30秒
//                .setCharsetName(SocketConfig.UTF_8)//设置字符集，默认为 “UTF-8”
//                .setReadBufferSize(2048)//设置接收数据缓存区,默认2048
//                .setReconnect(false)//是否开启断开重连,默认false,即关闭
//                .setRcDelayTime(3000)//设置重连时间间隔,单位毫秒,默认3000毫秒(开启断开重连后才生效)
//                .setHeartBeat(false)//设置是否开启心跳机制,默认false,即关闭
//                .setHbDelayTime(10)//设置心跳包发送时间间隔,默认10秒(设置开启心跳机制后才生效)
//                .setHbBackTime(5)//设置心跳包接收时间间隔,默认5秒(设置开启心跳机制后才生效)
//                //设置发送和接收心跳包数据的处理(设置开启心跳机制后才生效)
//                .setChbListener(new ClientHeartBeatFactory.OnClientHeartBeatListener() {
//                    @Override
//                    public boolean isResponse(IoSession ioSession, Object obj) {
//                        //客户端关注请求反馈,因此判断mesaage是否是反馈包
//                        String message=obj.toString();
//                        if("MinaServer".equals(message)){
//                            return true;
//                        }
//                        return false;
//                    }
//
//                    @Override
//                    public Object getRequest(IoSession ioSession) {
//                        //获取心跳请求包 non-null
//                        return "MinaClient";
//                    }
//                })
                //设置mina客户端接收数据监听
                .setCmrListener(new TmClientHandler.OnMessageReceivedListener() {
                    @Override
                    public void messageReceived(IoSession session, Object message) {
                        //接收服务端消息
                        //......

                        LogUtil.i("======我是服务端返回消息==message=" + message.toString());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTv.setText(message.toString());
                            }
                        });

                    }
                }).build();
        //初始化操作对象
        mTmClientManager = new TmClientManager(mConfig);
    }
```
其中涉及到的`断开重连`和`心跳机制`如果需要的话可设置，默认是不打开的。   
然后建立连接如下:
```
        new Thread(new Runnable() {
            @Override
            public void run() {
                //连接服务端
                mTmClientManager.connect();
            }
        }).start();
```
向服务端发送消息，你可以像虾米那这样:
```
               String result="我是中国人啊你是谁,我是亚瑟，你知道么，大家好一切都号";
               //给服务端发送消息
               SessionManager.getInstance().writeToServer(result);
```
通讯使用完毕，可以在界面销毁时断开连接，类似下面这样:
```
    @Override
    protected void onDestroy() {
        super.onDestroy();

        //断开连接
        mTmClientManager.disConnect();

    }
```
#### 三. 需要注意的问题
在`TmClientManager`对象建立`tcp长连接`的时候容易出现连接不上的情况，这时，可考虑从以下几个方面排查问题:
```
1.mina_tcp客户端与服务端链接地址(ip)不一致
2.mina_tcp客户端与服务端链接端口(port)不一致
3.mina_tcp服务端未开启
4.mina_tcp客户端未联网或未开联网权限
5.mina_tcp客户端与服务端不在一个网段
```
