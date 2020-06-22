package com.socketlibrary.mina_tcp.encrypt;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import java.nio.charset.Charset;

/**
 * Title:加密，解密工厂(解决发送接收数据粘包断包问题)
 * description:
 * autor:pei
 * created on 2020/6/16
 */
public class MessageCodecFactory implements ProtocolCodecFactory {

    private MinaMessageDecoder decoder;
    private MinaMessageEncoder encoder;

    public MessageCodecFactory() {
        this(Charset.defaultCharset());
    }

    public MessageCodecFactory(Charset charSet) {
        encoder = new MinaMessageEncoder(charSet);
        decoder = new MinaMessageDecoder(charSet);
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return decoder;
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        return encoder;
    }
}
