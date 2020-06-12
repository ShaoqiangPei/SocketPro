package com.socketlibrary.netty_udp;

import com.socketlibrary.util.SocketUtil;
import com.socketlibrary.util.StringUtil;
import java.nio.charset.Charset;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

/**
 * Title:netty实现UDP客户端辅助类
 * description:
 * autor:pei
 * created on 2020/6/11
 */
public class UnClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private String mCharsetName;//字符编码集：如 TcpConfig.UTF_8 或 TcpConfig.GBK
    private OnChannelListener mOnChannelListener;

    public UnClientHandler(String charsetName){
        this.mCharsetName=charsetName;
    }

    /**设置获取服务端返回结果的监听**/
    public void setOnChannelListener(OnChannelListener listener){
        this.mOnChannelListener=listener;
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket datagramPacket) throws Exception {
        SocketUtil.i("=====netty_udp客户端准备接收消息=====");

        String response=null;
        if(StringUtil.isNotEmpty(mCharsetName)){
            response=datagramPacket.content().toString(Charset.forName(mCharsetName));
        }else{
            response=datagramPacket.content().toString();
        }
        //客户端接收数据
        if(mOnChannelListener!=null){
            SocketUtil.i("netty_udp客户端接收消息："+response);
            SocketUtil.i("接收消息字符集："+mCharsetName);
            SocketUtil.i("======netty_udp客户端接收消息完毕=========");
            mOnChannelListener.success(ctx,response);
        }
        //关闭
        if(ctx!=null){
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        // TODO Auto-generated method stub
        cause.printStackTrace();

        if(mOnChannelListener!=null){
            mOnChannelListener.failed(ctx,cause);
        }
        //关闭
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
        super.channelActive(ctx);
    }

    public interface OnChannelListener{
        void success(ChannelHandlerContext ctx, String result);
        void failed(ChannelHandlerContext ctx, Throwable cause);
    }

}
