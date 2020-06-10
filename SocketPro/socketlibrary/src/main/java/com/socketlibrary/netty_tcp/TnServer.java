package com.socketlibrary.netty_tcp;

import com.socketlibrary.util.SocketHelper;
import com.socketlibrary.util.SocketUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Title: netty 实现 tcp 服务端
 * description:
 * autor:pei
 * created on 2020/6/9
 */
public class TnServer {

    private String mCharsetName;//字符编码集：如 TcpConfig.UTF_8 或 TcpConfig.GBK
    private int port;//端口：0 ~ 65535

    /**
     * 初始化对象
     *
     * @param port 端口：0-65535
     * @param charsetName 字符编码集：如 TcpConfig.UTF_8 或 TcpConfig.GBK
     */
    public TnServer(int port,String charsetName){
        this.port=port;
        this.mCharsetName=charsetName;
    }

    /**
     * 服务端启动方法
     *
     * @param listener 接收和回复客户端消息的监听
     */
    public void start(TnServerHandler.OnChannelListener listener){
        if(port < 0 || port > 65535){
            throw new SecurityException("======请初始化TnClient并设置合适port(0~65535)======");
        }
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b=new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            TnServerHandler tnServerHandler=new TnServerHandler(mCharsetName);
                            tnServerHandler.setOnChannelListener(listener);
                            ch.pipeline().addLast(tnServerHandler);
                        }
                    });
            ChannelFuture cft = b.bind(port).sync();
            SocketUtil.systemPrintln("========netty_tcp服务端已经启动=========");
            SocketUtil.systemPrintln("本机IP地址："+ SocketHelper.getIpAddress());
            SocketUtil.systemPrintln("约定端口(port): "+port);
            cft.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            SocketUtil.systemPrintln("========netty_tcp服务端关闭=========");
        }
    }

}
