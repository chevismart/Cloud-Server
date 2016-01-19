package com.gamecenter.server.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.apache.http.impl.client.HttpClients;

import java.io.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class HttpServiceTest {

    private HttpService httpService;
    private final int port = 8088;
    private final String uri = "/uri";
    private final HttpClient httpclient = HttpClients.createDefault();

    @Before
    public void setUp() throws Exception {
        httpService = new HttpService(port, 10);
        httpService.start();
        httpService.register(new HttpRequestHandler() {
                                 public String uri() {
                                     return uri;
                                 }

                                 public void handle(HttpExchange httpExchange) throws IOException {
                                     generateHandler().handle(httpExchange);
                                 }
                             }
        );
    }

    @After
    public void tearDown() throws Exception {
        httpService.stop();
    }

    @Test
    public void httpServerCanStartupAndListeningHttpPort() throws Exception {
        HttpResponse response = httpclient.execute(new HttpGet("http://localhost:" + port + "/" + uri));
        assertThat(response.getStatusLine().toString(), is("HTTP/1.1 200 OK"));
    }

    @Test
    public void registerHttpRequestHandlerSuccessAndTheHandlerCanProcessTheRequest() throws Exception {
        httpService.register(new HttpRequestHandler() {
            public String uri() {
                return "uri1";
            }
            public void handle(HttpExchange httpExchange) throws IOException {
                generateHandler().handle(httpExchange);
            }
        });
    }

    private static HttpHandler generateHandler() {
        return new HttpHandler() {
            public void handle(HttpExchange httpExchange) throws IOException {
                String responseMsg = "ok";   //响应信息
                InputStream in = httpExchange.getRequestBody(); //获得输入流
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String temp = null;
                while ((temp = reader.readLine()) != null) {
                    System.out.println("client request:" + temp);
                }
                httpExchange.sendResponseHeaders(200, responseMsg.length()); //设置响应头属性及响应信息的长度
                OutputStream out = httpExchange.getResponseBody();  //获得输出流
                out.write(responseMsg.getBytes());
                out.flush();
                httpExchange.close();
            }
        };
    }
}