package com.gamecenter.handler.http;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.alibaba.fastjson.JSONObject;
import com.gamecenter.handler.tcp.CounterProxy;
import com.gamecenter.model.Counter;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.server.http.AbstractHttpRequestHandler;
import com.gamecenter.utils.SessionUtil;
import com.sun.net.httpserver.HttpExchange;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.gamecenter.constants.ServerConstants.JsonConst.*;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class CounterStatusHttpHandler extends AbstractHttpRequestHandler {

    private final CounterProxy counterProxy;

    public CounterStatusHttpHandler(CounterProxy counterProxy) {
        this.counterProxy = counterProxy;
    }

    public String process(HttpExchange httpExchange) {
        String response = EMPTY;
        String mac = getParameter(httpExchange, MAC);
        Map<String, DeviceInfo> deviceInfoMap = SessionUtil.getDeviceInfoByMacAddress(ByteArrayUtil.hexStringToByteArray(mac));
        if (null != deviceInfoMap && !deviceInfoMap.isEmpty()) {

            DeviceInfo deviceInfo = deviceInfoMap.values().iterator().next();
            try {
                Counter counter = (Counter) counterProxy.refreshCounterStatus(deviceInfo).getResult();
                Map<String, String> respMap = new HashMap<String, String>();
                respMap.put(COIN_STATUS, String.valueOf(counter.isCoinOn()));
                respMap.put(PRIZE_STATUS, String.valueOf(counter.isPrizeOn()));
                respMap.put(COUNTER_STATUS_TIMESTAMP, String.valueOf(counter.getLastStatusTime().toString()));
//                respMap.put(ServerConstants.JsonConst.COIN_RESET_TIMESTAMP, String.valueOf(deviceInfo.getCounter().getLastPrizeResetTime().getTime()));
                response = JSONObject.toJSONString(respMap);
            } catch (TimeoutException e) {
                logger.error("Query device({}) counter status failed!", mac);
            }
        } else {
            logger.warn("Device {} not found!", mac);
        }
        return response;
    }

    @Override
    public String uri() {
        return "/counterStatus";
    }
}
