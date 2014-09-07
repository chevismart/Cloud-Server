package com.gamecenter.filter.http;

import com.gamecenter.model.HttpResponseMessage;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

/**
 * Created by Boss on 2014/8/30.
 */
public class HttpResponseEncoder implements MessageEncoder {
    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        IoBuffer buffer = IoBuffer.allocate(100).setAutoExpand(true);
        buffer.put(((HttpResponseMessage)message).buildHTTPResponseMessage().getBytes());
//        buffer.putString((CharSequence) message, Charset.forName("UTF-8").newEncoder());
        buffer.flip();
        out.write(buffer);
    }
}
