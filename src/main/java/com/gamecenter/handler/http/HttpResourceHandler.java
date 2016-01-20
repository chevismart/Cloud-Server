package com.gamecenter.handler.http;

import com.gamecenter.server.http.AbstractHttpRequestHandler;
import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.net.URI;

public class HttpResourceHandler extends AbstractHttpRequestHandler {
    public String process(HttpExchange httpExchange) {
        logger.info("Requesting uri is {}", httpExchange.getRequestURI());
        String root = "/web";
        URI uri = httpExchange.getRequestURI();
        try {
            InputStream resourceAsStream = HttpResourceHandler.class.getResourceAsStream(root + uri.getPath().replace(uri(), ""));

////            File file = new File(root + uri.getPath().replace(uri(), "")).getCanonicalFile();
//            File file = new File(HttpResourceHandler.class.getResource(root + uri.getPath().replace(uri(), "")).toURI()).getCanonicalFile();
//            logger.debug("file exist? {} {}",file.exists(), file.getPath());
//            if (!file.getPath().startsWith(root)) {
//                logger.warn("Suspected path traversal attack: reject with 403 error.");
//                String response = "403 (Forbidden)\n";
//                httpExchange.sendResponseHeaders(403, response.length());
//                OutputStream os = httpExchange.getResponseBody();
//                os.write(response.getBytes());
//                os.close();
//            } else if (!file.isFile()) {
//                logger.warn("Object does not exist or is not a file: reject with 404 error.");
//                String response = "404 (Not Found)\n";
//                httpExchange.sendResponseHeaders(404, response.length());
//                OutputStream os = httpExchange.getResponseBody();
//                os.write(response.getBytes());
//                os.close();
//            } else {
            logger.debug("Object exists and is a file: accept with response code 200.");
            httpExchange.sendResponseHeaders(200, 0);
            OutputStream os = httpExchange.getResponseBody();
//                FileInputStream fs = new FileInputStream(file);
            final byte[] buffer = new byte[0x10000];
            int count = 0;
            while ((count = resourceAsStream.read(buffer)) >= 0) {
                os.write(buffer, 0, count);
            }
            resourceAsStream.close();
            os.close();
//            }
        } catch (Exception e) {
            logger.error("Resource not found for {}", uri.toString());
        }
        return StringUtils.EMPTY;
    }

    public String uri() {
        return "/ui";
    }

    public static void main(String[] args) {
        (new HttpResourceHandler()).getClass().getResource("/");
    }
}
