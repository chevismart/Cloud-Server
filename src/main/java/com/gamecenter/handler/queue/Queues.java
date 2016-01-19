package com.gamecenter.handler.queue;

import com.gamecenter.model.Model;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.gamecenter.serializer.constants.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class Queues {

    public final static Queues instance = new Queues();

    private final Table<MessageType, String, List<QueueEntry<Model>>> queues = HashBasedTable.create();
    private final Logger logger = LoggerFactory.getLogger(Queues.class);

    public void put(MessageType messageType, String deviceId, QueueEntry<Model> queueEntry) {
        List<QueueEntry<Model>> queueEntryList = queues.get(messageType, deviceId);
        if (queueEntryList == null) {
            queueEntryList = newArrayList();
            queues.put(messageType, deviceId, queueEntryList);
        }
        queueEntryList.add(queueEntry);
    }

    public void consume(MessageType messageType, String deviceId, Model model) {
        List<QueueEntry<Model>> queueEntryList = queues.get(messageType, deviceId);
        if (queueEntryList != null) {
            List<QueueEntry<Model>> toBeRemoved = newArrayList();
            logger.debug("Queue list size is {}", queueEntryList.size());
            for (QueueEntry<Model> entry : queueEntryList) {
                entry.setResult(model);
                entry.ready();
                toBeRemoved.add(entry);
            }
            synchronized (queues) {
                queueEntryList.removeAll(toBeRemoved);
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
}
