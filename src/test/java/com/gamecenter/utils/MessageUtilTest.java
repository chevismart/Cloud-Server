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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class MessageUtilTest {
    CounterProxy counterProxy = mock(CounterProxy.class);
    TopUpHandler handler = new TopUpHandler(counterProxy);
    TopUp topup;
    int timeoutInSec = 5;
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
                boolean result = MessageUtil.waitForResponse(handler, timeoutInSec);
                assertThat(result, is(true));
            }
        });

        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.err.println("Start waiting: " + new Date().getTime());
                try {
                    Thread.sleep(4 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.err.println("Stop waiting: " + new Date().getTime());
                topup.setUpdateTime(new Date());
            }
        });
        Thread.sleep(10 * 1000);

    }
}