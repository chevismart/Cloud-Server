package com.gamecenter.handler.http;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.gamecenter.constants.ServerConstants;
import com.gamecenter.handler.HttpJsonHandler;
import com.gamecenter.handler.HttpServerHandler;
import com.gamecenter.handler.tcp.CounterProxy;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.model.HttpRequestMessage;
import com.gamecenter.model.HttpResponseMessage;
import com.gamecenter.utils.JsonUtil;
import com.gamecenter.utils.MessageUtil;
import com.gamecenter.utils.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chevis on 2014/10/5.
 */
public class CounterSwitchHandler extends HttpServerHandler implements HttpJsonHandler {

    private final CounterProxy counterProxy;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    Date lastQuery;
    DeviceInfo deviceInfo;


    public CounterSwitchHandler(CounterProxy counterProxy) {
        this.counterProxy = counterProxy;
    }

    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {
        HttpResponseMessage response = new HttpResponseMessage();

        String mac = request.getParameter(ServerConstants.JsonConst.MAC);
        Map<String, DeviceInfo> deviceInfoMap = SessionUtil.getDeviceInfoByMacAddress(ByteArrayUtil.hexStringToByteArray(mac));
        if (null != deviceInfoMap && !deviceInfoMap.isEmpty()) {
            deviceInfo = deviceInfoMap.values().iterator().next();

            String switcher = request.getParameter(ServerConstants.JsonConst.COUNTER_SWITCH);

            lastQuery = (Date) deviceInfo.getCounter().getLastStatusTime().clone();

            counterProxy.switchCounter(deviceInfo, MessageUtil.isTrue(switcher));

            logger.debug("Wait for counter status response at {}", new Date());

            if (MessageUtil.waitForResponse(this, MessageUtil.TCP_MESSAGE_TIMEOUT_IN_SECOND)) {

                Map<String, String> respMap = new HashMap<String, String>();
                respMap.put(ServerConstants.JsonConst.COIN_STATUS, String.valueOf(deviceInfo.getCounter().isCoinOn()));
                respMap.put(ServerConstants.JsonConst.PRIZE_STATUS, String.valueOf(deviceInfo.getCounter().isPrizeOn()));
                respMap.put(ServerConstants.JsonConst.COUNTER_STATUS_TIMESTAMP, String.valueOf(deviceInfo.getCounter().getLastStatusTime().getTime()));
//                respMap.put(ServerConstants.JsonConst.COIN_RESET_TIMESTAMP, String.valueOf(deviceInfo.getCounter().getLastPrizeResetTime().getTime()));

                response.appendBody(buildJsonResponse(request, JsonUtil.getJsonFromMap(respMap)));
            }
        } else {
            logger.warn("Device {} not found!", mac);
            response = null;
        }
        return response;
    }

    @Override
    public boolean await() {
        return MessageUtil.isKeeyWaiting(lastQuery, deviceInfo.getCounter().getLastStatusTime());
    }

    @Override
    public Date getRequestTime() {
        return lastQuery;
    }

    @Override
    public Date getUpdateTime() {
        return deviceInfo.getCounter().getLastStatusTime();
    }
}

