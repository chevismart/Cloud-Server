package com.gamecenter.model;

import java.util.Date;

/**
 * Created by Chevis on 14-10-3.
 */
public class Power {

    private boolean status;
    private Date updateTime;

    public Power() {
        this.updateTime = new Date();
    }

    @Override
    public String toString() {
        return "Power{" +
                "status=" + status +
                ", updateTime=" + updateTime +
                '}';
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
