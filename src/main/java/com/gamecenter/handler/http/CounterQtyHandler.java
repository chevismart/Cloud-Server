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
 * Created by Chevis on 14-10-3.
 */
public class CounterQtyHandler extends HttpServerHandler implements HttpJsonHandler {

    private final CounterProxy counterProxy;
    Logger logger = LoggerFactory.getLogger(this.getClass());
    Date lastQuery;
    DeviceInfo deviceInfo;

    public CounterQtyHandler(CounterProxy counterProxy) {
        this.counterProxy = counterProxy;
    }

    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {

        HttpResponseMessage response = new HttpResponseMessage();

        String mac = request.getParameter(ServerConstants.JsonConst.MAC);
        Map<String, DeviceInfo> deviceInfoMap = SessionUtil.getDeviceInfoByMacAddress(ByteArrayUtil.hexStringToByteArray(mac));
        if (null != deviceInfoMap && !deviceInfoMap.isEmpty()) {

            deviceInfo = deviceInfoMap.values().iterator().next();

            String queryCoin = request.getParameter(ServerConstants.JsonConst.COIN_QTY);
            String queryPrize = request.getParameter(ServerConstants.JsonConst.PRIZE_QTY);
            boolean isQueryCoin = MessageUtil.isTrue(queryCoin);
            boolean isQueryPrize = MessageUtil.isTrue(queryPrize);

            counterProxy.refreshCounterQty(isQueryCoin, isQueryPrize, deviceInfo);

            lastQuery = deviceInfo.getCounter().getLastQtyTime();

            if (MessageUtil.waitForResponse(this, MessageUtil.TCP_MESSAGE_TIMEOUT_IN_SECOND)) {
                Map<String, String> respMap = new HashMap<String, String>();
                respMap.put(ServerConstants.JsonConst.COIN_QTY, String.valueOf(deviceInfo.getCounter().getCoinQty()));
                respMap.put(ServerConstants.JsonConst.PRIZE_QTY, String.valueOf(deviceInfo.getCounter().getPrizeQty()));
                respMap.put(ServerConstants.JsonConst.COUNTER_QTY_TIMESTAMP, String.valueOf(deviceInfo.getCounter().getLastQtyTime().getTime()));
//                respMap.put(ServerConstants.JsonConst.COIN_RESET_TIMESTAMP, String.valueOf(deviceInfo.getCounter().getLastPrizeResetTime().getTime()));
//                respMap.put(ServerConstants.JsonConst.PRIZE_RESET_TIMESTAMP, String.valueOf(deviceInfo.getCounter().getLastPrizeResetTime().getTime()));

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
        return MessageUtil.isKeeyWaiting(getRequestTime(), getUpdateTime());
    }

    @Override
    public Date getRequestTime() {
        return lastQuery;
    }

    @Override
    public Date getUpdateTime() {
        return deviceInfo.getCounter().getLastQtyTime();
    }
}
