package com.gamecenter.utils;

import com.gamecenter.constants.ServerConstants;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Chevis on 14-10-2.
 */
public class MessageUtil {

    public static boolean isStatusOn(String statusStr) {
        return StringUtils.isNotEmpty(statusStr) && statusStr.equalsIgnoreCase(ServerConstants.STATUS_ON) ? true : false;
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
}
