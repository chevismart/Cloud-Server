package com.gamecenter.handler.tcp;

import com.gamecenter.handler.queue.QueueEntry;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.model.Model;
import org.gamecenter.serializer.constants.MessageType;

/**
 * Created by chevi on 2016/1/18.
 */
public interface Executor {
    int TIMEOUT = 10;

    QueueEntry<Model> execute(byte[] message, MessageType messageType, DeviceInfo deviceInfo);
}
