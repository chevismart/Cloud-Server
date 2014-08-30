package com.gamecenter.filter.tcp;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Boss on 2014/8/11.
 */
public class DownStreamEncoder implements ProtocolEncoder {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void encode(IoSession ioSession, Object o, ProtocolEncoderOutput out) throws Exception {
        logger.debug("this is DownStreamEncoder");
        IoBuffer buf = IoBuffer.allocate(16);
        buf.setAutoExpand(true); // Enable auto-expand for easier encoding

        // 编码消息头
//        buf.putShort((short) type);//type字段占2个字节(short)
//        buf.putInt(message.getSequence());// sequence字段占4个字节(int)

        buf.put((byte[])o);

        // 编码消息体,由子类实现
//        encodeBody(session, message, buf);
        buf.flip();
        out.write(buf);
    }

    @Override
    public void dispose(IoSession ioSession) throws Exception {

    }
}
