package com.gamecenter.handler.http;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.gamecenter.constants.ServerConstants;
import com.gamecenter.handler.HttpJsonHandler;
import com.gamecenter.handler.HttpServerHandler;
import com.gamecenter.handler.tcp.CounterProxy;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.model.HttpRequestMessage;
import com.gamecenter.model.HttpResponseMessage;
import com.gamecenter.model.TopUp;
import com.gamecenter.utils.JsonUtil;
import com.gamecenter.utils.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chevis on 14-10-3.
 */
public class TopUpHandler extends HttpServerHandler implements HttpJsonHandler {

    private final CounterProxy counterProxy;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public TopUpHandler(CounterProxy counterProxy) {
        this.counterProxy = counterProxy;
    }

    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {

        HttpResponseMessage response = new HttpResponseMessage();

        String mac = request.getParameter(ServerConstants.JsonConst.MAC);
        Map<String, DeviceInfo> deviceInfoMap = SessionUtil.getDeviceInfoByMacAddress(ByteArrayUtil.hexStringToByteArray(mac));
        if (null != deviceInfoMap && !deviceInfoMap.isEmpty()) {

            DeviceInfo deviceInfo = deviceInfoMap.values().iterator().next();

            String refId = request.getParameter(ServerConstants.JsonConst.TOP_UP_REFERENCE_ID);
            int coinQty = Integer.valueOf(request.getParameter(ServerConstants.JsonConst.TOP_UP_COIN_QTY));

            Date now = new Date();

            counterProxy.topUpCoins(deviceInfo, coinQty, refId, now);

            logger.info("Top up time is {}", now);

            TopUp topUp = deviceInfo.getTopUpHistory().get(refId);

            boolean topUpResult = false;

            if (null != topUp) {

                while (!now.before(topUp.getUpdateTime())) {
                    //TODO: handle timeout here
                }

                topUpResult = topUp.isTopUpResult();

            }

            Map<String, String> respMap = new HashMap<String, String>();
            respMap.put(ServerConstants.JsonConst.TOP_UP_RESULT, String.valueOf(topUpResult));
            respMap.put(ServerConstants.JsonConst.TOP_UP_REFERENCE_ID, topUp.getReferenceId());
            respMap.put(ServerConstants.JsonConst.TOP_UP_RESULT_TIMESTAMP, topUp.getUpdateTime().toString());

            response.appendBody(buildJsonResponse(request, JsonUtil.getJsonFromMap(respMap)));

        } else {
            logger.warn("Device {} not found!", mac);
            response = null;
        }

        return response;
    }
}
