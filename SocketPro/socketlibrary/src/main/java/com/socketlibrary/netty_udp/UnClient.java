package com.socketlibrary.netty_udp;

import com.socketlibrary.util.SocketUtil;
import com.socketlibrary.util.StringUtil;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * Title: netty实现UDP客户端
 * description:
 * autor:pei
 * created on 2020/6/11
 */
public class UnClient {

    private int port;//端口号: 0-65535
    private long mTimeOut=15000;//链接超时时间(单位毫秒)，默认15000,即15秒

    /**
     * 初始化对象
     *
     * @param port 端口：0-65535
     */
    public UnClient(int port){
       this.port=port;
    }

    /***
     * 设置连接超时时间,未设置的话,默认时间为15000毫秒,即15秒。
     *
     * @param timeOut 超时时间
     * @return
     */
    public UnClient setTimeOut(long timeOut){
        if(timeOut<0||timeOut==0){
            throw new SecurityException("======请设置大于0的超时时间(timeOut)=======");
        }
        this.mTimeOut=timeOut;
        return UnClient.this;
    }

    /***
     *
     *
     * @param message 要发送的消息字符串
     * @param charsetName 字符集,如 TcpConfig.UTF_8 或 TcpConfig.GBK等
     * @param listener 接收服务端返回数据的监听
     */
    public void sendMessage(String message,String charsetName,UnClientHandler.OnChannelListener listener){
        SocketUtil.i("========netty_udp客户端信息==========");
        SocketUtil.i("约定端口(port): "+port);
        SocketUtil.i("设置连接超时时间: mTimeOut="+mTimeOut+"(毫秒)");

        if(port < 0 || port > 65535){
            throw new SecurityException("======请初始化UnClient并设置合适port(0~65535)======");
        }
        EventLoopGroup group=new NioEventLoopGroup();
        try {
            UnClientHandler unClientHandler=new UnClientHandler(charsetName);
            unClientHandler.setOnChannelListener(listener);
            //开始建立连接
            Bootstrap bootstrap=new Bootstrap();
            bootstrap.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(unClientHandler);
            Channel ch=bootstrap.bind(0).sync().channel();
            //DatagramPacket构造发送广播消息，所以IP必须为"255.255.255.255"，而不是服务端的IP地址
            InetSocketAddress inetSocketAddress=new InetSocketAddress("255.255.255.255",port);

            //发送消息
            if(StringUtil.isNotEmpty(message)){
                SocketUtil.i("=====netty_udp客户端准备发送消息=====");
                byte data[]=null;
                if(StringUtil.isNotEmpty(charsetName)){
                    data=message.getBytes(Charset.forName(charsetName));
                }else{
                    data=message.getBytes();
                }
                DatagramPacket datagramPacket=new DatagramPacket(Unpooled.copiedBuffer(data), inetSocketAddress);
                ch.writeAndFlush(datagramPacket).sync();

                SocketUtil.i("netty_udp客户端发送消息："+message);
                SocketUtil.i("发送消息字符集："+charsetName);
                SocketUtil.i("======netty_udp客户端发送消息完毕=========");
            }
            //此处必须设置超时，不然客户端收不到服务端返回数据
            if(!ch.closeFuture().await(mTimeOut)){
                SocketUtil.i("**********netty_udp客户端连接已经超时******");
                SocketUtil.i("连接超时时间: mTimeOut="+mTimeOut+"(毫秒)");
                //打印连接失败可能原因
                printConnectedFaiedReson();
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            //打印连接失败可能原因
            printConnectedFaiedReson();
        }finally{
            group.shutdownGracefully();
            SocketUtil.i("=======netty_udp客户端关闭========");
        }
    }

    /**打印连接失败可能原因**/
    private void printConnectedFaiedReson(){
        SocketUtil.i("=====netty_udp客户端接收不到消息可能原因======");
        SocketUtil.i("1.netty_udp客户端与服务端链接端口(port)不一致");
        SocketUtil.i("2.netty_udp服务端未开启");
        SocketUtil.i("3.netty_udp客户端未联网或未开联网权限");
        SocketUtil.i("4.netty_udp客户端与服务端不在一个网段");
        SocketUtil.i("*****************************************");
    }
}
