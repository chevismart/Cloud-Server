package server.Filter;

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
    public void encode(IoSession ioSession, Object o, ProtocolEncoderOutput protocolEncoderOutput) throws Exception {
        logger.debug("this is DownStreamEncoder");
    }

    @Override
    public void dispose(IoSession ioSession) throws Exception {

    }
}
