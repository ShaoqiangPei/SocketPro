package com.socketlibrary.mina_tcp.heart_beat;

import com.socketlibrary.util.SocketUtil;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

/**
 * Title:被动心跳机制
 * description:
 * autor:pei
 * created on 2020/6/17
 */
public class ServerHeartBeatFactory implements KeepAliveMessageFactory {


    private OnServerHeartBeatListener mOnShbListener;

    public ServerHeartBeatFactory(OnServerHeartBeatListener listener){
        this.mOnShbListener=listener;
    }

    @Override
    public boolean isRequest(IoSession ioSession, Object o) {
//        //判断是否心跳请求包  是的话返回true
//        String message=o.toString();
//        System.out.println("=====服务端收心跳包信息=======o="+message);
//        if("MinaClient".equals(message)){
//            return true;
//        }

        if(o!=null){
            SocketUtil.systemPrintln("mina_tcp服务端收到客户端心跳包数据："+o.toString());
        }else{
            SocketUtil.systemPrintln("mina_tcp服务端收到客户端心跳包数据："+o);
        }
        //接收服务端心跳包数据并判断是否需要回复心跳包
        if(mOnShbListener!=null){
            return mOnShbListener.isResponse(ioSession, o);
        }
        return false;
    }

    @Override
    public boolean isResponse(IoSession ioSession, Object o) {
        //被动型心跳机制，没有请求当然也就不关注反馈 因此直接返回false
        return false;
    }

    @Override
    public Object getRequest(IoSession ioSession) {
        //被动型心跳机制无请求  因此直接返回null
        return null;
    }

    @Override
    public Object getResponse(IoSession ioSession, Object o) {
//        //根据心跳请求request 反回一个心跳反馈消息 non-null
//        String message=o.toString();
//        System.out.println("=======getResponse=======message="+message);

        Object obj=null;
        //给客户端回复心跳包数据
        if(mOnShbListener!=null){
            obj=mOnShbListener.getRequest(ioSession);
            if(obj!=null){
                SocketUtil.systemPrintln("mina_tcp服务端发送给客户端的心跳包数据："+obj.toString());
            }else{
                SocketUtil.systemPrintln("mina_tcp服务端发送给客户端的心跳包数据："+obj);
            }
            return obj;
        }
        return null;
    }

    public interface OnServerHeartBeatListener{

        /***
         * 接收客户端心跳包数据并判断是否需要回复心跳包
         * @param ioSession
         * @param obj 收到服务端的心跳包数据
         * @return
         */
        boolean isResponse(IoSession ioSession, Object obj);

        /**
         * 给客户端回复心跳包数据
         * @param ioSession
         * @return 回复给服务端的心跳包数据
         */
        Object getRequest(IoSession ioSession);
    }

}
