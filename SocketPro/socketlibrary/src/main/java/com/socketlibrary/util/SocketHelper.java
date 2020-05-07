package com.socketlibrary.util;

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
}
