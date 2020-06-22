package com.socketlibrary.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Title:socket帮助类
 * description:
 * autor:pei
 * created on 2020/5/7
 */
public class SocketHelper {

    /**
     * 消息转换
     *
     * 注:当接收消息的方法是以 (result = bufferedReader.readLine()) != null 做判断读取stream的时候，
     *    则发送的消息要以 "\n" 结尾
     *
     * @param message
     * @return
     */
    public static String getMessageByReadLine(String message){
        message=message+"\n";
        return message;
    }

    /**获取本机ip(用于服务端获取本机ip)**/
    public static String getIpAddress(){
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            SocketUtil.e("IP地址获取失败: " + e.getMessage());
            System.err.println("IP地址获取失败" + e.toString());
        }
        return null;
    }

    /***
     * 检查网络是否可用
     * 需要权限 ACCESS_NETWORK_STATE
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isAvailable();
    }

}
