## UnServer(netty实现udp服务端)使用说明

### 概述
`UnServer`是一个利用`netty`实现的`udp服务端`,利用此类可以快捷实现`udp服务端`。  
要了解`netty`实现的`udp客户端`可参考[UnClient使用说明](https://github.com/ShaoqiangPei/SocketPro/blob/master/read/UnClient%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E.md)

### 使用说明
#### 一 通讯基础
主要是涉及端口`port`的问题。`port`的范围是`0 ~ 65535`.

#### 二. UnServer在代码中的使用
你可以在项目中利用`UnServer`这样建立一个`udp服务端`:
```
public class TestJava {

    public static void main(String[] args) {
        LogUtil.setDebug(true);

        UnServer unServer=new UnServer(1112,"UTF-8");
        unServer.start(new UnServerHandler.OnChannelListener() {
            @Override
            public String receiveData(ChannelHandlerContext ctx, String result) {
                //接收客户端数据
                System.out.println("=========接收客户端的数据=====result=" + result);

                //给客户端的回复,若返回null，表示不给客户端回复
                return "我真的是服务端啊";
            }
        });
    }
}
```
这里需要注意的是，若要对客户端发送过来的数据做不同的处理，可以在接收的客户端的数据上标记不同`tag`，
然后，根据不同`tag`，给客户端回复不同的数据，类似下面这样：
```
 unServer.start(new UnServerHandler.OnChannelListener() {
            @Override
            public String receiveData(ChannelHandlerContext ctx, String result) {
                //接收客户端数据
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
```
#### 三.需要注意的问题
若接收或发送数据出现乱码，请注意保持服务端和客户端字符集编码一致(字符集编码帮助类是`SocketConfig`)。


