package com.socketlibrary.mina_tcp.tm_connect;

import org.apache.mina.core.session.IoSession;

/**
 * Title:mina_tcp客户端用于收发消息的session
 * description:
 * autor:pei
 * created on 2020/6/13
 */
public class SessionManager {

    private static SessionManager mInstance=null;

    private IoSession mSession;

    public static SessionManager getInstance(){
        if(mInstance==null){
            synchronized (SessionManager.class){
                if(mInstance==null){
                    mInstance=new SessionManager();
                }
            }
        }
        return mInstance;
    }

    private SessionManager(){}

    public void setSession(IoSession session){
        this.mSession=session;
    }

    public void writeToServer(Object message){
        if(mSession!=null){
            mSession.write(message);
        }
    }

    public void closeSession(){
        if(mSession!=null){
            mSession.closeOnFlush();
        }
    }

    public void removeSesion(){
        this.mSession=null;
    }

}
