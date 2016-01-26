package com.gamecenter.handler.queue;

import com.gamecenter.model.Model;
import org.apache.commons.lang.time.DateUtils;

import java.util.Date;
import java.util.concurrent.TimeoutException;

public class  QueueEntryImpl implements QueueEntry<Model>{

    private final int timeout;
    private final Date startWatingTime;
    private Model model;
    private boolean keepWaiting = true;
    private Date endWaitingTime;

    public QueueEntryImpl(int timeoutInSecond) {
        this.timeout = timeoutInSecond;
        this.startWatingTime = new Date();
    }

    private void await() throws TimeoutException {
        while (new Date().before(DateUtils.addSeconds(this.startWatingTime, timeout)))
            if (!keepWaiting) return;
        throw new TimeoutException("Queue entry time out!");
    }

    public Model getResult() throws TimeoutException {
        await();
        return model;
    }

    public void setResult(Model value) {
        model = value;
    }

    public void ready() {
        keepWaiting = false;
        endWaitingTime = new Date();
    }

    public long elapse() {
        try {
            return endWaitingTime == null ? -1 : endWaitingTime.getTime() - startWatingTime.getTime();
        } catch (Exception e) {
            return -1;
        }
    }
}
