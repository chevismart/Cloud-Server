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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chevis on 14-10-2.
 */
public class CounterStatusHandler extends HttpServerHandler implements HttpJsonHandler {

    private final CounterProxy counterProxy;

    public CounterStatusHandler(CounterProxy counterProxy) {
        this.counterProxy = counterProxy;
    }

    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {

        HttpResponseMessage response = new HttpResponseMessage();

        String mac = request.getParameter(ServerConstants.JsonConst.MAC);
        Map<String, DeviceInfo> deviceInfoMap = SessionUtil.getDeviceInfoByMacAddress(ByteArrayUtil.hexStringToByteArray(mac));
        if (null != deviceInfoMap && !deviceInfoMap.isEmpty()) {

            DeviceInfo deviceInfo = deviceInfoMap.values().iterator().next();

            String queryCoin = request.getParameter(ServerConstants.JsonConst.COIN_STATUS);
            String queryPrize = request.getParameter(ServerConstants.JsonConst.PRIZE_STATUS);
            boolean isQueryCoin = MessageUtil.isQuery(queryCoin);
            boolean isQueryPrize = MessageUtil.isQuery(queryPrize);

            Date lastQuery = (Date) deviceInfo.getCounter().getLastStatusTime().clone();

            counterProxy.refreshCounterQty(isQueryCoin, isQueryPrize, deviceInfo);

            while (!lastQuery.before(deviceInfo.getCounter().getLastStatusTime())) {

                // TODO: To be fixed here and add timeout
                deviceInfo.getCounter().setLastStatusTime(new Date());
                deviceInfo.getCounter().setCoinOn(true);
            }

            Map<String, String> respMap = new HashMap<String, String>();
            respMap.put(ServerConstants.JsonConst.COIN_STATUS, String.valueOf(deviceInfo.getCounter().isCoinOn()));
            respMap.put(ServerConstants.JsonConst.PRIZE_STATUS, String.valueOf(deviceInfo.getCounter().isPrizeOn()));
            respMap.put(ServerConstants.JsonConst.COUNTER_STATUS_TIMESTAMP, deviceInfo.getCounter().getLastStatusTime().toString());

            response.appendBody(buildJsonResponse(request, JsonUtil.getJsonFromMap(respMap)));

        }
        return response;
    }
}
