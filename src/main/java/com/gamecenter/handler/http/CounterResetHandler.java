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
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chevis on 14-10-3.
 */
public class CounterResetHandler extends HttpServerHandler implements HttpJsonHandler {

    private final CounterProxy counterProxy;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public CounterResetHandler(CounterProxy counterProxy) {
        this.counterProxy = counterProxy;
    }

    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {

        HttpResponseMessage response = new HttpResponseMessage();


        String mac = request.getParameter(ServerConstants.JsonConst.MAC);
        Map<String, DeviceInfo> deviceInfoMap = SessionUtil.getDeviceInfoByMacAddress(ByteArrayUtil.hexStringToByteArray(mac));
        if (null != deviceInfoMap && !deviceInfoMap.isEmpty()) {

            DeviceInfo deviceInfo = deviceInfoMap.values().iterator().next();

            String resetCoin = request.getParameter(ServerConstants.JsonConst.COIN_RESET);
            String resetPrize = request.getParameter(ServerConstants.JsonConst.PRIZE_RESET);
            boolean isResetCoin = MessageUtil.isTrue(resetCoin);
            boolean isResetPrize = MessageUtil.isTrue(resetPrize);

            counterProxy.resetCounter(isResetCoin, isResetPrize, deviceInfo);

            Date lastCoinResetTime = (Date) deviceInfo.getCounter().getLastCoinResetTime().clone();
            Date lastPrizeResetTime = (Date) deviceInfo.getCounter().getLastPrizeResetTime().clone();

            while ((isResetCoin ? !lastCoinResetTime.before(deviceInfo.getCounter().getLastCoinResetTime()) : true)
                    && (isResetPrize ? !lastPrizeResetTime.before(deviceInfo.getCounter().getLastPrizeResetTime()) : true)) {
                //TODO: handle timeout here
            }

            Map<String, String> respMap = new HashMap<String, String>();
            respMap.put(ServerConstants.JsonConst.COIN_RESET, String.valueOf(isResetCoin ? true : StringUtils.EMPTY));
            respMap.put(ServerConstants.JsonConst.PRIZE_RESET, String.valueOf(isResetPrize ? true : StringUtils.EMPTY));
            respMap.put(ServerConstants.JsonConst.COIN_RESET_TIMESTAMP, deviceInfo.getCounter().getLastCoinResetTime().toString());
            respMap.put(ServerConstants.JsonConst.PRIZE_RESET_TIMESTAMP, deviceInfo.getCounter().getLastPrizeResetTime().toString());

            response.appendBody(buildJsonResponse(request, JsonUtil.getJsonFromMap(respMap)));

        } else {
            logger.warn("Device {} not found!", mac);
            response = null;
        }

        return response;
    }
}
