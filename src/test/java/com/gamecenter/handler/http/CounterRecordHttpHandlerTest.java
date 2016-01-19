package com.gamecenter.handler.http;

import com.gamecenter.handler.tcp.CounterProxy;
import com.gamecenter.server.http.HttpService;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class CounterRecordHttpHandlerTest {
    private final int port = 8888;
    private HttpService httpService = new HttpService(port, 5);
    private CounterRecordHttpHandler counterRecordHttpHandler;
    private final CounterProxy counterProxy = mock(CounterProxy.class);
    private final HttpClient httpclient = HttpClients.createDefault();

    @Before
    public void setUp() throws Exception {
        counterRecordHttpHandler = new CounterRecordHttpHandler(counterProxy);
        httpService.start();
        httpService.register(counterRecordHttpHandler);
    }

    @After
    public void tearDown() throws Exception {
        httpService.stop();
    }

    @Test(expected = NoHttpResponseException.class)
    public void deviceNotFoundWillReturnEmpty() throws Exception {
        HttpResponse response =httpclient.execute(new HttpGet("http://localhost:" + port + "/" + counterRecordHttpHandler.uri()));
    }
}