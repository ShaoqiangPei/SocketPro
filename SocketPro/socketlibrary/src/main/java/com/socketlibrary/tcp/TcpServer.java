package com.socketlibrary.tcp;

import com.socketlibrary.util.SocketUtil;
import com.socketlibrary.util.StringUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Title:tcp通讯 服务端
 * description:
 * autor:pei
 * created on 2020/5/6
 */
public class TcpServer {

    private ServerSocket mServerSocket;
    private Socket mServer;

    private OutputStream mOutStream;
    private PrintWriter mPrinter;

    private InputStream mInputStream;
    private InputStreamReader mInputStreamReader;
    private BufferedReader mBufferedReader;

    /***
     * 建立服务端
     * @param port  端口范围：0~65535,通常指定较大的数值
     */
    public void initSocket(int port){
        try {
            if (mServerSocket == null) {
                mServerSocket = new ServerSocket(port);//端口范围：0~65535
                InetAddress inetAddress= InetAddress.getLocalHost();
                SocketUtil.systemPrintln("=====tcp 服务端 ServerSocket 信息=========");
                SocketUtil.systemPrintln("=====主机名称(HostName): "+inetAddress.getHostName());
                SocketUtil.systemPrintln("=====主机地址(HostAddress): "+inetAddress.getHostAddress());
                SocketUtil.systemPrintln("=====地址(Address): "+inetAddress.getAddress());
                SocketUtil.systemPrintln("=====端口(port): "+port);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /***
     * Tcp发送数据
     * @param message 要发送的字符串
     * @param charsetName  字符集，如  TcpConfig.UTF_8 或 TcpConfig.GBK,为null时采用编译器默认字符集
     *
     * 注：若接收方以 "(receiveData = bufferedReader.readLine()) != null" 方式读取消息，
     *    则发送的消息结尾要加上 "\n".
     */
    public void sendMessage(String message,String charsetName){
        if (mServer != null) {
            SocketUtil.systemPrintln("=====tcp服务端ServerSocket准备发送消息=========");
            try {
                mOutStream=mServer.getOutputStream();
                mPrinter = new PrintWriter(new OutputStreamWriter(mOutStream,Charset.forName(charsetName)));
                mPrinter.println(message);
                mPrinter.flush();
                SocketUtil.systemPrintln("=====tcp服务端ServerSocket发送消息完毕=========");
                SocketUtil.systemPrintln("=====tcp服务端ServerSocket发送消息字符集: charsetName="+charsetName);
                SocketUtil.systemPrintln("=====tcp服务端ServerSocket发送消息内容: "+message);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                SocketUtil.systemPrintln("=====tcp服务端ServerSocket发送消息失败！======");
            }finally{
                //此处不做任何关于socket和stream的处理
            }
        }
    }

    /***
     * Tcp接收数据
     * @param charsetName  字符集，如  TcpConfig.UTF_8 或 TcpConfig.GBK,为null时采用编译器默认字符集
     * @return
     *
     * 注: 发送的消息要以 "\n" 结尾，因为本方法是以 (result = bufferedReader.readLine()) != null 做判断读取stream的
     */
    public String receiveMessage(String charsetName){
        String receiveData=null;

        SocketUtil.systemPrintln("=====tcp服务端ServerSocket准备接收消息=========");
        try{
            mServer=mServerSocket.accept();
            mInputStream = mServer.getInputStream();
            if(StringUtil.isNotEmpty(charsetName)) {
                mInputStreamReader = new InputStreamReader(mInputStream, Charset.forName(charsetName));
            }else{
                mInputStreamReader = new InputStreamReader(mInputStream);
            }
            mBufferedReader = new BufferedReader(mInputStreamReader);
            while ((receiveData = mBufferedReader.readLine()) != null) { //判断最后一行不存在，为空
                //读取出的最后结果
                //SocketUtil.systemPrintln("====读取的最后结果=====result=" + result);
                break;
            }
            SocketUtil.systemPrintln("====tcp客户端socket接收数据为: "+receiveData);
            SocketUtil.systemPrintln("=====tcp服务端ServerSocket接收消息完毕=========");
            SocketUtil.systemPrintln("=====tcp服务端ServerSocket接收消息字符集: charsetName="+charsetName);
            SocketUtil.systemPrintln("=====tcp服务端ServerSocket接收消息内容: "+receiveData);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            SocketUtil.systemPrintln("=====tcp服务端ServerSocket接收消息失败！=====");
        }finally {
            //接收消息时,读写的stream和socket均不能关闭
            //否则服务端会报错:java.net.SocketException: Socket is closed
        }
        return receiveData;
    }

    /**
     * 关闭socket连接
     */
    public void close() {
        try {
            if (mPrinter != null) {
                mPrinter.close();
            }
            if (mOutStream != null) {
                mOutStream.close();
            }
            if (mInputStream != null) {
                mInputStream.close();
            }
            if(mInputStreamReader!=null){
                mInputStreamReader.close();
            }
            if(mBufferedReader!=null){
                mBufferedReader.close();
            }
            if (mServer != null) {
                mServer.close();
            }
            SocketUtil.systemPrintln("=====tcp服务端ServerSocket关闭连接=====");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
