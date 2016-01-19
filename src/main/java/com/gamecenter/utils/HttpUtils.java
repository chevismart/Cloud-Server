package com.gamecenter.utils;

import com.gamecenter.model.HttpRequestMessage;

/**
 * Created by chevi on 2016/1/14.
 */
public class HttpUtils {

    public static final String JSONP_CALLBACK = "jsonpCallback";

    public static String buildJsonResponse(HttpRequestMessage request, String jsonStr) {
        return request.getParameter(JSONP_CALLBACK) + "(" + jsonStr + ")";
    }


}
