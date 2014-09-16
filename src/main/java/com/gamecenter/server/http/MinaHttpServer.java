package com.gamecenter.server.http;

import com.gamecenter.filter.http.HttpServerProtocolCodecFactory;
import com.gamecenter.handler.HttpJsonHandler;
import com.gamecenter.handler.HttpServerHandler;
import com.gamecenter.server.Server;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by Boss on 2014/8/30.
 */
public class MinaHttpServer implements Server {
    @Override
    public void start() {
        int port = DEFAULT_PORT;

        try {
            // Create an acceptor
            NioSocketAcceptor acceptor = new NioSocketAcceptor();

            // Create a service configuration
            acceptor.getFilterChain().addLast(
                    "protocolFilter",
                    new ProtocolCodecFilter(
                            new HttpServerProtocolCodecFactory()));
            acceptor.getFilterChain().addLast("logger", new LoggingFilter());
            acceptor.setHandler(new HttpServerHandler());
            acceptor.bind(new InetSocketAddress(port));

            System.out.println("Http Server now listening on port " + port);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /** Default HTTP port */
    private static final int DEFAULT_PORT = 8080;
    private NioSocketAcceptor acceptor;
    private boolean isRunning;

    private String encoding;
    private HttpJsonHandler httpJsonHandler;

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
//        HttpRequestDecoder.defaultEncoding = encoding;
//        HttpResponseEncoder.defaultEncoding = encoding;
    }

    public HttpJsonHandler getHttpJsonHandler() {
        return httpJsonHandler;
    }

    public void setHttpJsonHandler(HttpJsonHandler httpJsonHandler) {
        this.httpJsonHandler = httpJsonHandler;
    }

    /**
     * @param port listening port
     * @throws IOException
     */
    public void run(int port) throws IOException {
        synchronized (this) {
            if (isRunning) {
                System.out.println("Server is already running.");
                return;
            }
            acceptor = new NioSocketAcceptor();
            acceptor.getFilterChain().addLast(
                    "protocolFilter",
                    new ProtocolCodecFilter(
                            new HttpServerProtocolCodecFactory()));
            // acceptor.getFilterChain().addLast("logger", new LoggingFilter());
            HttpServerHandler handler = new HttpServerHandler();
            handler.setHandler(httpJsonHandler);
            acceptor.setHandler(handler);
            acceptor.bind(new InetSocketAddress(port));
            isRunning = true;
            System.out.println("Server now listening on port " + port);
        }
    }

    /**
     * 使用默认端口8080
     *
     * @throws IOException
     */
    public void run() throws IOException {
        run(DEFAULT_PORT);
    }

    /**
     * 停止监听HTTP服务
     */
    @Override
    public void stop() {
        synchronized (this) {
            if (!isRunning) {
                System.out.println("Server is already stoped.");
                return;
            }
            isRunning = false;
            try {
                acceptor.unbind();
                acceptor.dispose();
                System.out.println("Server is stoped.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        int port = DEFAULT_PORT;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-port")) {
                port = Integer.parseInt(args[i + 1]);
            }
        }

        try {
            // Create an acceptor
            NioSocketAcceptor acceptor = new NioSocketAcceptor();

            // Create a service configuration
            acceptor.getFilterChain().addLast(
                    "protocolFilter",
                    new ProtocolCodecFilter(
                            new HttpServerProtocolCodecFactory()));
            acceptor.getFilterChain().addLast("logger", new LoggingFilter());
            acceptor.setHandler(new HttpServerHandler());
            acceptor.bind(new InetSocketAddress(port));

            System.out.println("Server now listening on port " + port);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
