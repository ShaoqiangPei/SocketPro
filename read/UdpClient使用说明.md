## UdpClient(UDP客户端)使用说明

### 简介
`UdpClient`是一个`UDP`实现的客户端封装类，便于开发者快速实现`UDP`客户端。  
要用`UDP服务端`可参考[UdpServer(UDP服务端)使用说明](https://github.com/ShaoqiangPei/SocketPro/blob/master/read/UdpServer%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E.md)。

### 使用说明
#### 一.通讯基础
##### 1.1 关于ip
通讯时会涉及到`ip`和`端口`，这里的`ip`要设置的是`服务端ip`，而不是客户端设备的`ip`.
##### 1.2 关于端口
端口(port),端口范围是`0-65535`,一般端口会取该范围内较大的数值。这样可以大概率避免端口使用冲突。
#### 二.UDP客户端封装类UdpClient的使用
若你要在`Android`项目的`MainActivity`中使用`UdpClient`,类似下面这样：
```
    //声明
    private UdpClient mUdpClient;
    
    //初始化
    mUdpClient=new UdpClient("192.168.50.152",12345);
    
    //点击按钮时发送消息并接收返回数据
        public void onClick(View v) {
       switch (v.getId()) {
           case R.id.btn1://测试
               LogUtil.i("=======测试=======");

               test();
               break;
           default:
               break;
       }
    }

    /**测试**/
    private void test(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message="我是UDP客户端啊,你是UDP服务端吗?";
                mUdpClient.sendMessage(message, SocketConfig.UTF_8);
                String result = mUdpClient.receiveMessage(SocketConfig.UTF_8);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.i("=======我来了=====");
                        mTv.setText(result);
                    }
                });

                LogUtil.i("=====客户端收到结果===result=" + result);
            }
        }).start();
    }
    
    //界面退出时,关闭socket
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭udp链接
        if(mUdpClient!=null){
            mUdpClient.close();
        }
    }
```
#### 三.封装类UdpClient使用时注意事项
##### 3.1 Android上使用注意点
`udp`在`Android`上的使用仍然需要注意网络权限问题。你需要在`AndroidManifast.xml`中加入以下网络权限：
```
 <uses-permission android:name="android.permission.INTERNET" />
```
若有必要，你还需要在你`Android`项目中加入`ndroid 6.0+`手动权限。
##### 3.2 问题排查参考
- 通讯逻辑要放到子线程中执行，不可放到`UI`线程中执行
- 若`UdpClient`客户端发出消息后未收到消息，可从以下方面着手排查问题：
```
1.udp客户端与服务端链接地址(ip)不一致
2.udp客户端与服务端链接端口(port)不一致
3.udp服务端未开启
4.udp客户端未联网或未开联网权限
5.udp客户端与服务端不在一个网段
```
- 若出现数据接收乱码，请保持`udp`客户端和服务端`字符集编码`一致(字符集类为`SocketConfig`)
