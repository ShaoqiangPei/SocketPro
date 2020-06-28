package com.socketlibrary.mina_tcp.tmc_connect;

import android.content.Context;
import com.socketlibrary.mina_tcp.encrypt.MessageCodecFactory;
import com.socketlibrary.mina_tcp.heart_beat.ClientHeartBeatFactory;
import com.socketlibrary.util.SocketHelper;
import com.socketlibrary.util.SocketUtil;
import com.socketlibrary.util.StringUtil;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Title:mina_tcp客户端连接操作对象
 * description:
 * autor:pei
 * created on 2020/6/18
 */
public class TmClientManager {

    private static final String MINA_LOGGER="tmc_Logger";
    private static final String MINA_CODEC="tmc_codec";
    private static final String MINA_HEART_BEAT="tmc_heart_beat";

    private TmClientConfig mConfig;
    private WeakReference<Context> mReference;
    private NioSocketConnector mConnector;
    private InetSocketAddress mAddress;
    private IoSession mSession;

    public TmClientManager(TmClientConfig config){
        this.mConfig=config;
        init();
    }

    private void init(){
        //参数校验
        chackParamets();
        //初始化
        mReference=new WeakReference<Context>(mConfig.getContext());
        //创建客户端连接器
        mConnector = new NioSocketConnector();
        //设置连接超时
        mConnector.setConnectTimeoutMillis(mConfig.getConnectTimeOut());
        //设置log打印
        mConnector.getFilterChain().addLast(TmClientManager.MINA_LOGGER,new LoggingFilter());
        //设置编码过滤器
        //mConnector.getFilterChain().addLast("codec",new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
        mConnector.getFilterChain().addLast(TmClientManager.MINA_CODEC,new ProtocolCodecFilter(new MessageCodecFactory(Charset.forName(mConfig.getCharsetName()))));
        //设置心跳机制
        setHeartBeat();
        //设置接收handler
        mConnector.setHandler(new TmClientHandler(mConfig.getCmrListener()));
        //设置重连机制
        setReConnect();
    }

    /**参数校验**/
    private void chackParamets(){
        //ip校验
        if(StringUtil.isEmpty(mConfig.getIp())){
            throw new NullPointerException("======ip不能为空======");
        }
        //端口检验
        if(mConfig.getPort() < 0 || mConfig.getPort() > 65535){
            throw new SecurityException("======请设置合适port(0~65535)======");
        }
        //连接超时时间
        if(mConfig.getConnectTimeOut()<0){
            throw new SecurityException("======连接超时时间不能小于0======");
        }
        //字符编码集
        if(StringUtil.isEmpty(mConfig.getCharsetName())){
            throw new NullPointerException("======字符集编码不能为null======");
        }
        //接收数据缓存区大小
        if(mConfig.getReadBufferSize()<0){
            throw new SecurityException("======设置接收数据缓存区大小不能小于0======");
        }
        //心跳包发送时间间隔
        if(mConfig.getHbDelayTime()<0){
            throw new SecurityException("======设置心跳包发送时间间隔不能小于0======");
        }
        //心跳包接收时间间隔
        if(mConfig.getHbBackTime()<0){
            throw new SecurityException("======设置心跳包接收时间间隔不能小于0======");
        }
        //重连时间间隔
        if(mConfig.getRcDelayTime()<0){
            throw new SecurityException("======设置断开重连时间间隔不能小于0======");
        }
    }

    /**设置心跳机制**/
    private void setHeartBeat(){
        if(mConfig.isHeartBeat()) {
            //设置心跳包
            KeepAliveMessageFactory factory = new ClientHeartBeatFactory(mConfig.getChbListener());
            //IdleStatus参数为 READER_IDLE ,及表明如果当前连接的读通道空闲的时候在指定的时间间隔getRequestInterval后发送出心跳请求，以及发出Idle事件
            //KeepAliveRequestTimeoutHandler设置为CLOS表明，当发出的心跳请求在规定时间内没有接受到反馈的时候则调用CLOSE方式 关闭连接
            KeepAliveFilter kaf = new KeepAliveFilter(factory, IdleStatus.READER_IDLE, KeepAliveRequestTimeoutHandler.CLOSE);
            //继续调用 IoHandlerAdapter 中的 sessionIdle时间
            kaf.setForwardEvent(true);
            //设置当连接的读取通道空闲的时候，心跳包请求时间间隔
            kaf.setRequestInterval(mConfig.getHbDelayTime());
            //设置心跳包请求后 等待反馈超时时间。 超过该时间后则调用KeepAliveRequestTimeoutHandler.CLOSE
            kaf.setRequestTimeout(mConfig.getHbBackTime());
            // 该过滤器加入到整个通信的过滤链中
            mConnector.getFilterChain().addLast(TmClientManager.MINA_HEART_BEAT, kaf);
        }
    }

    /**设置重连机制**/
    private void setReConnect(){
        //断开重连的监听
        mConnector.addListener(new ClientReConnectListener(){
            @Override
            public void sessionDestroyed(IoSession ioSession) throws Exception {
                SocketUtil.i("=========mina_tcp客户端启动重连机制==========");
                while (true) {
                    if (mConfig.isReconnect()) {
                        try {
                            Thread.sleep(mConfig.getRcDelayTime());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //开始重连
                        boolean connect = connect();
                        if (connect) {
                            SocketUtil.i("======mina_tcp客户端断开重连成功=====");
                            break;
                        } else {
                            SocketUtil.i("=====mina_tcp客户端重连失败,正在尝试重新连接");
                        }
                    } else {
                        SocketUtil.i("=======mina_tcp客户端退出重连机制==========");
                        break;
                    }
                }
            }
        });
    }

    /**打印连接信息**/
    private void printConnectInfo(){
        SocketUtil.i("=========mina_tcp客户端信息=============");
        SocketUtil.i("服务端ip: "+mConfig.getIp());
        SocketUtil.i("端口号port: "+mConfig.getPort());
        SocketUtil.i("连接超时时间: "+mConfig.getConnectTimeOut()+"(毫秒)");
        SocketUtil.i("字符编码集: "+mConfig.getCharsetName());
        SocketUtil.i("是否开启心跳机制: "+mConfig.isHeartBeat());
        if(mConfig.isHeartBeat()){
            SocketUtil.i("=========mina_tcp客户端心跳机制信息(心跳开启)=============");
            SocketUtil.i("心跳请求时间间隔："+mConfig.getHbDelayTime()+"(秒)");
            SocketUtil.i("心跳接收反馈时间间隔："+mConfig.getHbBackTime()+"(秒)");
        }
        SocketUtil.i("是否开启断开重连机制: "+mConfig.isReconnect());
        if(mConfig.isReconnect()){
            SocketUtil.i("断开重连时间间隔："+mConfig.getRcDelayTime()+"(毫秒)");
        }
    }

    /***
     * 建立连接
     *
     * @return true：连接成功， false：连接失败
     */
    public boolean connect(){
        //打印连接信息
        printConnectInfo();
        //检测网络连接
        if(SocketHelper.isNetworkConnected(mReference.get())){
            ConnectFuture future= null;
            try {
                SocketUtil.i("======mina_tcp客户端开始建立连接========");
                //建立连接
                mAddress=new InetSocketAddress(mConfig.getIp(),mConfig.getPort());
                future = mConnector.connect(mAddress);
                //等待连接创立完成
                future.awaitUninterruptibly();

                mSession=future.getSession();
            } catch (Exception e) {
                e.printStackTrace();
                SocketUtil.e("=====mina_tcp客户端连接错误========="+e.getMessage());

                SocketUtil.i("********连接失败可能原因****************");
                SocketUtil.i("1.mina_tcp客户端与服务端链接地址(ip)不一致");
                SocketUtil.i("2.mina_tcp客户端与服务端链接端口(port)不一致");
                SocketUtil.i("3.mina_tcp服务端未开启");
                SocketUtil.i("4.mina_tcp客户端未联网或未开联网权限");
                SocketUtil.i("5.mina_tcp客户端与服务端不在一个网段");

                return false;
            }
        }else{
            SocketUtil.i("========当前无网络,请检测网络连接=========");
            if (mSession != null) {
                //等待连接断开
                mSession.getCloseFuture().awaitUninterruptibly();
            }
            SessionManager.getInstance().closeSession();
            SessionManager.getInstance().removeSesion();
            mSession=null;
        }
        return mSession == null ? false : true;
    }

    /**断开连接**/
    public void disConnect(){
        mConfig.setReconnect(false);
        mConnector.dispose();
        if (mSession != null) {
            //等待连接断开
            mSession.getCloseFuture().awaitUninterruptibly();
        }
        mConnector=null;
        mAddress=null;
        SessionManager.getInstance().closeSession();
        SessionManager.getInstance().removeSesion();
        mSession=null;
        SocketUtil.i("=======mina_tcp客户端断开连接========");
    }

}
