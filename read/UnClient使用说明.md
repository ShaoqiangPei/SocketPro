## UnClient(netty实现udp客户端)使用说明

### 概述
`UnClient`是一个利用`netty`封装实现`udp`客户端的工具类，开发者可以利用此类迅速实现一个`udp`客户端。  
要用`netty`实现的`udp`服务端可参考[UnServer使用说明](https://github.com/ShaoqiangPei/SocketPro/blob/master/read/UnServer%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E.md)。

### 使用说明
#### 一.通讯基础
##### 1.1 关于ip
通讯时会涉及到`ip`和`端口`，这里的`ip`要设置的是`服务端ip`，而不是客户端设备的`ip`.

##### 1.2 关于端口
端口(`port`),端口范围是`0-65535`,一般端口会取该范围内较大的数值。这样可以大概率避免端口使用冲突。
#### 二. 封装类 UnClient 的使用
你可以像下面这样在`activity`中建立`UnClient`客户端:
```
    //声明对象
    private UnClient mUnClient;
    
    //初始化对象
    mUnClient=new UnClient(1112)
           //设置连接超时时间,未设置的话,默认时间为15000毫秒,即15秒。
           .setTimeOut(15000);
        
    @Override
    public void onClick(View v) {
       switch (v.getId()) {
           case R.id.btn1://测试1
               LogUtil.i("=======测试=======");

               test1();
               break;
           default:
               break;
       }
    }

    /**测试**/
    private void test1(){
        ToastUtil.shortShow("我是第一条测试");

        mUnClient.sendMessage("我是王者啊", SocketConfig.UTF_8, new UnClientHandler.OnChannelListener() {
            @Override
            public void success(ChannelHandlerContext ctx, String result) {
                LogUtil.i("=======接收服务端返回数据成功===result="+result);

                //显示数据
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTv.setText(result);
                    }
                });
            }

            @Override
            public void failed(ChannelHandlerContext ctx, Throwable cause) {
                LogUtil.i("=======接收服务端返回数据失败failed=========");
            }
        });
    }
```
#### 三. 需要注意的问题
##### 3.1 Android上使用注意的点
使用需要注意网络权限问题。你需要在AndroidManifast.xml中加入以下网络权限：
```
 <uses-permission android:name="android.permission.INTERNET" />
```
若有必要，你还需要在你`Android`项目中加入`android 6.0+`手动权限。
##### 3.2 问题排查参考
- 通讯逻辑要放到子线程中执行，不可放到UI线程中执行
- 若UnClient客户端发出消息后未收到消息，可从以下方面着手排查问题：
```
1.udp客户端与服务端链接端口(port)不一致
2.udp服务端未开启
3.udp客户端未联网或未开联网权限
4.udp客户端与服务端不在一个网段
```
- 若出现数据接收乱码，请保持UnClient客户端和服务端字符集编码一致(字符编码集利用`SocketConfig`)
