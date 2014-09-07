package com.gamecenter.handler;

import com.gamecenter.constants.ServerConstants;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class HttpServerHandlerTest extends TestCase {

    Map<String, String> context = new HashMap<String, String>();


    String centerId = "center";
    String token = "chevis";
    String type = "CLIENT_LIST";
    String dataType = "jsonp";

    @Test
    public void receiveHttpRequestWithFullParameters() throws Exception {
        context.put(ServerConstants.JsonConst.CENTER_ID, centerId);
        context.put(ServerConstants.JsonConst.TOKEN, token);
        context.put(ServerConstants.JsonConst.REQUEST_TYPE, type);
        context.put(ServerConstants.JsonConst.DATA_TYPE)

    }
}