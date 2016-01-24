package com.gamecenter.model;

import org.apache.mina.core.session.IoSession;
import org.gamecenter.serializer.messages.MessageHeader;
import org.gamecenter.serializer.utils.ByteUtil;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DeviceInfo implements Model {

    private IoSession session;
    private MessageHeader messageHeader;
    private Counter counter;
    private Map<String, TopUp> topUpHistory; // referenceId : topup
    private Power power;
    private final String mac;
    private Date lastOnlineTime;

    public DeviceInfo(String mac) {
        this.mac = mac;
        this.topUpHistory = new ConcurrentHashMap<>();
        this.counter = new Counter();
        this.power = new Power();
        this.lastOnlineTime = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceInfo that = (DeviceInfo) o;

        if (session != null ? !session.equals(that.session) : that.session != null) return false;
        if (messageHeader != null ? !messageHeader.equals(that.messageHeader) : that.messageHeader != null)
            return false;
        if (counter != null ? !counter.equals(that.counter) : that.counter != null) return false;
        if (topUpHistory != null ? !topUpHistory.equals(that.topUpHistory) : that.topUpHistory != null) return false;
        if (power != null ? !power.equals(that.power) : that.power != null) return false;
        if (mac != null ? !mac.equals(that.mac) : that.mac != null) return false;
        return lastOnlineTime != null ? lastOnlineTime.equals(that.lastOnlineTime) : that.lastOnlineTime == null;

    }

    @Override
    public int hashCode() {
        int result = session != null ? session.hashCode() : 0;
        result = 31 * result + (messageHeader != null ? messageHeader.hashCode() : 0);
        result = 31 * result + (counter != null ? counter.hashCode() : 0);
        result = 31 * result + (topUpHistory != null ? topUpHistory.hashCode() : 0);
        result = 31 * result + (power != null ? power.hashCode() : 0);
        result = 31 * result + (mac != null ? mac.hashCode() : 0);
        result = 31 * result + (lastOnlineTime != null ? lastOnlineTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "session=" + session +
                ", messageHeader=" + messageHeader +
                ", counter=" + counter +
                ", topUpHistory=" + topUpHistory +
                ", power=" + power +
                ", mac='" + mac + '\'' +
                ", lastOnlineTime=" + lastOnlineTime +
                '}';
    }

    public Power getPower() {
        return power;
    }

    public void setPower(Power power) {
        this.power = power;
    }

    public Map<String, TopUp> getTopUpHistory() {
        return topUpHistory;
    }

    public void setTopUpHistory(Map<String, TopUp> topUpHistory) {
        this.topUpHistory = topUpHistory;
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

    public String getMac() {
        return mac;
    }

    public Date getLastOnlineTime() {
        return lastOnlineTime;
    }

    public void setLastOnlineTime(Date lastOnlineTime) {
        this.lastOnlineTime = lastOnlineTime;
    }
}
