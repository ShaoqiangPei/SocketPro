package com.socketlibrary.netty_tcp;

import com.socketlibrary.util.SocketUtil;
import com.socketlibrary.util.StringUtil;
import java.nio.charset.Charset;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Title: netty tcp 服务端辅助类
 * description:
 * autor:pei
 * created on 2020/6/9
 */
public class TnServerHandler extends ChannelHandlerAdapter {

    private String mCharsetName;//字符编码集：如 TcpConfig.UTF_8 或 TcpConfig.GBK
    private OnChannelListener mOnChannelListener;

    public TnServerHandler (String charsetName){
        this.mCharsetName=charsetName;
    }

    public void setOnChannelListener(OnChannelListener listener){
        this.mOnChannelListener=listener;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        SocketUtil.systemPrintln("=====准备接收netty_tcp客户端信息=======");

        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] data = new byte[byteBuf.readableBytes()];
        // msg中存储的是ByteBuf类型的数据，把数据读取到byte[]中
        byteBuf.readBytes(data);

        String result=null;
        if(StringUtil.isNotEmpty(mCharsetName)){
            result = new String(data, Charset.forName(mCharsetName));
        }else{
            result = new String(data);
        }
        // 释放资源，这行很关键
        byteBuf.release();

        if(mOnChannelListener!=null){
            // 接收并打印客户端的信息
            SocketUtil.systemPrintln("接收netty_tcp客户端信息: " + result);
            SocketUtil.systemPrintln("接收信息字符集："+mCharsetName);
            SocketUtil.systemPrintln("======接收netty_tcp客户端信息完毕=====");
            //接收并返回数据
            String responseData=mOnChannelListener.receiveData(ctx,result);
            if(StringUtil.isNotEmpty(responseData)) {
                //给客户端返回数据
                send(ctx, responseData, mCharsetName);
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        //cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
        super.channelActive(ctx);
    }

    public void send(ChannelHandlerContext ctx, String message, String charsetName){
        SocketUtil.systemPrintln("=====准备给netty_tcp客户端回复消息=======");
        // 向客户端发送消息
        String response =message;
        // 在当前场景下，发送的数据必须转换成ByteBuf数组
        ByteBuf encoded = ctx.alloc().buffer(4 * response.length());
        try {
            byte data[]=null;
            if(StringUtil.isNotEmpty(charsetName)){
                data = response.getBytes(Charset.forName(charsetName));
            }else{
                data = response.getBytes();
            }
            encoded.writeBytes(data);
            ctx.write(encoded);
            ctx.flush();
            SocketUtil.systemPrintln("给netty_tcp客户端回复信息: " + response);
            SocketUtil.systemPrintln("回复信息字符集："+charsetName);
            SocketUtil.systemPrintln("======给netty_tcp客户端回复信息完毕=====");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

    public interface OnChannelListener{
        String receiveData(ChannelHandlerContext ctx, String result);
    }

}
