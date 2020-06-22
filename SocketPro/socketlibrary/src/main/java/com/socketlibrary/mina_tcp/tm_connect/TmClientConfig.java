package com.socketlibrary.mina_tcp.tm_connect;

import android.content.Context;

import com.socketlibrary.mina_tcp.heart_beat.ClientHeartBeatFactory;
import com.socketlibrary.util.SocketConfig;

/**
 * Title:mina客户端连接配置类
 * description:
 * autor:pei
 * created on 2020/6/18
 */
public class TmClientConfig {

    //默认连接超时时间为30秒
    private static final long CONNECT_TIME_OUT=30*1000;//单位毫秒
    //默认字符编码集为"utf-8"
    private static final String CHARSET_NAME= SocketConfig.UTF_8;
    //默认设置接收缓存大小2048
    private static final int READ_BUFFER_SIZE=2048;

    //默认设置心跳包发送时间间隔10秒
    private static final int HB_DELAY_TIME=10;//单位秒
    //默认设置心跳包接收反馈等待时长5秒
    private static final int HB_BACK_TIME=5;//单位秒

    //默认设置断开重连时间间隔3秒
    private static final int RC_DELAY_TIME=3*1000;//单位毫秒

    //总配置
    private Context context;
    private String ip;//服务端ip;
    private int port;//端口
    private long connectTimeOut;//连接超时时间(单位毫秒)
    private String charsetName;//字符编码集
    private int readBufferSize;//设置接收缓存区大小
    private TmClientHandler.OnMessageReceivedListener cmrListener;//客户端接收数据的监听

    //心跳机制
    private boolean heartBeat;//是否设置心跳机制
    private int hbDelayTime;//心跳包发送时间间隔(单位秒)
    private int hbBackTime;//心跳等待反馈时间间隔(单位秒)
    private ClientHeartBeatFactory.OnClientHeartBeatListener chbListener;//客户端心跳机制接收及反馈监听

    //重连机制
    private boolean reconnect;//是否设置断开重连
    private long rcDelayTime;//重连时间间隔

    public Context getContext() {
        return context;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public long getConnectTimeOut() {
        return connectTimeOut;
    }

    public String getCharsetName() {
        return charsetName;
    }

    public int getReadBufferSize() {
        return readBufferSize;
    }

    public TmClientHandler.OnMessageReceivedListener getCmrListener() {
        return cmrListener;
    }

    public boolean isHeartBeat() {
        return heartBeat;
    }

    public int getHbDelayTime() {
        return hbDelayTime;
    }

    public int getHbBackTime() {
        return hbBackTime;
    }

    public ClientHeartBeatFactory.OnClientHeartBeatListener getChbListener() {
        return chbListener;
    }

    public boolean isReconnect() {
        return reconnect;
    }

    public void setReconnect(boolean reconnect) {
        this.reconnect = reconnect;
    }

    public long getRcDelayTime() {
        return rcDelayTime;
    }

    public static class Builder{

        //总配置
        private Context context;
        private String ip;//服务端ip;
        private int port;//端口
        private long connectTimeOut=TmClientConfig.CONNECT_TIME_OUT;//连接超时时间(单位毫秒)
        private String charsetName=TmClientConfig.CHARSET_NAME;//字符编码集
        private int readBufferSize=TmClientConfig.READ_BUFFER_SIZE;//设置接收缓存区大小
        private TmClientHandler.OnMessageReceivedListener cmrListener;//客户端接收数据的监听

        //心跳机制
        private boolean heartBeat;//是否设置心跳机制
        private int hbDelayTime=TmClientConfig.HB_DELAY_TIME;//心跳包发送时间间隔(单位秒)
        private int hbBackTime=TmClientConfig.HB_BACK_TIME;//心跳等待反馈时间间隔(单位秒)
        private ClientHeartBeatFactory.OnClientHeartBeatListener chbListener;//客户端心跳机制接收及反馈监听

        //重连机制
        private boolean reconnect;//是否设置断开重连
        private long rcDelayTime=TmClientConfig.RC_DELAY_TIME;//重连时间间隔

        public Builder(Context context){
            this.context=context;
        }

        public Builder setIp(String ip) {
            this.ip = ip;
            return this;
        }

        /**
         * 设置端口号
         * @param port: 0-65535之间
         * @return
         */
        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        /***
         * 设置超时时间,默认为30*1000，即30秒
         * @param connectTimeOut:单位毫秒
         * @return
         */
        public Builder setConnectTimeOut(long connectTimeOut) {
            this.connectTimeOut = connectTimeOut;
            return this;
        }

        /***
         * 设置字符集，默认为 “UTF-8”
         * @param charsetName
         * @return
         */
        public Builder setCharsetName(String charsetName) {
            this.charsetName = charsetName;
            return this;
        }

        /***
         * 设置接收数据缓存区大小，默认2048
         * @param readBufferSize
         * @return
         */
        public Builder setReadBufferSize(int readBufferSize) {
            this.readBufferSize = readBufferSize;
            return this;
        }

        /***
         * 设置mina客户端接收数据监听
         * @param cmrListener
         * @return
         */
        public Builder setCmrListener(TmClientHandler.OnMessageReceivedListener cmrListener) {
            this.cmrListener = cmrListener;
            return this;
        }

        /***
         * 设置是否开启心跳机制,默认false,即关闭
         * @param heartBeat
         * @return
         */
        public Builder setHeartBeat(boolean heartBeat) {
            this.heartBeat = heartBeat;
            return this;
        }

        /***
         * 设置心跳包发送时间间隔,默认10秒
         * 设置开启心跳机制后才生效
         *
         * @param hbDelayTime:单位秒
         * @return
         */
        public Builder setHbDelayTime(int hbDelayTime) {
            this.hbDelayTime = hbDelayTime;
            return this;
        }

        /***
         * 设置心跳包接收时间间隔,默认5秒
         * 设置开启心跳机制后才生效
         *
         * @param hbBackTime:单位秒
         * @return
         */
        public Builder setHbBackTime(int hbBackTime) {
            this.hbBackTime = hbBackTime;
            return this;
        }

        /***
         * 设置客户端心跳包发送和接收心跳包的数据处理监听
         * 设置开启心跳机制后才生效
         *
         * @param listener
         * @return
         */
        public Builder setChbListener(ClientHeartBeatFactory.OnClientHeartBeatListener listener) {
            this.chbListener = listener;
            return this;
        }

        /***
         * 是否开启断开重连,默认false,即关闭
         * @param reconnect
         * @return
         */
        public Builder setReconnect(boolean reconnect) {
            this.reconnect = reconnect;
            return this;
        }

        /***
         * 设置重连时间间隔,单位毫秒(开启断开重连后才生效)
         * 默认时间为 3秒
         *
         * @param rcDelayTime：单位毫秒
         * @return
         */
        public Builder setRcDelayTime(long rcDelayTime) {
            this.rcDelayTime = rcDelayTime;
            return this;
        }

        private void applyConfig(TmClientConfig config) {
            //总配置
            config.context=this.context;
            config.ip=this.ip;//服务端ip;
            config.port=this.port;//端口
            config.connectTimeOut=this.connectTimeOut;//连接超时时间(单位毫秒)
            config.charsetName=this.charsetName;//字符编码集
            config.readBufferSize=this.readBufferSize;//设置接收缓存区大小
            config.cmrListener=this.cmrListener;//客户端接收数据的监听
            //心跳机制
            config.heartBeat=this.heartBeat;//是否设置心跳机制
            config.hbDelayTime=this.hbDelayTime;//心跳包发送时间间隔(单位秒)
            config.hbBackTime=this.hbBackTime;//心跳等待反馈时间间隔(单位秒)
            config.chbListener=this.chbListener;//客户端心跳机制接收及反馈监听
            //重连机制
            config.reconnect=this.reconnect;//是否设置断开重连
            config.rcDelayTime=this.rcDelayTime;//重连时间间隔
        }

        public TmClientConfig build(){
            TmClientConfig config=new TmClientConfig();
            applyConfig(config);
            return config;
        }
    }
}
