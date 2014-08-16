package server;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Boss on 2014/8/7.
 */
public class MyCodecFactory implements ProtocolCodecFactory {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ProtocolEncoder getEncoder(IoSession ioSession) throws Exception {
        logger.info("This is encoder");
        ProtocolEncoder encoder;
        encoder = new ProtocolEncoder() {
            @Override
            public void encode(IoSession ioSession, Object o, ProtocolEncoderOutput protocolEncoderOutput) throws Exception {
                logger.info("This is encode");

            }

            @Override
            public void dispose(IoSession ioSession) throws Exception {
                logger.info("This is dispose");
            }
        };

        return encoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession ioSession) throws Exception {
        ProtocolDecoder decoder;

        decoder = new ProtocolDecoder() {
            @Override
            public void decode(IoSession ioSession, IoBuffer ioBuffer, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {
                logger.info("This is decode");
            }

            @Override
            public void finishDecode(IoSession ioSession, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {
                logger.info("This is finishDecode");
            }

            @Override
            public void dispose(IoSession ioSession) throws Exception {

            }
        };

        return decoder;
    }
}
