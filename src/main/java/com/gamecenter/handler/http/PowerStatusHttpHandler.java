package com.gamecenter.handler.http;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.alibaba.fastjson.JSONObject;
import com.gamecenter.handler.tcp.PowerProxy;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.model.Power;
import com.gamecenter.server.http.AbstractHttpRequestHandler;
import com.gamecenter.utils.SessionUtil;
import com.sun.net.httpserver.HttpExchange;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.gamecenter.constants.ServerConstants.JsonConst;
import static com.gamecenter.constants.ServerConstants.JsonConst.MAC;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class PowerStatusHttpHandler extends AbstractHttpRequestHandler {

    private final PowerProxy powerProxy;

    public PowerStatusHttpHandler(PowerProxy powerProxy) {
        this.powerProxy = powerProxy;
    }

    public String process(HttpExchange httpExchange) {
        String response = EMPTY;
        String mac = getParameter(httpExchange, MAC);
        Map<String, DeviceInfo> deviceInfoMap = SessionUtil.getDeviceInfoByMacAddress(ByteArrayUtil.hexStringToByteArray(mac));
        if (null != deviceInfoMap && !deviceInfoMap.isEmpty()) {

            DeviceInfo deviceInfo = deviceInfoMap.values().iterator().next();

            Power power;
            try {
                power = (Power) powerProxy.queryPowerStatus(deviceInfo).getResult();
                Map<String, String> respMap = new HashMap<>();
                respMap.put(JsonConst.POWER_STATUS, String.valueOf(power.isStatus()));
                respMap.put(JsonConst.POWER_STATUS_UPDATE_TIME, deviceInfo.getPower().getUpdateTime().toString());
                response = JSONObject.toJSONString(respMap);
            } catch (TimeoutException e) {
                logger.error("Query power status failed.", e);
            }
        } else {
            logger.warn("Device {} not found!", mac);
        }
        return response;
    }

    public String uri() {
        return "/powerStatus";
    }
}
