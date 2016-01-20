package com.gamecenter.handler.http;

import com.alibaba.fastjson.JSONObject;
import com.gamecenter.handler.tcp.DeviceListProxy;
import com.gamecenter.server.http.AbstractHttpRequestHandler;
import com.sun.net.httpserver.HttpExchange;

import java.util.List;

import static com.gamecenter.constants.ServerConstants.JsonConst.CENTER_ID;

public class DeviceListHandler extends AbstractHttpRequestHandler {

    private final DeviceListProxy deviceListProxy;

    public DeviceListHandler(DeviceListProxy deviceListProxy) {
        this.deviceListProxy = deviceListProxy;
    }

    public String process(HttpExchange httpExchange) {
        String centerId = getParameter(httpExchange, CENTER_ID);
        List<String> devicesMacList = deviceListProxy.getOninceDevicesByCenterId(centerId);
        logger.info("The mac list of center id {} is {}", centerId, devicesMacList);
        return JSONObject.toJSONString(devicesMacList);
    }

    public String uri() {
        return "/listDevice";
    }
}
