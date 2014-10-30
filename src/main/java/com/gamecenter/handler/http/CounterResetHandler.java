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

    DeviceInfo deviceInfo;
    boolean isResetCoin;
    boolean isResetPrize;
    Date lastCoinResetTime;
    Date lastPrizeResetTime;

    public CounterResetHandler(CounterProxy counterProxy) {
        this.counterProxy = counterProxy;
    }

    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {

        HttpResponseMessage response = new HttpResponseMessage();

        String mac = request.getParameter(ServerConstants.JsonConst.MAC);
        Map<String, DeviceInfo> deviceInfoMap = SessionUtil.getDeviceInfoByMacAddress(ByteArrayUtil.hexStringToByteArray(mac));
        if (null != deviceInfoMap && !deviceInfoMap.isEmpty()) {

            deviceInfo = deviceInfoMap.values().iterator().next();

            String resetCoin = request.getParameter(ServerConstants.JsonConst.COIN_RESET);
            String resetPrize = request.getParameter(ServerConstants.JsonConst.PRIZE_RESET);
            isResetCoin = MessageUtil.isTrue(resetCoin);
            isResetPrize = MessageUtil.isTrue(resetPrize);

            counterProxy.resetCounter(isResetCoin, isResetPrize, deviceInfo);

            lastCoinResetTime = (Date) deviceInfo.getCounter().getLastCoinResetTime().clone();
            lastPrizeResetTime = (Date) deviceInfo.getCounter().getLastPrizeResetTime().clone();

            if ((isResetCoin || isResetPrize) && MessageUtil.waitForResponse(this, MessageUtil.TCP_MESSAGE_TIMEOUT_IN_SECOND)) {

                Map<String, String> respMap = new HashMap<String, String>();
                respMap.put(ServerConstants.JsonConst.COIN_RESET, String.valueOf(isResetCoin ? true : StringUtils.EMPTY));
                respMap.put(ServerConstants.JsonConst.PRIZE_RESET, String.valueOf(isResetPrize ? true : StringUtils.EMPTY));
                respMap.put(ServerConstants.JsonConst.COIN_RESET_TIMESTAMP, String.valueOf(deviceInfo.getCounter().getLastCoinResetTime().getTime()));
                respMap.put(ServerConstants.JsonConst.PRIZE_RESET_TIMESTAMP, String.valueOf(deviceInfo.getCounter().getLastPrizeResetTime().getTime()));

                response.appendBody(buildJsonResponse(request, JsonUtil.getJsonFromMap(respMap)));
            }
        } else if (!isResetPrize && !isResetCoin) {
            logger.error("Invalid request for reset none of coin nor prize!");
        } else {
            logger.warn("Device {} not found!", mac);
            response = null;
        }

        return response;
    }

    @Override
    public boolean await() {
        return MessageUtil.isKeeyWaiting(getRequestTime(), getUpdateTime());
    }

    @Override
    public Date getRequestTime() {

        return (this.isResetCoin && this.isResetPrize) ? lastCoinResetTime :
                (isResetCoin ? lastCoinResetTime :
                        (isResetPrize ? lastPrizeResetTime : null));
    }

    @Override
    public Date getUpdateTime() {
        return (this.isResetCoin && this.isResetPrize) ? deviceInfo.getCounter().getLastCoinResetTime() :
                (isResetCoin ? deviceInfo.getCounter().getLastCoinResetTime() :
                        (isResetPrize ? deviceInfo.getCounter().getLastPrizeResetTime() : null));
    }
}
