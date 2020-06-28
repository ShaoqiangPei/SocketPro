package com.socketlibrary.mina_tcp.tms_connect;

import com.socketlibrary.mina_tcp.encrypt.MessageCodecFactory;
import com.socketlibrary.mina_tcp.heart_beat.ServerHeartBeatFactory;
import com.socketlibrary.util.SocketConfig;
import com.socketlibrary.util.SocketHelper;
import com.socketlibrary.util.SocketUtil;
import com.socketlibrary.util.StringUtil;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Title:mina服务端操作对象
 * description:
 * autor:pei
 * created on 2020/6/22
 */
public class TmServerManager {

    private static final String MINA_LOGGER="tms_Logger";
    private static final String MINA_CODEC="tms_codec";
    private static final String MINA_HEART_BEAT="tms_heart_beat";

    private int mPort;//端口号
    private String mCharsetName= SocketConfig.UTF_8;
    private int mReadBufferSize=2048;//设置接收缓存区大小
    private int mHbDelayTime=10;//心跳包接收时间间隔(单位秒)
    private int mIdleTime=10;//回到空闲状态时间间隔
    private TmServerHandler.OnTmsHandlerListener mOnTmsHandlerListener;//服务端节后与发送消息处理类

    //心跳机制
    private boolean mHeartBeat;//是否设置心跳机制
    private ServerHeartBeatFactory.OnServerHeartBeatListener mOnServerHeartBeatListener;//心跳机制接收与发送监听

    /***
     * 设置端口号
     * @param port 范围 0-65535
     * @return
     */
    public TmServerManager setPort(int port) {
        this.mPort = port;
        return this;
    }

    /***
     * 设置字符编码集，若不设置则默认 UTF-8
     * @param charsetName
     * @return
     */
    public TmServerManager setCharsetName(String charsetName) {
        this.mCharsetName = charsetName;
        return this;
    }

    /***
     * 设置接收缓存区大小，不设置的话默认为 2048
     * @param readBufferSize
     * @return
     */
    public TmServerManager setReadBufferSize(int readBufferSize){
        this.mReadBufferSize=readBufferSize;
        return this;
    }

    /***
     * 设置服务回到空闲状态时间间隔,不设置则默认10秒
     * @param idleTime 单位秒
     */
    public TmServerManager setIdleTime(int idleTime) {
        this.mIdleTime = idleTime;
        return this;
    }

    /***
     * 是否开启心跳机制，默认不开启
     * @param heartBeat
     */
    public TmServerManager setHeartBeat(boolean heartBeat) {
        this.mHeartBeat = heartBeat;
        return this;
    }

    /***
     * 设置接收心跳时间间隔(单位秒),若不设置则默认10秒
     * 当心跳机制开启后才生效
     *
     * @param hbDelayTime
     */
    public TmServerManager setHbDelayTime(int hbDelayTime){
        this.mHbDelayTime=hbDelayTime;
        return this;
    }

    /***
     * 设置心跳机制的接收心跳包和回复心跳包给客户端
     * 当心跳机制开启后才生效
     *
     * @param listener
     * @return
     */
    public TmServerManager setOnServerHeartBeatListener(ServerHeartBeatFactory.OnServerHeartBeatListener listener){
        this.mOnServerHeartBeatListener=listener;
        return this;
    }


    /***
     * 设置服务端接收和回复消息的监听
     * @param listener
     * @return 返回给客户端回复的消息，当为null时表示不给客户端回复消息
     */
    public TmServerManager setOnTmsHandlerListener(TmServerHandler.OnTmsHandlerListener listener){
        this.mOnTmsHandlerListener=listener;
        return this;
    }

    /**启动服务端**/
    public void start() {
        SocketUtil.systemPrintln("========mina_tcp服务端信息=========");
        SocketUtil.systemPrintln("本机IP地址："+ SocketHelper.getIpAddress());
        SocketUtil.systemPrintln("约定端口(port): "+mPort);
        SocketUtil.systemPrintln("收发消息字符集(charsetName): "+mCharsetName);
        SocketUtil.systemPrintln("缓存区大小: "+mReadBufferSize);
        SocketUtil.systemPrintln("回到空闲状态的时间: "+mIdleTime+"(秒)");
        SocketUtil.systemPrintln("是否开启心跳机制: "+mHeartBeat);
        //参数校验
        chackParamets();
        //创建连接器
        IoAcceptor acceptor = new NioSocketAcceptor();
        //添加日志管理过滤器
        acceptor.getFilterChain().addLast(TmServerManager.MINA_LOGGER, new LoggingFilter());
        // 协议解析，采用mina现成的UTF-8字符串处理方式
//        acceptor.getFilterChain().addLast(TmServerManager.MINA_CODEC,
//                new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName(mCharsetName))));
        acceptor.getFilterChain().addLast(TmServerManager.MINA_CODEC, new ProtocolCodecFilter(new MessageCodecFactory(Charset.forName(mCharsetName))));
        //设置心跳机制
        if(mHeartBeat){
            setMinaHeartBeat(acceptor);
        }
        // 设置接收缓存区大小
        acceptor.getSessionConfig().setReadBufferSize(mReadBufferSize);
        //回到空闲状态的时间
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, mIdleTime);//10秒
        // 设置消息处理类（创建、关闭Session，可读可写等等，继承自接口IoHandler）
        acceptor.setHandler(new TmServerHandler(mOnTmsHandlerListener));
        try {
            // 服务器开始监听
            acceptor.bind(new InetSocketAddress(mPort));
            SocketUtil.systemPrintln("========mina_tcp服务端已经启动=========");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**参数校验**/
    private void chackParamets(){
        //端口检验
        if(mPort < 0 || mPort > 65535){
            throw new SecurityException("======请设置合适port(0~65535)======");
        }
        //字符编码集
        if(StringUtil.isEmpty(mCharsetName)){
            throw new NullPointerException("======字符集编码不能为null======");
        }
        //接收数据缓存区大小
        if(mReadBufferSize<0){
            throw new SecurityException("======设置接收数据缓存区大小不能小于0======");
        }
        //心跳包心跳包接收时间间隔
        if(mHbDelayTime<0){
            throw new SecurityException("======设置心跳包心跳包接收时间间隔不能小于0======");
        }
        //回到空闲时间间隔
        if(mIdleTime<0){
            throw new SecurityException("======设置回到空闲时间间隔不能小于0======");
        }
    }

    /**设置心跳机制**/
    private void setMinaHeartBeat(IoAcceptor acceptor){
        SocketUtil.systemPrintln("接收心跳时间间隔: "+mHbDelayTime+"(秒)");

        //心跳机制
        ServerHeartBeatFactory heartBeatFactory = new ServerHeartBeatFactory(mOnServerHeartBeatListener);
        //IdleStatus参数为 BOTH_IDLE,即表明如果当前连接的读写通道都空闲的时候在指定的时间间隔getRequestInterval后发送出发Idle事件
        KeepAliveFilter kaf = new KeepAliveFilter(heartBeatFactory, IdleStatus.BOTH_IDLE);
        kaf.setForwardEvent(true); //idle事件回发  当session进入idle状态的时候 依然调用handler中的idled方法
        kaf.setRequestInterval(mHbDelayTime);//本服务器为被定型心跳,即需要每10秒接受一个心跳请求(默认10秒)否则该连接进入空闲状态 并且发出idled方法回调
        acceptor.getFilterChain().addLast(TmServerManager.MINA_HEART_BEAT, kaf);
    }


}
