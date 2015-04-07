package com.gamecenter.utils;

import com.gamecenter.handler.http.TopUpHandler;
import com.gamecenter.handler.tcp.CounterProxy;
import com.gamecenter.model.TopUp;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;

public class MessageUtilTest {
    CounterProxy counterProxy = mock(CounterProxy.class);
    TopUpHandler handler = new TopUpHandler(counterProxy);
    TopUp topup;
    int timeoutInSec = 2;
    private Date now = new Date();

    @Before
    public void setUp() throws Exception {
        topup = new TopUp(now);
        Whitebox.setInternalState(handler, "topUp", topup);
        Whitebox.setInternalState(handler, "requestTime", now);
    }

    @Test
    public void testName() throws Exception {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 8, 0L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(100000), new ThreadPoolExecutor.CallerRunsPolicy());
        //        when(handler.getRequestTime()).thenReturn(now);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.err.println("Wait for response start");
                MessageUtil.waitForResponse(handler, timeoutInSec);
            }
        });

        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.err.println("Start waiting");
                try {
                    Thread.sleep(timeoutInSec * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.err.println("Stop waiting");
                topup.setUpdateTime(new Date());
            }
        });
        Thread.sleep(timeoutInSec * 10000);

    }
}