package com.socketlibrary.mina_tcp.heart_beat;

import com.socketlibrary.util.SocketUtil;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

/**
 * Title:mina客户端心跳机制类
 * description:
 * autor:pei
 * created on 2020/6/17
 */
public class ClientHeartBeatFactory implements KeepAliveMessageFactory {

    private OnClientHeartBeatListener mOnChbListener;

    public ClientHeartBeatFactory(OnClientHeartBeatListener listener){
        this.mOnChbListener=listener;
    }

    @Override
    public boolean isRequest(IoSession ioSession, Object o) {
        //服务器不会给客户端发送请求包,因此不关注请求包，直接返回false
        return false;
    }

    @Override
    public boolean isResponse(IoSession ioSession, Object o) {
//    //=========示例===============
//        //客户端关注请求反馈,因此判断mesaage是否是反馈包
//        String message=o.toString();
//        if("heart_server_to_client".equals(message)){
//            return true;
//        }

        if(o!=null){
            SocketUtil.i("mina_tcp客户端收到服务端心跳包数据："+o.toString());
        }else{
            SocketUtil.i("mina_tcp客户端收到服务端心跳包数据："+o);
        }
        //接收服务端心跳包数据并判断是否需要回复心跳包
        if(mOnChbListener!=null){
            return mOnChbListener.isResponse(ioSession, o);
        }
        return false;
    }

    @Override
    public Object getRequest(IoSession ioSession) {
//    //=========示例===============
//        //获取心跳请求包 non-null
//        return "heart_client_to_server";

        Object obj=null;
        //给服务端回复心跳包数据
        if(mOnChbListener!=null){
            obj=mOnChbListener.getRequest(ioSession);
            if(obj!=null){
                SocketUtil.i("mina_tcp客户端发送给服务端的心跳包数据："+obj.toString());
            }else{
                SocketUtil.i("mina_tcp客户端发送给服务端的心跳包数据："+obj);
            }
            return obj;
        }
        return null;
    }

    @Override
    public Object getResponse(IoSession ioSession, Object o) {
        //服务器不会给客户端发送心跳请求，客户端当然也不用反馈  该方法返回null
        return null;
    }

    public interface OnClientHeartBeatListener{

        /***
         * 接收服务端心跳包数据并判断是否需要回复心跳包
         * @param ioSession
         * @param obj 收到服务端的心跳包数据
         * @return
         */
        boolean isResponse(IoSession ioSession, Object obj);

        /**
         * 给服务端回复心跳包数据
         * @param ioSession
         * @return 回复给服务端的心跳包数据
         */
        Object getRequest(IoSession ioSession);
    }

}
