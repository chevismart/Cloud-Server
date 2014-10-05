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

    public CounterQtyHandler(CounterProxy counterProxy) {
        this.counterProxy = counterProxy;
    }

    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {

        HttpResponseMessage response = new HttpResponseMessage();

        String mac = request.getParameter(ServerConstants.JsonConst.MAC);
        Map<String, DeviceInfo> deviceInfoMap = SessionUtil.getDeviceInfoByMacAddress(ByteArrayUtil.hexStringToByteArray(mac));
        if (null != deviceInfoMap && !deviceInfoMap.isEmpty()) {

            DeviceInfo deviceInfo = deviceInfoMap.values().iterator().next();

            String queryCoin = request.getParameter(ServerConstants.JsonConst.COIN_QTY);
            String queryPrize = request.getParameter(ServerConstants.JsonConst.PRIZE_QTY);
            boolean isQueryCoin = MessageUtil.isTrue(queryCoin);
            boolean isQueryPrize = MessageUtil.isTrue(queryPrize);

            counterProxy.refreshCounterQty(isQueryCoin, isQueryPrize, deviceInfo);

            Date lastQuery = deviceInfo.getCounter().getLastQtyTime();

            logger.debug("Wait for counter quantity response at {}", new Date());

            while (!lastQuery.before(deviceInfo.getCounter().getLastQtyTime())) {
//
//                // TODO: To be fixed here and add timeout
//                deviceInfo.getCounter().setLastStatusTime(new Date());
//                deviceInfo.getCounter().setCoinOn(true);
            }

            logger.debug("Get counter quantity response at {}", deviceInfo.getCounter().getLastStatusTime());

            Map<String, String> respMap = new HashMap<String, String>();
            respMap.put(ServerConstants.JsonConst.COIN_QTY, String.valueOf(deviceInfo.getCounter().getCoinQty()));
            respMap.put(ServerConstants.JsonConst.PRIZE_QTY, String.valueOf(deviceInfo.getCounter().getPrizeQty()));
            respMap.put(ServerConstants.JsonConst.COUNTER_QTY_TIMESTAMP, deviceInfo.getCounter().getLastQtyTime().toString());

            response.appendBody(buildJsonResponse(request, JsonUtil.getJsonFromMap(respMap)));

        } else {
            logger.warn("Device {} not found!", mac);
            response = null;
        }

        return response;
    }
}
