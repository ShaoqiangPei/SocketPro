package com.socketlibrary.netty_tcp;

import com.socketlibrary.util.SocketUtil;
import com.socketlibrary.util.StringUtil;
import java.nio.charset.Charset;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Title:netty tcp 客户端辅助类
 * description:
 * autor:pei
 * created on 2020/6/8
 */
public class TnClientHandler extends ChannelHandlerAdapter {

    private String mSendMessage;//要发送的消息
    private String mCharsetName;//字符编码集：如 TcpConfig.UTF_8 或 TcpConfig.GBK
    private OnChannelListener mOnChannelListener;

    /**
     * Creates a client-side handler.
     */
    public TnClientHandler(String message,String charsetName,OnChannelListener listener) {
        this.mSendMessage=message;
        this.mCharsetName=charsetName;
        this.mOnChannelListener=listener;

        ByteBuf buffer = Unpooled.buffer(1024);
        for (int i = 0; i < buffer.capacity(); i ++) {
            buffer.writeByte((byte) i);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        SocketUtil.i("=====netty_tcp客户端准备发送消息========");

        if(StringUtil.isNotEmpty(mSendMessage)){
            ByteBuf encoded = ctx.alloc().buffer(4 * mSendMessage.length());
            byte data[]=null;
            if(StringUtil.isNotEmpty(mCharsetName)){
                data=mSendMessage.getBytes(Charset.forName(mCharsetName));
            }else{
                data=mSendMessage.getBytes();
            }
            encoded.writeBytes(data);
            ctx.write(encoded);
            ctx.writeAndFlush(mSendMessage);

            SocketUtil.i("netty_tcp客户端发送消息字符集：charsetName="+mCharsetName);
            SocketUtil.i("netty_tcp客户端发送消息为: "+mSendMessage);
            SocketUtil.i("====netty tcp 客户端数据发送完毕===============");
        }else{
            SocketUtil.e("====netty tcp 客户端发送数据不能为空===============");
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        SocketUtil.i("=====netty_tcp客户端准备接收消息========");
        SocketUtil.i("=====netty_tcp客户端接收不到消息可能原因======");
        SocketUtil.i("1.netty_tcp客户端与服务端链接地址(ip)不一致");
        SocketUtil.i("2.netty_tcp客户端与服务端链接端口(port)不一致");
        SocketUtil.i("3.netty_tcp服务端未开启");
        SocketUtil.i("4.netty_tcp客户端未联网或未开联网权限");
        SocketUtil.i("5.netty_tcp客户端与服务端不在一个网段");

        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] data = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);

        String result=null;
        if(StringUtil.isNotEmpty(mCharsetName)){
            result = new String(data,Charset.forName(mCharsetName));
        }else{
            result = new String(data);
        }
        SocketUtil.i("netty_tcp客户端接收数据字符集：charsetName="+mCharsetName);
        SocketUtil.i("netty_tcp客户端接收数据为: "+result);
        SocketUtil.i("====netty_tcp客户端接收数据完毕!=====");
        if(mOnChannelListener!=null){
            mOnChannelListener.success(ctx,result);
        }
        //释放资源
        byteBuf.release();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        //cause.printStackTrace();
        //获取结果失败
        if(mOnChannelListener!=null){
            mOnChannelListener.failed(ctx,cause);
        }
        SocketUtil.e("====netty_tcp客户端接收数据失败:  "+cause.getMessage());
        //关闭ChannelHandlerContext
        ctx.close();
    }

    public interface OnChannelListener{
        void success(ChannelHandlerContext ctx, String result);
        void failed(ChannelHandlerContext ctx, Throwable cause);
    }
}
