## TnServer(netty实现tcp服务端)使用说明

### 概述
`TnServer`是一个利用`netty`实现的`tcp服务端`,利用此类可以快捷实现`tcp`服务端。  
要了解`netty`实现的`tcp客户端`可参考[TnClient使用说明](https://github.com/ShaoqiangPei/SocketPro/blob/master/read/TnClient%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E.md)

### 使用说明
#### 一 通讯基础
主要是涉及端口`port`的问题。`port`的范围是`0 ~ 65535`.
#### 二. TnServer在代码中的使用
你可以在项目中利用`TnServer`这样建立一个`tcp服务端`:
```
/**
 * Title:
 * description:
 * autor:pei
 * created on 2020/3/6
 */
public class TestJava {


    public static void main(String[] args) {
        //初始化服务端对象
        TnServer tnServer=new TnServer(1111,SocketConfig.GBK);
        //启动 tcp 服务端
        tnServer.start(new TnServerHandler.OnChannelListener() {
            @Override
            public String receiveData(ChannelHandlerContext ctx, String result) {
                int type=Integer.valueOf(result.substring(0,1));
                String message=result.substring(1,result.length());
                System.out.println("===========我是接收结果=====result="+message);

                //返回值为返回给客户端的结果
                String response = null;
                switch (type) {
                    case 1:
                        response = "服务端已经收到第一条数据";
                        break;
                    case 2:
                        response = "服务端已经收到第二条数据";
                        break;
                    default:
                        break;
                }
                System.out.println("===========我是回复信息=====response="+response);
               return response;
            }
        });
    }

}
```
这里需要注意的是，为了处理不同的通讯，我们可以在`receiveData(ChannelHandlerContext ctx, String result)`的`result`上做标记用以区分不同的数据通讯结果，
然后根据这个标记分类，给客户端返回不同的数据，即以上代码中 `switch-case`中的处理。
#### 三.TnServer服务端相关问题
若接收或发送数据出现乱码，请注意保持服务端和客户端字符集编码一致(字符集编码帮助类是`SocketConfig`)。

