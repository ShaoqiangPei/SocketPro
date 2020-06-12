package com.socketlibrary.netty_udp;

import com.socketlibrary.util.SocketUtil;
import com.socketlibrary.util.StringUtil;
import java.nio.charset.Charset;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

/**
 * Title: netty实现UDP服务端 辅助类
 * description:
 * autor:pei
 * created on 2020/6/11
 */
public class UnServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private String mCharsetName;//字符编码集：如 TcpConfig.UTF_8 或 TcpConfig.GBK
    private OnChannelListener mOnChannelListener;

    /***
     * 初始化对象,设置字符集
     *
     * @param charsetName 字符编码集：如 TcpConfig.UTF_8 或 TcpConfig.GBK
     */
    public UnServerHandler(String charsetName){
        this.mCharsetName=charsetName;
    }

    public void setOnChannelListener(OnChannelListener listener){
        this.mOnChannelListener=listener;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, DatagramPacket packet)
            throws Exception {
        SocketUtil.systemPrintln("=====准备接收netty_udp客户端信息=======");

        //接收的为byte数组
        ByteBuf byteBuf=packet.content().copy();
        byte[] receiveData = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(receiveData);
        String result=null;
        if(StringUtil.isNotEmpty(mCharsetName)){
            result=new String(receiveData, Charset.forName(mCharsetName));
        }else{
            result=new String(receiveData);
        }
        //接收数据并处理发送的数据
        if(mOnChannelListener!=null){
            // 接收并打印客户端的信息
            SocketUtil.systemPrintln("接收netty_udp客户端信息: " + result);
            SocketUtil.systemPrintln("接收信息字符集："+mCharsetName);
            SocketUtil.systemPrintln("======接收netty_udp客户端信息完毕=====");

            String response=mOnChannelListener.receiveData(ctx,result);
            if(StringUtil.isNotEmpty(response)){
                SocketUtil.systemPrintln("=====准备给netty_udp客户端回复消息=======");
                //返回数据给客户端
                byte[] responseData=null;
                if(StringUtil.isNotEmpty(mCharsetName)){
                    responseData=response.getBytes(Charset.forName(mCharsetName));
                }else{
                    responseData=response.getBytes();
                }
                DatagramPacket datagramPacket=new DatagramPacket(Unpooled.copiedBuffer(responseData),packet.sender());
                ctx.writeAndFlush(datagramPacket);

                SocketUtil.systemPrintln("给netty_udp客户端回复信息: " + response);
                SocketUtil.systemPrintln("回复信息字符集："+mCharsetName);
                SocketUtil.systemPrintln("======给netty_udp客户端回复信息完毕=====");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
        super.channelActive(ctx);
    }

    public interface OnChannelListener{
        String receiveData(ChannelHandlerContext ctx, String result);
    }

}