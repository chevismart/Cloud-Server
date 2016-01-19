package com.gamecenter.handler.tcp;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.gamecenter.handler.queue.QueueEntry;
import com.gamecenter.handler.queue.QueueEntryImpl;
import com.gamecenter.handler.queue.Queues;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.model.Model;
import org.gamecenter.serializer.constants.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class TcpMessageExecutor implements Executor {

    public final static TcpMessageExecutor executor = new TcpMessageExecutor(Queues.instance);
    private final Queues queues;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public TcpMessageExecutor(Queues queues) {
        this.queues = queues;
    }

    @Override
    public synchronized QueueEntry<Model> execute(byte[] message, MessageType messageType, DeviceInfo deviceInfo) {
        QueueEntry<Model> queueEntry = new QueueEntryImpl(TIMEOUT);
        queues.put(messageType, deviceInfo.getMac(), queueEntry);
        logger.debug("Query Entry is added for message type [{}]", messageType);
        deviceInfo.getSession().write(message);
        logger.info("Message({}) is sent: {}", ByteArrayUtil.toHexString(message), Arrays.toString(message));
        return queueEntry;
    }
}
