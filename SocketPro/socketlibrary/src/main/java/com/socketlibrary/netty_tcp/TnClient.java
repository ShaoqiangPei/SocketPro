package com.socketlibrary.netty_tcp;

import com.socketlibrary.util.SocketUtil;
import com.socketlibrary.util.StringUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Title:netty 实现 tcp 客户端
 * description:
 * autor:pei
 * created on 2020/6/8
 */
public class TnClient {

    private String ip;//ip地址
    private int port;//端口号

    /**
     * 初始化对象
     *
     * @param ip 服务端ip
     * @param port 端口号 0-65535
     */
    public TnClient(String ip,int port){
        this.ip=ip;
        this.port=port;
    }

    /**
     * 发起通讯
     *
     * @param message  要发送的消息，字符串
     * @param charsetName 字符集，如 TcpConfig.UTF_8 或 TcpConfig.GBK,为null时采用编译器默认字符集
     * @param listener 接收服务端返回数据的监听
     */
    public void channel(String message,String charsetName,TnClientHandler.OnChannelListener listener){
        //打印通讯消息
        SocketUtil.i("========netty_tcp客户端信息==========");
        SocketUtil.i("服务端地址(ip): "+ip);
        SocketUtil.i("约定端口(port): "+port);

        if(StringUtil.isEmpty(ip)){
            throw new NullPointerException("======请初始化TnClient并设置ip======");
        }
        if(port < 0 || port > 65535){
            throw new SecurityException("======请初始化TnClient并设置合适port(0~65535)======");
        }
        EventLoopGroup eventLoopGroup=new NioEventLoopGroup();
        Bootstrap bootstrap=new Bootstrap();
        try {
            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new TnClientHandler(message, charsetName, listener));
                        }
                    });
            // Start the client.
            ChannelFuture f=bootstrap.connect(ip, port).sync();
            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            // Shut down the event loop to terminate all threads.
            eventLoopGroup.shutdownGracefully();
            SocketUtil.i("=======netty_tcp客户端关闭========");
        }
    }

}
