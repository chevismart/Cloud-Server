package com.gamecenter.model;

import org.apache.mina.core.session.IoSession;
import org.gamecenter.serializer.messages.MessageHeader;
import org.gamecenter.serializer.utils.ByteUtil;

/**
 * Created by Chevis on 14-9-19.
 */
public class DeviceInfo {

    private IoSession session;
    private MessageHeader messageHeader;
    private Counter counter;

    public DeviceInfo() {
        this.counter = new Counter();
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "session=" + session +
                ", messageHeader=" + messageHeader +
                ", counter=" + counter +
                '}';
    }

    public Counter getCounter() {
        return counter;
    }

    public void setCounter(Counter counter) {
        this.counter = counter;
    }

    public IoSession getSession() {
        return session;
    }

    public void setSession(IoSession session) {
        this.session = session;
    }

    public MessageHeader getMessageHeader() {
        return messageHeader;
    }

    public void setMessageHeader(MessageHeader messageHeader) {
        this.messageHeader = messageHeader;
    }

    public MessageHeader getHeaderWithMessageNumIncreasment() {
        if (null != messageHeader && null != messageHeader.getMessageSN()) {
            int sn = ByteUtil.getInteger(messageHeader.getMessageSN());
            messageHeader.setMessageSN(ByteUtil.getBytes(sn++));
        }
        return messageHeader;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceInfo that = (DeviceInfo) o;

        if (counter != null ? !counter.equals(that.counter) : that.counter != null) return false;
        if (messageHeader != null ? !messageHeader.equals(that.messageHeader) : that.messageHeader != null)
            return false;
        if (session != null ? !session.equals(that.session) : that.session != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = session != null ? session.hashCode() : 0;
        result = 31 * result + (messageHeader != null ? messageHeader.hashCode() : 0);
        result = 31 * result + (counter != null ? counter.hashCode() : 0);
        return result;
    }
}
