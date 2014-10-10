package com.gamecenter.utils;

import com.gamecenter.constants.ServerConstants;
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

    public static boolean waitForResponse(Date startTime, Date targetTime, int timeoutInSecond) {
        if (null != startTime && null != targetTime && timeoutInSecond > 0) {
            logger.debug("Raised request at {}", new Date());
            boolean isReplied = false;
            while (!startTime.before(targetTime)) {
                if (!startTime.before(getExpireTime(startTime, timeoutInSecond))) {
                    isReplied = true;
                    break;
                }
            }
            if (isReplied) {
                logger.debug("Received response at {}", targetTime);
                logger.info("Travel for time TCP is {} milliseconds.", targetTime.getTime() - startTime.getTime());
            } else {
                logger.warn("Request Timeout!");
            }
            return isReplied;
        } else {
            logger.error("Invalid parameters! Start time = {}, targetTime = {}, timeout = {}", startTime, targetTime, timeoutInSecond);
            return false;
        }
    }
}
