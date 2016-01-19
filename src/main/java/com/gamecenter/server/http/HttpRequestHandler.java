package com.gamecenter.server.http;

import com.sun.net.httpserver.HttpHandler;

public interface HttpRequestHandler extends HttpHandler{
    String uri();
}
