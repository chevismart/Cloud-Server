package com.gamecenter.handler.http;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.alibaba.fastjson.JSONObject;
import com.gamecenter.constants.ServerConstants;
import com.gamecenter.handler.tcp.CounterProxy;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.model.TopUp;
import com.gamecenter.server.http.AbstractHttpRequestHandler;
import com.gamecenter.utils.SessionUtil;
import com.sun.net.httpserver.HttpExchange;

import java.util.HashMap;
import java.util.Map;

import static com.gamecenter.constants.ServerConstants.JsonConst.*;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class TopupHttpHandler extends AbstractHttpRequestHandler {

    private final CounterProxy counterProxy;

    public TopupHttpHandler(CounterProxy counterProxy) {
        this.counterProxy = counterProxy;
    }

    @Override
    public String process(HttpExchange httpExchange) {

        String mac = getParameter(httpExchange, MAC);
        DeviceInfo deviceInfo;
        Map<String, DeviceInfo> deviceInfoMap = SessionUtil.getDeviceInfoByMacAddress(ByteArrayUtil.hexStringToByteArray(mac));
        TopUp topup = null;
        if (null != deviceInfoMap && !deviceInfoMap.isEmpty()) {

            deviceInfo = deviceInfoMap.values().iterator().next();

            String refId = formatRefId(getParameter(httpExchange, TOP_UP_REFERENCE_ID));
            int coinQty = Integer.valueOf(getParameter(httpExchange, TOP_UP_COIN_QTY));
            if (!isPefIdProcessed(refId, deviceInfo)) {
                try {
                    if (!deviceInfo.getTopUpHistory().containsKey(refId)) {
                        // Raise a topup request by counter proxy and add one record in the topup history.
                        topup = (TopUp) counterProxy.topUpCoins(deviceInfo, coinQty, refId).getResult();
                    } else {
                        logger.warn("Reference [{}] topup request is requested and under processing, waiting for the response.", refId);
                        return EMPTY;
                    }

                } catch (Exception e) {
                    logger.error("Topup failed since: {}", e.getMessage());
                }
            } else {
                logger.warn("Reference Id {} has been processed, request aborted!", refId);
                return EMPTY;
            }

        } else {
            logger.warn("Device {} not found!", mac);
        }
        return JSONObject.toJSONString(getTopUpResultMap(topup));
    }

    @Override
    public String uri() {
        return "/topup";
    }

    private boolean isPefIdProcessed(String refId, DeviceInfo deviceInfo) {
        return deviceInfo.getTopUpHistory().containsKey(refId) && deviceInfo.getTopUpHistory().get(refId).isDeviceReplied();
    }

    private Map<String, String> getTopUpResultMap(TopUp topUp) {
        Map<String, String> respMap = new HashMap<>();
        if (topUp != null) {
            respMap.put(ServerConstants.JsonConst.TOP_UP_RESULT, String.valueOf(topUp.isTopUpResult()));
            respMap.put(ServerConstants.JsonConst.TOP_UP_REFERENCE_ID, topUp.getReferenceId());
            respMap.put(ServerConstants.JsonConst.TOP_UP_RESULT_TIMESTAMP, topUp.getUpdateTime().toString());
            logger.info("Response content is: {}", respMap);
        }
        return respMap;
    }

    private String formatRefId(String refId) {
        if (refId.length() != 10) {
            if (refId.length() > 10) return refId.substring(0, 9);
            else {
                for (int i = refId.length(); i <= 10; i++) {
                    refId += "*";
                }
            }
        }
        return refId;
    }

}
