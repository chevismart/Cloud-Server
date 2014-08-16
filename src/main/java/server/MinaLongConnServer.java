package server;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import server.Filter.CodecFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by Boss on 2014/8/6.
 */
public class MinaLongConnServer {
    private static final int PORT = 8002;

    public static void main(String[] arg) throws IOException {
        MinaLongConnServer server = new MinaLongConnServer();
        server.start();
    }

    public void start() throws IOException {

        IoAcceptor acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("logger", new LoggingFilter());
//        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CodecFactory()));
//        ProtocolCodecFilter filter= new ProtocolCodecFilter(new ObjectSerializationCodecFactory());
//        acceptor.getFilterChain().addLast("objectFilter", filter);

        acceptor.setHandler(new MinaLongConnServerHandler());

        acceptor.getSessionConfig().setReadBufferSize(2048);
        acceptor.bind(new InetSocketAddress(PORT));
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60000);
        System.out.println("Listening on port " + PORT);

    }


}
