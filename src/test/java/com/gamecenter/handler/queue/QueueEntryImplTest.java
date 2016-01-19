package com.gamecenter.handler.queue;

import com.gamecenter.model.DeviceInfo;
import com.gamecenter.model.Model;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeoutException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class QueueEntryImplTest {
    private int timeout = 1;
    private QueueEntry<Model> queueEntry;
    private DeviceInfo deviceInfo = mock(DeviceInfo.class);
    private Model model = mock(Model.class);

    @Before
    public void setUp() throws Exception {
        queueEntry = new QueueEntryImpl(timeout);
    }

    @Test
    public void ifTheQueueIsReadyThenWillBreakFromWaiting() throws Exception {
        queueEntry.ready();
        queueEntry.setResult(model);
        assertThat(queueEntry.getResult(), is(model));
    }

    @Test(expected = TimeoutException.class)
    public void exceptionWillBeThrownIfTimeout() throws Exception {
        Thread.sleep(timeout * 1000 * 2);
        queueEntry.getResult();
    }

    @Test
    public void canCalculateElapseTime() throws Exception {
        Thread.sleep(timeout * 1000 / 2);
        queueEntry.ready();
        queueEntry.setResult(deviceInfo);
        assertThat(queueEntry.elapse() >= timeout * 1000 / 2, is(true));
    }
}