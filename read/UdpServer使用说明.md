## UdpServer(UDP服务端)使用说明

### 简介
`UdpServer`是一个`UDP服务端`封装类，利用此类可以方便快捷建立一个`UDP服务端`。  
了解`UDP客户端`可参考[UdpClient(UDP客户端)使用说明](https://github.com/ShaoqiangPei/SocketPro/blob/master/read/UdpClient%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E.md)

### 使用说明
#### 一. 通讯基础
主要是涉及端口`port`的问题。`port`的范围是`0 ~ 65535`.
#### 二. UdpServer 在代码中的使用
`UdpServer`主要是在`java`上运行，所以就让我们在`Androidstudio`上模拟下在`Java`中运行`Udp`服务端的场景：
```
public class TestJava {

    public static void main(String[] args) {

        SocketUtil.setDebug(true);
        UdpServer server=new UdpServer(12345);

        while (true){
            String message=server.receiveMessage(SocketConfig.UTF_8);
            System.out.println("======收到消息===message="+message);

            String kk="东方一点儿一点opkelpo儿泛着鱼肚色的天空，" +
                    "飘着五颜六色的朝霞，有：降紫的、金黄的、青色的……甚至还有一些火红色的火烧云，" +
                    "好像把大半个天给点燃，简直就是美不胜收。今天的朝霞十分奇异，既不像棉花糖，" +
                    "又不像绵羊，而像鱼鳞，很罕见，如果把蓝天比作大海，那这朝霞就是海浪，令人陶醉。" +
                    "在东南方向，还有一道七色彩虹，像桥一样，也许是太阳公公的桥梁吧！";
            //发送消息
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
#### 三.udp通讯服务端相关问题
若接收或发送数据出现乱码，请注意保持服务端和客户端字符集编码一致(字符集编码帮助类是`SocketConfig`)。
