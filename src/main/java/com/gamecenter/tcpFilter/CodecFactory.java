package com.gamecenter.tcpFilter;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * Created by Boss on 2014/8/11.
 */
public class CodecFactory implements ProtocolCodecFactory {

    private ProtocolEncoder encoder;

    private ProtocolDecoder decoder;

    public CodecFactory() {
        this.encoder = new DownStreamEncoder();
        this.decoder = new UpStreamDecoder();
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        return encoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        return decoder;
    }
}
