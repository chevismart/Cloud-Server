package com.gamecenter.handler.queue;

import com.gamecenter.model.DeviceInfo;
import com.gamecenter.model.Initialization;
import com.gamecenter.model.Model;
import com.gamecenter.model.TopUp;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import org.apache.commons.lang.time.DateUtils;
import org.gamecenter.serializer.constants.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Queues implements Callable<String> {

    public final static Queues instance = new Queues();

    private final Table<MessageType, String, ConcurrentLinkedQueue<QueueEntry<Model>>> queues = HashBasedTable.create();
    private final Logger logger = LoggerFactory.getLogger(Queues.class);

    public void put(MessageType messageType, String deviceId, QueueEntry<Model> queueEntry) {
        ConcurrentLinkedQueue<QueueEntry<Model>> queueEntryList = queues.get(messageType, deviceId);
        if (queueEntryList == null) {
            queueEntryList = new ConcurrentLinkedQueue<>();
            queues.put(messageType, deviceId, queueEntryList);
        }
        queueEntryList.add(queueEntry);
    }

    public void consume(MessageType messageType, String deviceId, Model model) {
        ConcurrentLinkedQueue<QueueEntry<Model>> queueEntryList = queues.get(messageType, deviceId);
        if (queueEntryList != null) {
            logger.debug("Queue list size is {}", queueEntryList.size());
            for (QueueEntry<Model> entry : queueEntryList) {
                entry.setResult(model);
                entry.ready();
            }
        } else {
            logger.warn("Queue not found for {}", deviceId);
        }
    }

    @Override
    public String toString() {
        return "Queues{" +
                "queues=" + queues +
                '}';
    }

    public String call() throws Exception {
        logger.debug("Start to cleanup topup history.");
        Date now = new Date();
        for (DeviceInfo deviceInfo : Initialization.getInstance().getClientMap().values()) {
            List<String> toBeRemoved = Lists.newArrayList();
            for (Map.Entry<String, TopUp> entry : deviceInfo.getTopUpHistory().entrySet()) {
                if (DateUtils.addMinutes(entry.getValue().getUpdateTime(), 5).after(now)) {
                    toBeRemoved.add(entry.getKey());
                }
            }
            for (String refId : toBeRemoved) {
                deviceInfo.getTopUpHistory().remove(refId);
                logger.info("Clear topup history for {}", refId);
            }
        }
        logger.debug("Cleanup topup history end.");
        return null;
    }
}
