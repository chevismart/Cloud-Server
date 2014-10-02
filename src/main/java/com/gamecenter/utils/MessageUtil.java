package com.gamecenter.utils;

import com.gamecenter.constants.ServerConstants;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Chevis on 14-10-2.
 */
public class MessageUtil {

    public static boolean isStatusOn(String statusStr) {
        return StringUtils.isNotEmpty(statusStr) && statusStr.equals(ServerConstants.STATUS_ON) ? true : false;
    }

    public static boolean isQuery(String value) {
        return StringUtils.isNotEmpty(value) ? Boolean.valueOf(value) : false;
    }

}
