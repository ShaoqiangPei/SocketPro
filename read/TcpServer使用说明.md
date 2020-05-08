## TcpServer(TCP服务端)使用说明

### 简介
利用`socket`实现`TCP`服务端，方便开发者快速建立一个`tcp`的服务端。

### 使用说明
#### 一.tcp服务端severSocket初始化问题
在`tcp`服务端建立`ServerSocket`的时候，我们通常是这样的：
```
mServerSocket = new ServerSocket(port);
```
其实以上方法调用的是:
```
ServerSocket(int port, int backlog, InetAddress bindAddr) 
```
其中涉及到的参数：
- port ：端口，0-65535 之间
- backlog：ServerSocket可接收的socket应答个数，超过此数据服务端拒收客户端发送过来的数据
- bindAddr: ServerSocket绑定的ip地址类  
鉴于tcp服务端ServerSocket一般运行在"本机"上，则快速初始化ServerSocket运用上面的方法：
```
mServerSocket = new ServerSocket(port);
```
意思是建立的`ServerSocket` `IP地址`为`本机`，可容纳`socket`个数为`50`。
#### 二.TcpServer在代码中的使用
`TcpServe`r主要是在`java`上运行，所以就让我们在`Androidstudio`上模拟下在`Java`中运行`tcp服务端`的场景：
```
/**
 * Title:
 * description:
 * autor:pei
 * created on 2020/3/6
 */
public class TestJava {

    public static void main(String[] args) {
        LogUtil.setDebug(true);

        TcpServer server=new TcpServer();
        server.initSocket(12345);

        while (true){
            String message=server.receiveMessage(SocketConfig.UTF_8);
            System.out.println("======收到消息===message="+message);

            String kk="东方一点儿一点opkelpo儿泛着鱼肚色的天空，" +
                    "飘着五颜六色的朝霞，有：降紫的、金黄的、青色的……甚至还有一些火红色的火烧云，" +
                    "好像把大半个天给点燃，简直就是美不胜收。今天的朝霞十分奇异，既不像棉花糖，" +
                    "又不像绵羊，而像鱼鳞，很罕见，如果把蓝天比作大海，那这朝霞就是海浪，令人陶醉。" +
                    "在东南方向，还有一道七色彩虹，像桥一样，也许是太阳公公的桥梁吧！";
            //发送消息
            kk= SocketHelper.getMessageByReadLine(kk);
            server.sendMessage(kk,SocketConfig.UTF_8);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```
#### 三.tcp通讯服务端相关问题

`tcp`服务端主要容易出现以下两个问题：
- `ServerSocket`接收不到`客户端socket`的数据  
对于这个问题，这里需要强调的是`TcpServer`的接收方法`receiveMessage(String charsetName)`是以`(result = bufferedReader.readLine()) != null`
做判断读取`stream`的，所以`客户端`向`TcpServer`发送消息时，需要在结尾加上`\n`,这样`TcpServer`的`receiveMessage(String charsetName)`方法才能将传
过来的数据接收完整。  
将发送的message转为带\n的message，可以调用SocketHelper中的getMessageByReadLine(String message)方法
- `ServerSocket`接收数据乱码  
接收数据乱码问题。`TcpServer`的发送和接收方法中均有一个字符集参数，为了保证数据不乱码，需要客户端和服务端使用相同的字符编码集。
字符集可以调用`SocketConfig`类中关于字符集的常量

