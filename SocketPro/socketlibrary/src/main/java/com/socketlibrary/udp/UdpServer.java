package com.socketlibrary.udp;

import com.socketlibrary.util.SocketUtil;
import com.socketlibrary.util.StringUtil;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.Charset;

/**
 * Title: udp 数据报 服务端
 * description:
 * autor:pei
 * created on 2020/6/5
 */
public class UdpServer {

    private byte[] mBuffer = new byte[1024];
    private DatagramSocket mDatagramSocket;
    private DatagramPacket mDatagramPacket;

    /**构造函数，绑定主机和端口**/
    public UdpServer(int port){
        try {
            mDatagramSocket = new DatagramSocket(port);
            SocketUtil.systemPrintln("========udp 服务端启动!=========");
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /***
     *  服务端给客户端回复消息
     *
     * @param message  要发送的字符串
     * @param charsetName 字符集，如  TcpConfig.UTF_8 或 TcpConfig.GBK,为null时采用编译器默认字符集
     */
    public void sendMessage(String message,String charsetName){
        SocketUtil.systemPrintln("=====udp 服务端 ServerSocket 信息=========");
        SocketUtil.systemPrintln("=====客户端ip地址(ClientAddress): "+mDatagramPacket.getAddress().getHostAddress());
        SocketUtil.systemPrintln("=====客户端分配端口(port): "+mDatagramPacket.getPort());
        SocketUtil.systemPrintln("======服务端准备给客户端返回消息========");

        DatagramPacket datagramPacket = new DatagramPacket(mBuffer, mBuffer.length, mDatagramPacket
                .getAddress(), mDatagramPacket.getPort());
        byte data[]=null;
        if(StringUtil.isNotEmpty(charsetName)) {
            data=message.getBytes(Charset.forName(charsetName));
        }else{
            data=message.getBytes();
        }
        datagramPacket.setData(data);
        try {
            mDatagramSocket.send(datagramPacket);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SocketUtil.systemPrintln("=====udp服务端发送消息字符集: charsetName="+charsetName);
        SocketUtil.systemPrintln("=====udp服务端发送消息内容: "+message);
        SocketUtil.systemPrintln("=====udp服务端发送消息完毕=========");
    }


    /**
     * 接收数据包，该方法会造成线程阻塞
     *
     * @param charsetName 字符集，如  TcpConfig.UTF_8 或 TcpConfig.GBK,为null时采用编译器默认字符集
     * @return
     */
    public String receiveMessage(String charsetName){
        String info=null;
        mDatagramPacket = new DatagramPacket(mBuffer, mBuffer.length);
        try {
            SocketUtil.systemPrintln("=====udp服务端准备接收消息=========");
            mDatagramSocket.receive(mDatagramPacket);
            byte data[]=mDatagramPacket.getData();
            if(StringUtil.isNotEmpty(charsetName)) {
                info=new String(data,Charset.forName(charsetName));
            }else{
                info=new String(data);
            }
            SocketUtil.systemPrintln("=====udp服务端接收消息字符集: charsetName="+charsetName);
            SocketUtil.systemPrintln("=====udp服务端接收消息内容: "+info);
            SocketUtil.systemPrintln("=====udp服务端接收消息完毕=========");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return info;
    }

    /**关闭数据报服务端socket**/
    public void close(){
        if(mDatagramSocket!=null){
            mDatagramSocket.close();
        }
        mDatagramSocket=null;
        SocketUtil.systemPrintln("=====udp服务端关闭连接=====");
    }

}
