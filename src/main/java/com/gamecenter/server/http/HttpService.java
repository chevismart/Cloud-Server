package com.gamecenter.server.http;

import com.gamecenter.handler.http.CounterRecordHttpHandler;
import com.gamecenter.handler.tcp.CounterProxy;
import com.gamecenter.handler.tcp.TcpMessageExecutor;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class HttpService {
    private final int port;
    private final int concurrentNum;
    private final Logger logger = LoggerFactory.getLogger(HttpService.class);
    private HttpServer httpServer;

    public HttpService(int port, int concurrentNum) {
        this.port = port;
        this.concurrentNum = concurrentNum;
    }

    public void start() {
        if (httpServer == null) {
            HttpServerProvider provider = HttpServerProvider.provider();
            try {
                httpServer = provider.createHttpServer(new InetSocketAddress(port), concurrentNum);
                httpServer.setExecutor(Executors.newCachedThreadPool());
                httpServer.start();
                logger.info("Http Server is started and listening on [{}]", port);
            } catch (IOException e) {
                logger.error("Http Server startup failure with exception: ", e);
            }
        }
    }

    public void stop() {
        httpServer.stop(1);
        httpServer = null;
    }

    public static void main(String[] args) {
        HttpService httpService = new HttpService(8080, 10);
        httpService.start();
        httpService.register(new CounterRecordHttpHandler(new CounterProxy(TcpMessageExecutor.executor)));
    }

    public void register(HttpRequestHandler httpResponseHandler) {
        httpServer.createContext(getUri(httpResponseHandler), httpResponseHandler);
    }

    private String getUri(HttpRequestHandler httpResponseHandler) {
        String uri = httpResponseHandler.uri();
        return uri.startsWith("/") ? uri : "/".concat(uri);
    }

    public void unregister(HttpRequestHandler httpRequestHandler) {
        httpServer.removeContext(getUri(httpRequestHandler));
    }

    public static String getContent(HttpExchange httpExchange) throws IOException {
        return IOUtils.toString(httpExchange.getRequestBody());
    }

    public void sendMessage(HttpExchange httpExchange, String message) {
        OutputStream out = httpExchange.getResponseBody();  //获得输出流
        try {
            out.write(message.getBytes());
            out.flush();
        } catch (IOException e) {
            logger.error("Response http request failed: ", e);
        } finally {
            httpExchange.close();
        }
    }
}
