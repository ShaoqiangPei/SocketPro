package com.socketlibrary.udp;

import com.socketlibrary.util.SocketUtil;
import com.socketlibrary.util.StringUtil;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

/**
 * Title: udp 数据报 客户端
 * description:
 * autor:pei
 * created on 2020/6/5
 */
public class UdpClient {

    private DatagramSocket mDatagramSocket;
    private String ip;//服务端 ip
    private int port;//端口号

    /**
     * 初始化对象
     *
     * @param ip 服务端ip
     * @param port 约定端口
     */
    public UdpClient(String ip, int port){
        try {
            this.ip=ip;
            this.port=port;
            mDatagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 发送消息
     *
     * @param message 字符串消息
     * @param charsetName 字符集，如  TcpConfig.UTF_8 或 TcpConfig.GBK,为null时采用编译器默认字符集
     */
    public void sendMessage(String message,String charsetName){
        System.out.println("========udp客户端信息==========");
        //ip和port校验
        if (StringUtil.isEmpty(ip) || port < 0 || port > 65535) {
            throw new SecurityException("=ip或port<端口>数据格式错误，请设置合适参数值(0<port<65535)=");
        }
        //发送消息格式校验
        if (StringUtil.isEmpty(message)) {
            SocketUtil.i("====不能发空数据===");
            return;
        }
        //打印连接信息
        System.out.println("========udp客户端信息==========");
        System.out.println("服务端地址(ip): "+ip);
        System.out.println("约定端口(port): "+port);

        // 创建发送类型的数据报：
        try {
            byte data[]=null;
            if(StringUtil.isNotEmpty(charsetName)) {
                data= message.getBytes(Charset.forName(charsetName));
            }else{
                data= message.getBytes();
            }
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, InetAddress.getByName(ip),port);
            // 通过套接字发送数据：
            mDatagramSocket.send(sendPacket);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            //			if(mDatagramSocket!=null){
            //				mDatagramSocket.close();
            //			}
        }
        SocketUtil.i("====udp客户端发送消息成功=====");
        SocketUtil.i("====udp客户端发送消息字符集：charsetName="+charsetName);
        SocketUtil.i("====udp客户端发送消息为: "+message);
    }

    /***
     * 接收消息
     *
     * @param charsetName 字符集，如  TcpConfig.UTF_8 或 TcpConfig.GBK,为null时采用编译器默认字符集
     * @return 收到的消息
     */
    public String receiveMessage(String charsetName){
        SocketUtil.i("======udp客户端接收不到消息可能原因======");
        SocketUtil.i("1.udp客户端与服务端链接地址(ip)不一致");
        SocketUtil.i("2.udp客户端与服务端链接端口(port)不一致");
        SocketUtil.i("3.udp服务端未开启");
        SocketUtil.i("4.udp客户端未联网或未开联网权限");
        SocketUtil.i("5.udp客户端与服务端不在一个网段");
        SocketUtil.i("======udp客户端准备接收消息======");

        String info=null;
        try {
            byte inBuff[] = new byte[mDatagramSocket.getReceiveBufferSize()];
            DatagramPacket datagramPacket = new DatagramPacket(inBuff, inBuff.length);
            mDatagramSocket.receive(datagramPacket);
            byte data[]=datagramPacket.getData();

            //info = new String(datagramPacketk.getData(), 0, datagramPacketk.getLength());
            if(StringUtil.isNotEmpty(charsetName)) {
                info=new String(data,Charset.forName(charsetName));
            }else{
                info=new String(data);
            }
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SocketUtil.i("====udp客户端接收数据字符集：charsetName="+charsetName);
        SocketUtil.i("====udp客户端接收数据为: "+info);
        SocketUtil.i("====udp客户端接收数据完毕!=====");
        return info;
    }

    /**关闭客户端数据报文**/
    public void close(){
        if(mDatagramSocket!=null){
            mDatagramSocket.close();
        }
        mDatagramSocket=null;
        SocketUtil.i("====udp客户端关闭=====");
    }

}
