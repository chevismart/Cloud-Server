package com.gamecenter.server;

import com.gamecenter.handler.HttpServerHandler;
import com.gamecenter.server.http.MinaHttpServer;
import com.gamecenter.server.tcp.MinaLongConnServer;

import java.io.IOException;

/**
 * Created by Boss on 2014/8/30.
 */
public class CloudServer {


    public static void main(String[] arg) throws IOException {
        MinaLongConnServer server = new MinaLongConnServer();
        server.start();
        MinaHttpServer httpServer = new MinaHttpServer(new HttpServerHandler(),8003);
        httpServer.start();
    }
}
