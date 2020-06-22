package com.socketlibrary.mina_tcp.tm_connect;

import com.util.LogUtil;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * Title:mina_tcp客户端数据发送与接收处理类
 * description:
 * autor:pei
 * created on 2020/6/16
 */
public class TmClientHandler extends IoHandlerAdapter {

    private OnMessageReceivedListener mOnMrListener;

    public TmClientHandler(OnMessageReceivedListener listener){
        this.mOnMrListener=listener;
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
        //保存sesion
        SessionManager.getInstance().setSession(session);
        LogUtil.i("=========mina_tcp客户端连接成功========");
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        super.exceptionCaught(session, cause);
        //客户端连接异常
        LogUtil.e("=========mina_tcp客户端连接异常========"+cause.getMessage());
        LogUtil.e("********连接失败可能原因****************");
        LogUtil.e("1.mina_tcp客户端与服务端链接地址(ip)不一致");
        LogUtil.e("2.mina_tcp客户端与服务端链接端口(port)不一致");
        LogUtil.e("3.mina_tcp服务端未开启");
        LogUtil.e("4.mina_tcp客户端未联网或未开联网权限");
        LogUtil.e("5.mina_tcp客户端与服务端不在一个网段");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        super.sessionIdle(session, status);
        LogUtil.i("=========sessionIdle=====session="+session+"   IdleStatus="+status);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);

        LogUtil.i("=========sessionClosed=====session="+session);
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
        LogUtil.i("mina_tcp客户端发送消息："+message.toString());
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        super.messageReceived(session, message);

        LogUtil.i("mina_tcp客户端收到消息："+message.toString());
        if(mOnMrListener!=null){
            mOnMrListener.messageReceived(session,message);
        }
    }

    /**接收数据的监听**/
    public interface OnMessageReceivedListener{
        void messageReceived(IoSession session, Object message);
    }
}
