package com.gamecenter.model;

import java.util.Date;

/**
 * Created by Chevis on 14-10-3.
 */
public class TopUp {
    private String referenceId;
    private int coinQty;
    private boolean topUpResult;
    private Date updateTime;

    public TopUp() {
        this.updateTime = new Date();
    }

    @Override
    public String toString() {
        return "TopUp{" +
                "referenceId='" + referenceId + '\'' +
                ", coinQty=" + coinQty +
                ", topUpResult=" + topUpResult +
                ", updateTime=" + updateTime +
                '}';
    }

    public boolean isTopUpResult() {
        return topUpResult;
    }

    public void setTopUpResult(boolean topUpResult) {
        this.topUpResult = topUpResult;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public int getCoinQty() {
        return coinQty;
    }

    public void setCoinQty(int coinQty) {
        this.coinQty = coinQty;
    }
}