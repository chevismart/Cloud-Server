package com.gamecenter.handler.queue;

import java.util.concurrent.TimeoutException;

public interface QueueEntry<T> {
//    void await() throws TimeoutException;//Throw exception if timeout

    T getResult() throws TimeoutException;

    void setResult(T value);

    void ready();

    long elapse();
}
