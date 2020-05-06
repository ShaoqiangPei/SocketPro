package com.socketlibrary.tcp;

import com.socketlibrary.util.SocketConfig;
import com.socketlibrary.util.SocketUtil;
import com.socketlibrary.util.StringUtil;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Title:Tcp socket客户端
 * description:
 * autor:pei
 * created on 2020/5/6
 */
public class TcpClient {

    private static final int DEFAULT_TIME_OUT=3000;//默认连接超时为3秒
    public boolean isTimeOut;//判断TCP连接超时，做出界面提醒

    private Socket mSocket;
    private OutputStream mOutputStream;
    private ByteArrayOutputStream mBos; // 字节流缓冲区
    private InputStream mInputStream;
    private BufferedOutputStream mBuffOut;

    private String ip;//通讯ip
    private int port;//通讯
    private int mTimeOut=DEFAULT_TIME_OUT;//连接超时时间(默认为DEFAULT_TIME_OUT)

    /**设置连接超时时间**/
    public TcpClient setConnectTimeOut(int timeOut){
        this.mTimeOut=timeOut;
        return TcpClient.this;
    }

    /**
     * 建立连接
     * @param ip 如: "192.168.1.1"
     * @param port  范围在 0-65535 之间，一般取比较大的值
     */
    public TcpClient setSocket(String ip,int port){
        this.ip=ip;
        this.port=port;
        return TcpClient.this;
    }

    /**
     * 发送消息
     * @param message 字符串消息
     * @param charsetName 字符集，如  SocketConfig.UTF_8 或 SocketConfig.GBK
     */
    public void sendMessage(String message,String charsetName) {
        //ip和port校验
        if (StringUtil.isEmpty(ip) || port < 0 || port > 65535) {
            throw new SecurityException("=ip或port<端口>数据格式错误，请调用setSocket(String ip,int port)方法设置合适参数值=");
        }
        //发送消息格式校验
        if (StringUtil.isEmpty(message)) {
            SocketUtil.i("====不能发空数据===");
            return;
        }
        try {
            //连接socket
            mSocket = new Socket();
            InetSocketAddress address=new InetSocketAddress(ip, port);
            //打印连接信息
            SocketUtil.i("========tcp客户端socket信息==========");
            SocketUtil.i("连接主机名(hostName): "+address.getHostName());
            SocketUtil.i("连接地址(address): "+address.getAddress());
            SocketUtil.i("连接端口(port): "+address.getPort());
            SocketUtil.i("连接超时时间(mTimeOut): "+mTimeOut+"(毫秒)");
            SocketUtil.i("====tcp客户端socket开始建立连接=====");
            mSocket.connect(address,mTimeOut);
            SocketUtil.i("====tcp客户端socket连接成功=====");

            SocketUtil.i("====tcp客户端socket开始发送消息=====");
            //发送数据
            mOutputStream = mSocket.getOutputStream();
            mBuffOut = new BufferedOutputStream(mOutputStream);
            byte data[]=null;
            if(StringUtil.isNotEmpty(charsetName)) {
                data= message.getBytes(Charset.forName(charsetName));
            }else{
                data= message.getBytes();
            }
            mBos = new ByteArrayOutputStream();
            mBos.write(data, 0, data.length);
            byte b[] = mBos.toByteArray();
            mOutputStream.write(b);
            mOutputStream.flush();
            mBuffOut.flush();
            SocketUtil.i("====tcp客户端socket发送消息成功=====");
            SocketUtil.i("====tcp客户端socket发送消息为message="+message);
        } catch (Exception e) {
            e.printStackTrace();

            SocketUtil.e("====tcp客户端socket连接失败=====");
            isTimeOut = true;
            //连接异常则关闭socket
            close();
        }finally{
            //发送消息过后,读写的stream和socket均不能关闭
            //否则无法收到服务端回复，会显示socket is closed
        }
    }

    /**
     * 接收消息
     *
     * @param charsetName 字符集，如  TcpConfig.UTF_8 或 TcpConfig.GBK
     * @return
     */
    public String receiveMessage(String charsetName){
        String result=null;
        if(mSocket==null){
            SocketUtil.i("====mSocket为null,请检查socket连接,可能原因: =====");
            SocketUtil.i("  1. 服务端未开启或者服务端停止");
            SocketUtil.i("  2. 服务端客户端ip地址不一致");
            SocketUtil.i("  3. 服务端客户端port端口不一致");
            SocketUtil.i("  4. 服务端客户端ip与port均一致,但是网络不在一个网段");
            return result;
        }
        //字符集校验
        if(StringUtil.isEmpty(charsetName)){
            throw new NullPointerException("====charsetName不能为空,请设置字符集,如 SocketConfig.UTF_8 或 SocketConfig.GBK 等=====");
        }
        InputStreamReader inputStreamReader=null;
        BufferedReader bufferedReader=null;
        SocketUtil.i("====tcp客户端socket开始接收数据=====");
        try {
            mInputStream = mSocket.getInputStream();
            inputStreamReader=new InputStreamReader(mInputStream, Charset.forName(charsetName));
            bufferedReader = new BufferedReader(inputStreamReader);
            while ((result = bufferedReader.readLine()) != null) { //判断最后一行不存在，为空
                //读取出的最后结果
                //System.out.println("====读取的最后结果=====result=" + result);
                break;
            }
            SocketUtil.i("====tcp客户端socket接收数据完毕!=====");
            SocketUtil.i("====tcp客户端socket接收数据为result="+result);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            SocketUtil.i("====tcp客户端socket接收数据失败!=====");
        }finally {
            //关闭当前方法中用到的流
            try {
                if(inputStreamReader!=null){
                    inputStreamReader.close();
                }
                if(bufferedReader!=null){
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //关闭socket
            close();
        }
        return result;
    }

    /**关闭连接**/
    public void close() {
        try {
            if (mOutputStream != null) {
                mOutputStream.close();
            }
            if (mBos != null) {
                mBos.close();
            }
            if (mInputStream != null) {
                mInputStream.close();
            }
            if (mBuffOut != null) {
                mBuffOut.close();
            }
            if(mSocket!=null){
                mSocket.close();
            }
            mOutputStream=null;
            mBos=null;
            mInputStream=null;
            mBuffOut=null;
            mSocket=null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        SocketUtil.i("====tcp客户端socket关闭=====");
    }

}
