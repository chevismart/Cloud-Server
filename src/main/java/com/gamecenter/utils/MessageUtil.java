package com.gamecenter.utils;

import com.gamecenter.constants.ServerConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

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
}
