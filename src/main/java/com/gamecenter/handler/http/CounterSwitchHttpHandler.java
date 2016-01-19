package com.gamecenter.handler.http;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.alibaba.fastjson.JSONObject;
import com.gamecenter.constants.ServerConstants;
import com.gamecenter.handler.tcp.CounterProxy;
import com.gamecenter.model.Counter;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.server.http.AbstractHttpRequestHandler;
import com.gamecenter.utils.MessageUtil;
import com.gamecenter.utils.SessionUtil;
import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.gamecenter.constants.ServerConstants.JsonConst.COUNTER_SWITCH;
import static com.gamecenter.constants.ServerConstants.JsonConst.MAC;

public class CounterSwitchHttpHandler extends AbstractHttpRequestHandler {

    private final CounterProxy counterProxy;

    public CounterSwitchHttpHandler(CounterProxy counterProxy) {
        this.counterProxy = counterProxy;
    }

    public String process(HttpExchange httpExchange) {
        String response = StringUtils.EMPTY;
        String mac = getParameter(httpExchange, MAC);
        Map<String, DeviceInfo> deviceInfoMap = SessionUtil.getDeviceInfoByMacAddress(ByteArrayUtil.hexStringToByteArray(mac));
        if (null != deviceInfoMap && !deviceInfoMap.isEmpty()) {
            DeviceInfo deviceInfo = deviceInfoMap.values().iterator().next();

            String switcher = getParameter(httpExchange, COUNTER_SWITCH);
            try {
                Counter counter = (Counter) counterProxy.switchCounter(deviceInfo, MessageUtil.isTrue(switcher)).getResult();
                Map<String, String> respMap = new HashMap<String, String>();
                respMap.put(ServerConstants.JsonConst.COIN_STATUS, String.valueOf(counter.isCoinOn()));
                respMap.put(ServerConstants.JsonConst.PRIZE_STATUS, String.valueOf(counter.isPrizeOn()));
                respMap.put(ServerConstants.JsonConst.COUNTER_STATUS_TIMESTAMP, String.valueOf(counter.getLastStatusTime().getTime()));
//                respMap.put(ServerConstants.JsonConst.COIN_RESET_TIMESTAMP, String.valueOf(deviceInfo.getCounter().getLastPrizeResetTime().getTime()));
                response = JSONObject.toJSONString(respMap);
            } catch (TimeoutException e) {
                logger.error("Switch device({}) counter failed.", mac);
            }
        } else {
            logger.warn("Device {} not found!", mac);
        }
        return response;
    }

    public String uri() {
        return "/counterSwitch";
    }
}
