package com.gamecenter.server.http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public abstract class AbstractHttpRequestHandler implements HttpRequestHandler {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public void handle(HttpExchange httpExchange) throws IOException {
        String response = process(httpExchange);
        OutputStream out = httpExchange.getResponseBody();  //获得输出流
        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        httpExchange.getResponseHeaders().set("Connection", "close");
        httpExchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        httpExchange.sendResponseHeaders(getStatusCode(response), response.length()); //设置响应头属性及响应信息的长度
        logger.debug("Response is {} and status code is {}",response, getStatusCode(response));
        out.write(response.getBytes());
        out.flush();
        out.close();
        httpExchange.getRequestHeaders().add("Connection", "Close"); // Use for close thread in cached thread pool in server
        httpExchange.close();
    }

    private int getStatusCode(String response) {
        return response.trim().equals("") ? HttpStatus.SC_BAD_REQUEST : HttpStatus.SC_OK;
    }

    protected String getRequestBody(HttpExchange httpExchange) throws IOException {
        return IOUtils.toString(httpExchange.getRequestBody());
    }

    protected Headers getHeader(HttpExchange httpExchange) {
        return httpExchange.getRequestHeaders();
    }

    protected String getParameter(HttpExchange httpExchange, String key) {
        return queryToMap(httpExchange.getRequestURI().getQuery()).get(key);
    }

    private Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }

    public abstract String process(HttpExchange httpExchange);

}
