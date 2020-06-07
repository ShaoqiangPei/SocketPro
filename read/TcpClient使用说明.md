## TcpClient(TCP客户端)使用说明

### 简介
`TcpClient`是一个`TCP`实现的客户端封装类，便于开发者快捷建立`tcp`客户端`socket`。  
要用`tcp服务端`可参考[TcpServer](https://github.com/ShaoqiangPei/SocketPro/blob/master/read/TcpServer%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E.md)类。

### 使用说明
#### 一.tcp通讯基础简介
`TCP`是一种比较常见和基础的通讯方式，关于比较细的东西这里就不做详细介绍。这里只提及几个比较重要的，容易被忽略的点。
##### 1.1 关于ip
`tcp`通讯时的客户端`socket`在与服务端建立连接时，会涉及到`ip`和`端口`，这里的`ip`要设置的是`服务端ip`，而不是`客户端设备的ip`.
##### 1.2 关于端口
端口(port),端口范围是`0-65535`,一般端口会取该范围内较大的数值。这样可以大概率避免端口使用冲突。
#### 二.tcp客户端封装类TcpClient的使用
若你要在`Android`项目的`MainActivity`中使用`TcpClient`,类似下面这样：
```
//声明
 private TcpClient mTcpClient;

//初始化并设置连接参数
mTcpClient=new TcpClient();
mTcpClient.setConnectTimeOut(5000)//设置连接超时
          .setSocket("192.168.50.152",12345);//设置连接ip和端口

//点击按钮时发送消息并接收返回数据
    @Override
    public void onClick(View v) {
       switch (v.getId()) {
           case R.id.btn1://测试
               test();
               break;
           default:
               break;
       }
    }

    private void test() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message="我是客户端a";
                message= SocketHelper.getMessageByReadLine(message);
                mTcpClient.sendMessage(message, SocketConfig.UTF_8);
                String result = mTcpClient.receiveMessage(SocketConfig.UTF_8);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.i("=======我来了=====");
                        mTextView.setText(result);
                    }
                });

                LogUtil.i("=====客户端收到结果===result=" + result);
            }
        }).start();
    }
```
#### 三.封装类TcpClient使用时注意事项
##### 3.1 Android上使用注意点
`tcp`在`Android`上的使用仍然需要注意网络权限问题。你需要在`AndroidManifast.xml`中加入以下网络权限：
```
 <uses-permission android:name="android.permission.INTERNET" />
```
若有必要，你还需要在你`Android`项目中加入`Android 6.0+`手动权限。
##### 3.2 问题排查参考
- 写的tcp客户端socket代码无法运行问题。在android中tcp客户端socket发送消息的逻辑只能放到线程中执行(如点击按钮开启一条线程，然后在线程中执行tcp通讯逻辑)，不能放到主线程中执行tcp通讯逻辑。
- 在tcp客户端socket与服务端建立连接的时候，经常会出现以下几种错误：
```
1.客户端socket连接超时
2.客户端socket结束收据时出现socket对象为空
3.socket已经关闭
```
鉴于各种socket连接失败的情况，我们可以按照以下几条来排查问题：
```
1. 服务端未开启或者服务端停止
2. 服务端客户端ip地址不一致
3. 服务端客户端port端口不一致
4. 服务端客户端ip与port均一致,但是网络不在一个网段
```
- 当`socket`建立`ok`后，可能还会出现`客户端socket`接收不到数据的问题。这里需要注意的是`TcpClient`的接收消息的方法`receiveMessage(String charsetName)`中是以`(result = bufferedReader.readLine()) != null`做判断读取`stream`的，所以`服务端`向`TcpClient`发送消息时，需要在结尾加上`\n`,这样`TcpClient`的`receiveMessage(String charsetName)`方法才能将传过来的数据接收完整。  
将发送的`message`转为带`\n`的`message`，可以调用`SocketHelper`中的`getMessageByReadLine(String message)`方法
- 接收数据乱码问题。`TcpClient`的发送和接收方法中均有一个字符集参数，为了保证数据不乱码，需要客户端和服务端使用相同的字符编码集。  
字符集可以调用`SocketConfig`类中关于字符集的常量
