package com.gamecenter.server;

import com.gamecenter.handler.http.*;
import com.gamecenter.handler.queue.Queues;
import com.gamecenter.handler.tcp.CounterProxy;
import com.gamecenter.handler.tcp.DeviceListProxy;
import com.gamecenter.handler.tcp.PowerProxy;
import com.gamecenter.handler.tcp.TcpMessageExecutor;
import com.gamecenter.server.http.HttpService;
import com.gamecenter.server.tcp.MinaLongConnServer;

import java.io.IOException;

public class CloudServer {


    private static final Queues queues = Queues.instance;
    private static final TcpMessageExecutor executor = new TcpMessageExecutor(queues);

    public static void main(String[] arg) throws IOException {
        MinaLongConnServer server = new MinaLongConnServer(queues);
        server.start();
        HttpService httpService = new HttpService(8003, 10);
        httpService.start();
        CounterProxy counterProxy = new CounterProxy(executor);
        PowerProxy powerProxy = new PowerProxy(executor);
        DeviceListProxy deviceListProxy = new DeviceListProxy();
        httpService.register(new CounterRecordHttpHandler(counterProxy));
        httpService.register(new TopupHttpHandler(counterProxy));
        httpService.register(new CounterRecordHttpHandler(counterProxy));
        httpService.register(new CounterResetHttpHandler(counterProxy));
        httpService.register(new CounterStatusHttpHandler(counterProxy));
        httpService.register(new CounterSwitchHttpHandler(counterProxy));
        httpService.register(new PowerControlHttpHandler(powerProxy));
        httpService.register(new PowerStatusHttpHandler(powerProxy));
        httpService.register(new DeviceListHandler(deviceListProxy));
    }
}
