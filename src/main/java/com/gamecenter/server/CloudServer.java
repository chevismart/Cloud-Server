package com.gamecenter.server;

import com.gamecenter.handler.http.*;
import com.gamecenter.handler.queue.Queues;
import com.gamecenter.handler.tcp.CounterProxy;
import com.gamecenter.handler.tcp.DeviceListProxy;
import com.gamecenter.handler.tcp.PowerProxy;
import com.gamecenter.handler.tcp.TcpMessageExecutor;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.model.Initialization;
import com.gamecenter.server.http.HttpService;
import com.gamecenter.server.tcp.MinaLongConnServer;
import com.gamecenter.utils.SessionUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

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
        httpService.register(new HttpResourceHandler());
        httpService.register(new CounterRecordHttpHandler(counterProxy));
        httpService.register(new TopupHttpHandler(counterProxy));
        httpService.register(new CounterRecordHttpHandler(counterProxy));
        httpService.register(new CounterResetHttpHandler(counterProxy));
        httpService.register(new CounterStatusHttpHandler(counterProxy));
        httpService.register(new CounterSwitchHttpHandler(counterProxy));
        httpService.register(new PowerControlHttpHandler(powerProxy));
        httpService.register(new PowerStatusHttpHandler(powerProxy));
        httpService.register(new DeviceListHandler(deviceListProxy));

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(
                new FutureTask<>(new Callable<String>() {
                    public String call() throws Exception {
                        Logger logger = LoggerFactory.getLogger(this.getClass());
                        logger.info("Scheduled task for session management start!");
                        Date now = new Date();
                        List<DeviceInfo> toBeRemoved = Lists.newArrayList();
                        for (DeviceInfo deviceInfo : Initialization.getInstance().getClientMap().values()) {
                            if(DateUtils.addMinutes(deviceInfo.getLastOnlineTime(),5).before(now))
                                toBeRemoved.add(deviceInfo);
                        }
                        for(DeviceInfo deviceInfo : toBeRemoved){
                            SessionUtil.removeSession(deviceInfo.getSession());
                            logger.info("Device session(mac={}) is removed.", deviceInfo.getMac());
                        }
                        return null;
                    }
                }),
                0,
                60000,
                MILLISECONDS);
    }
}
