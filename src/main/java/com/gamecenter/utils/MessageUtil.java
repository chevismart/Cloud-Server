package com.gamecenter.utils;

import com.gamecenter.constants.ServerConstants;
import com.gamecenter.handler.HttpJsonHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by Chevis on 14-10-2.
 */
public class MessageUtil {

    public static int TCP_MESSAGE_TIMEOUT_IN_SECOND = 5;

    private static Logger logger = LoggerFactory.getLogger(MessageUtil.class);

    public static boolean isStatusOn(String statusStr) {
        return StringUtils.isNotEmpty(statusStr) && statusStr.equalsIgnoreCase(ServerConstants.STATUS_ON) ? true : false;
    }

    public static boolean isTrue(String value) {
        return StringUtils.isNotEmpty(value) ? Boolean.valueOf(value) : false;
    }

    public static boolean isQuery(String value) {
        return StringUtils.isNotEmpty(value) && value.equalsIgnoreCase(ServerConstants.YES) ? true : false;
    }

    public static boolean isSuccess(String value) {
        return isQuery(value);
    }

    public static String isEnable(boolean bool) {
        return bool ? ServerConstants.ENABLED : ServerConstants.DISABLED;
    }

    public static boolean isPowerOn(String value) {
        return StringUtils.isNotEmpty(value) && value.equalsIgnoreCase(ServerConstants.POWER_ON) ? true : false;
    }

    public static String isPowerOn(boolean isPowerOn) {
        return isPowerOn ? ServerConstants.POWER_ON : ServerConstants.POWER_OFF;
    }

    public static Date getExpireTime(Date now, int timeoutInSecond) {
        int delayTime = timeoutInSecond;
        long nowTime = now.getTime();
        long expiryTime = nowTime + delayTime * 1000L;
        return new Date(expiryTime);
    }

    public static boolean waitForResponse(HttpJsonHandler handler, int timeoutInSecond) {


        if (null != handler && timeoutInSecond > 0) {
            boolean isExpired = false;
            Date expiredTime = getExpireTime(handler.getUpdateTime(), timeoutInSecond);
            while (!handler.await()) {
                if (!handler.getRequestTime().before(expiredTime)) {
                    isExpired = true;
                    break;
                }
            }
            if (!isExpired) {
                logger.info("Received response at {} {}", handler.getUpdateTime(), handler.getUpdateTime().getTime());
            } else {
                logger.warn("Request Timeout!");
            }
            return !isExpired;
        } else {
            logger.error("Invalid parameters! Start time = {}, targetTime = {}, timeout = {}", handler.getUpdateTime(), handler.getRequestTime(), timeoutInSecond);
            return false;
        }
    }

    public static boolean isKeeyWaiting(Date requestTime, Date targetTime) {
        return !requestTime.before(targetTime);
    }
}
