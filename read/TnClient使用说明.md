## TnClient(netty实现tcp客户端)使用说明

### 概述
`TnClient`是一个利用`netty`封装实现`tcp客户端`的工具类，开发者可以利用此类迅速实现一个`Tcp客户端`。  
要用`netty`实现的`tcp服务端`可参考[TnServer使用说明](https://github.com/ShaoqiangPei/SocketPro/blob/master/read/TnServer%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E.md)。

### 使用说明
#### 一.通讯基础
##### 1.1 关于ip
通讯时会涉及到`ip`和`端口`，这里的`ip`要设置的是`服务端ip`，而不是客户端设备的`ip`.

##### 1.2 关于端口
端口(port),端口范围是`0-65535`,一般端口会取该范围内较大的数值。这样可以大概率避免端口使用冲突。
#### 二. 封装类 TnClient 的使用
你可以像下面这样在`activity`中建立`TnClient`客户端:
```
    //声明客户端对象
    private TnClient mTnClient;
    
    /**初始化数据**/
    private void initData(){
        //初始化客户端对象(设置服务端IP和通讯端口号)
        mTnClient=new TnClient("192.168.50.152",1111);
    }
    
    /**点击事件中发送和接收消息的方法**/
    private void test(){
        //发送和接收消息
        mTnClient.channel("1我是第一条测试", SocketConfig.GBK, new TnClientHandler.OnChannelListener() {
            @Override
            public void success(ChannelHandlerContext ctx, String result) {
                LogUtil.i("=====通讯成功==result=="+result);

                //显示数据到控件上
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTv.setText(result);
                    }
                });
            }

            @Override
            public void failed(ChannelHandlerContext ctx, Throwable cause) {
               LogUtil.i("=======通讯失败======="+cause.getMessage());
            }
        });
    }
```
#### 三.封装类 TnClient 使用时注意事项
##### 3.1 Android上使用注意的点
使用需要注意网络权限问题。你需要在`AndroidManifast.xml`中加入以下网络权限：
```
 <uses-permission android:name="android.permission.INTERNET" />
```
若有必要，你还需要在你`Android`项目中加入`android 6.0+`手动权限。
##### 3.2 问题排查参考
- 通讯逻辑要放到子线程中执行，不可放到UI线程中执行
- 若`TnClient`客户端发出消息后未收到消息，可从以下方面着手排查问题：
```
1.udp客户端与服务端链接地址(ip)不一致
2.udp客户端与服务端链接端口(port)不一致
3.udp服务端未开启
4.udp客户端未联网或未开联网权限
5.udp客户端与服务端不在一个网段
```
- 若出现数据接收乱码，请保持`TnClient`客户端和服务端字符集编码一致(字符集类为`SocketConfig`)


