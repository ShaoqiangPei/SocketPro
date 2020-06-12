package com.socketlibrary.netty_udp;

import com.socketlibrary.util.SocketUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * Title:netty实现UDP服务端
 * description:
 * autor:pei
 * created on 2020/6/11
 */
public class UnServer {

    private int port;//端口号: 0-65535
    private String mCharsetName;//字符编码集：如 TcpConfig.UTF_8 或 TcpConfig.GBK

    /***
     * 初始化对象，设置端口,字符集
     *
     * @param port 端口范围：0-65535
     * @param charsetName 字符编码集：如 TcpConfig.UTF_8 或 TcpConfig.GBK
     */
    public UnServer(int port,String charsetName){
       this.port=port;
       this.mCharsetName=charsetName;
    }

    /***
     * 建立udp服务端
     *
     * @param listener 接收和返回数据给udp客户端的监听
     */
    public void start(UnServerHandler.OnChannelListener listener) {
        if(port < 0 || port > 65535){
            throw new SecurityException("======请初始化TnClient并设置合适port(0~65535)======");
        }
        SocketUtil.systemPrintln("========netty_udp服务端已经启动=========");
        System.out.println("约定端口(port): "+port);

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            UnServerHandler unServerHandler=new UnServerHandler(mCharsetName);
            unServerHandler.setOnChannelListener(listener);
            //发起通讯
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(unServerHandler);
            bootstrap.bind(port).sync().channel().closeFuture().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
            SocketUtil.systemPrintln("========netty_udp服务端关闭=========");
        }
    }

}
