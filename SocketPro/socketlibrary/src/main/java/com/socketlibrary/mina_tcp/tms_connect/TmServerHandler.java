package com.socketlibrary.mina_tcp.tms_connect;

import com.socketlibrary.util.SocketUtil;
import com.socketlibrary.util.StringUtil;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * 继承自IoHandlerAdapter，IoHandlerAdapter继承接口 IoHandler
 * 类IoHandlerAdapter实现了IoHandler的所有方法，只要重载关心的几个方法就可以了
 */
public class TmServerHandler extends IoHandlerAdapter {

    private OnTmsHandlerListener mOnTmsHandlerListener;

    public TmServerHandler(OnTmsHandlerListener listener){
        this.mOnTmsHandlerListener=listener;
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
        SocketUtil.systemPrintln("mina_tcp客户端与服务端连接成功.....");
        SocketUtil.systemPrintln("mina_tcp客户端ip："+session.getRemoteAddress());
    }

    /***
     * 这个方法是目前这个类里最主要的，
     *      当接收到消息，只要不是quit，就把服务器当前的时间返回给客户端
     *     如果是quit，则关闭客户端连接
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
//        //关闭服务端
//        String result = message.toString();
//        if (result.trim().equalsIgnoreCase("exit")) {
//            sessionClosed(session);
//            return;
//        }

        if(mOnTmsHandlerListener!=null){
            //收到消息
            SocketUtil.systemPrintln("mina_tcp服务端接收消息："+ message);
            SocketUtil.systemPrintln("======mina_tcp服务端接收完毕======");

            String backInfo=mOnTmsHandlerListener.messageHandler(session,message);
            //返回消息给客户端
            if(StringUtil.isNotEmpty(backInfo)){
                SocketUtil.systemPrintln("mina_tcp服务端回复消息: "+backInfo);
                session.write(backInfo);
                SocketUtil.systemPrintln("======mina_tcp服务端回复消息完毕======");
            }else{
                SocketUtil.systemPrintln("======mina_tcp服务端不能回复空消息======");
            }
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
        SocketUtil.systemPrintln("mina_tcp客户端与服务端断开连接.....");
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
        SocketUtil.systemPrintln("mina_tcp服务端Session 创建成功");
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        super.exceptionCaught(session, cause);
        cause.printStackTrace();

        SocketUtil.systemPrintln("mina_tcp服务端抛出异常："+cause.getMessage());
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        super.sessionIdle(session, status);
    }

    public interface OnTmsHandlerListener{
        /**
         * 收到消息并做出回复
         * @param session
         * @param message  接收的消息
         * @return 做出回复的消息
         */
        String messageHandler(IoSession session, Object message);
    }
}