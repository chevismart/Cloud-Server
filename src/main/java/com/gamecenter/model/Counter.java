package com.gamecenter.model;

import java.util.Date;

/**
 * Created by Chevis on 14-10-2.
 */
public class Counter {

    private boolean isCoinOn;
    private boolean isPrizeOn;
    private int coinQty;
    private int prizeQty;
    private Date lastCoinResetTime;
    private Date lastPrizeResetTime;
    private Date lastStatusTime;
    private Date lastQtyTime;

    public Counter() {
        Date now = new Date();
        lastCoinResetTime = now;
        lastPrizeResetTime = now;
        lastStatusTime = now;
        lastQtyTime = now;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Counter counter = (Counter) o;

        if (coinQty != counter.coinQty) return false;
        if (isCoinOn != counter.isCoinOn) return false;
        if (isPrizeOn != counter.isPrizeOn) return false;
        if (prizeQty != counter.prizeQty) return false;
        if (lastCoinResetTime != null ? !lastCoinResetTime.equals(counter.lastCoinResetTime) : counter.lastCoinResetTime != null)
            return false;
        if (lastPrizeResetTime != null ? !lastPrizeResetTime.equals(counter.lastPrizeResetTime) : counter.lastPrizeResetTime != null)
            return false;
        if (lastQtyTime != null ? !lastQtyTime.equals(counter.lastQtyTime) : counter.lastQtyTime != null) return false;
        if (lastStatusTime != null ? !lastStatusTime.equals(counter.lastStatusTime) : counter.lastStatusTime != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (isCoinOn ? 1 : 0);
        result = 31 * result + (isPrizeOn ? 1 : 0);
        result = 31 * result + coinQty;
        result = 31 * result + prizeQty;
        result = 31 * result + (lastCoinResetTime != null ? lastCoinResetTime.hashCode() : 0);
        result = 31 * result + (lastPrizeResetTime != null ? lastPrizeResetTime.hashCode() : 0);
        result = 31 * result + (lastStatusTime != null ? lastStatusTime.hashCode() : 0);
        result = 31 * result + (lastQtyTime != null ? lastQtyTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Counter{" +
                "isCoinOn=" + isCoinOn +
                ", isPrizeOn=" + isPrizeOn +
                ", coinQty=" + coinQty +
                ", prizeQty=" + prizeQty +
                ", lastCoinResetTime=" + lastCoinResetTime +
                ", lastPrizeResetTime=" + lastPrizeResetTime +
                ", lastStatusTime=" + lastStatusTime +
                ", lastQtyTime=" + lastQtyTime +
                '}';
    }

    public Date getLastStatusTime() {
        return lastStatusTime;
    }

    public void setLastStatusTime(Date lastStatusTime) {
        this.lastStatusTime = lastStatusTime;
    }

    public Date getLastQtyTime() {
        return lastQtyTime;
    }

    public void setLastQtyTime(Date lastQtyTime) {
        this.lastQtyTime = lastQtyTime;
    }

    public boolean isCoinOn() {
        return isCoinOn;
    }

    public void setCoinOn(boolean isCoinOn) {
        this.isCoinOn = isCoinOn;
    }

    public boolean isPrizeOn() {
        return isPrizeOn;
    }

    public void setPrizeOn(boolean isPrizeOn) {
        this.isPrizeOn = isPrizeOn;
    }

    public int getCoinQty() {
        return coinQty;
    }

    public void setCoinQty(int coinQty) {
        this.coinQty = coinQty;
    }

    public int getPrizeQty() {
        return prizeQty;
    }

    public void setPrizeQty(int prizeQty) {
        this.prizeQty = prizeQty;
    }

    public Date getLastCoinResetTime() {
        return lastCoinResetTime;
    }

    public void setLastCoinResetTime(Date lastCoinResetTime) {
        this.lastCoinResetTime = lastCoinResetTime;
    }

    public Date getLastPrizeResetTime() {
        return lastPrizeResetTime;
    }

    public void setLastPrizeResetTime(Date lastPrizeResetTime) {
        this.lastPrizeResetTime = lastPrizeResetTime;
    }
}
