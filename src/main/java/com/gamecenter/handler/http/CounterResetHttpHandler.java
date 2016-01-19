package com.gamecenter.handler.http;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.alibaba.fastjson.JSONObject;
import com.gamecenter.handler.tcp.CounterProxy;
import com.gamecenter.model.Counter;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.server.http.AbstractHttpRequestHandler;
import com.gamecenter.utils.MessageUtil;
import com.gamecenter.utils.SessionUtil;
import com.sun.net.httpserver.HttpExchange;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.gamecenter.constants.ServerConstants.JsonConst.*;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class CounterResetHttpHandler extends AbstractHttpRequestHandler {
    private final CounterProxy counterProxy;

    public CounterResetHttpHandler(CounterProxy counterProxy) {
        this.counterProxy = counterProxy;
    }

    public String uri() {
        return "/counterReset";
    }

    @Override
    public String process(HttpExchange httpExchange) {

        String response = EMPTY;
        String mac = getParameter(httpExchange, MAC);
        Map<String, DeviceInfo> deviceInfoMap = SessionUtil.getDeviceInfoByMacAddress(ByteArrayUtil.hexStringToByteArray(mac));
        if (null != deviceInfoMap && !deviceInfoMap.isEmpty()) {
            DeviceInfo deviceInfo = deviceInfoMap.values().iterator().next();
            String resetCoin = getParameter(httpExchange, COIN_RESET);
            String resetPrize = getParameter(httpExchange, PRIZE_RESET);
            boolean isResetCoin = MessageUtil.isTrue(resetCoin);
            boolean isResetPrize = MessageUtil.isTrue(resetPrize);
            Map<String, String> respMap = new HashMap<>();

            Counter counter;
            try {
                counter = (Counter) counterProxy.resetCounter(isResetCoin, isResetPrize, deviceInfo).getResult();

                respMap.put(COIN_RESET, String.valueOf(isResetCoin ? true : EMPTY));
                respMap.put(PRIZE_RESET, String.valueOf(isResetPrize ? true : EMPTY));
                respMap.put(COIN_RESET_TIMESTAMP, String.valueOf(((Date) counter.getLastCoinResetTime().clone()).getTime()));
                respMap.put(PRIZE_RESET_TIMESTAMP, String.valueOf(((Date) counter.getLastPrizeResetTime().clone()).getTime()));
                response = JSONObject.toJSONString(respMap);
            } catch (TimeoutException e) {
                logger.error("Reset counter failed for device({})", mac);
            }

        } else {
            logger.warn("Device {} not found!", mac);
        }
        return response;
    }
}
