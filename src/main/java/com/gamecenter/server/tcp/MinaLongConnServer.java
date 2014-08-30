package com.gamecenter.server.tcp;

import com.gamecenter.handler.MinaTcpLongConnServerHandler;
import com.gamecenter.server.Server;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import com.gamecenter.filter.tcp.CodecFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by Boss on 2014/8/6.
 */
public class MinaLongConnServer implements Server {
    private static final int PORT = 8002;

    @Override
    public void start() {

        try {

            IoAcceptor acceptor = new NioSocketAcceptor();
            acceptor.getFilterChain().addLast("logger", new LoggingFilter());
//        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
            acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CodecFactory()));
//        ProtocolCodecFilter tcp= new ProtocolCodecFilter(new ObjectSerializationCodecFactory());
//        acceptor.getFilterChain().addLast("objectFilter", tcp);

            acceptor.setHandler(new MinaTcpLongConnServerHandler());

            acceptor.getSessionConfig().setReadBufferSize(2048);
            acceptor.bind(new InetSocketAddress(PORT));
            acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60000);
            System.out.println("Listening on port " + PORT);
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void stop() {

    }


}
