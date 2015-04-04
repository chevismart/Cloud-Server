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
import com.gamecenter.utils.MessageUtil;
import com.gamecenter.utils.SessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.gamecenter.utils.MessageUtil.waitForResponse;

/**
 * Created by Chevis on 14-10-3.
 */
public class TopUpHandler extends HttpServerHandler implements HttpJsonHandler {

    private final CounterProxy counterProxy;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    DeviceInfo deviceInfo;
    Date requestTime;
    TopUp topUp;

    public TopUpHandler(CounterProxy counterProxy) {
        this.counterProxy = counterProxy;
    }

    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {

        HttpResponseMessage response = new HttpResponseMessage();
        Map<String, String> respMap = new HashMap<String, String>();

        String mac = request.getParameter(ServerConstants.JsonConst.MAC);
        Map<String, DeviceInfo> deviceInfoMap = SessionUtil.getDeviceInfoByMacAddress(ByteArrayUtil.hexStringToByteArray(mac));
        if (null != deviceInfoMap && !deviceInfoMap.isEmpty()) {

            deviceInfo = deviceInfoMap.values().iterator().next();

            String refId = request.getParameter(ServerConstants.JsonConst.TOP_UP_REFERENCE_ID);
            int coinQty = Integer.valueOf(request.getParameter(ServerConstants.JsonConst.TOP_UP_COIN_QTY));
            try {
                if (!isRefIdProcessing(refId)) {
                    requestTime = new Date();
                    logger.info("Top up time is {}", requestTime);
                    // Raise a topup request by counter proxy and add one record in the topup history.
                    counterProxy.topUpCoins(deviceInfo, coinQty, refId, requestTime);
                } else {
                    logger.warn("Reference [{}] topup request is requested and under processing, waiting for the response.");
                }
                topUp = deviceInfo.getTopUpHistory().get(refId);
                waitForResponse(this, MessageUtil.TCP_MESSAGE_TIMEOUT_IN_SECOND);
                respMap = getTopUpResultMap(topUp.isTopUpResult());
                removeRepliedRecord();
            } catch (Exception e) {
                logger.error("Topup failed since: {}", e.getMessage());
            }


        } else {
            logger.warn("Device {} not found!", mac);
            respMap = getTopUpResultMap(false);
        }
        response.appendBody(buildJsonResponse(request, JsonUtil.getJsonFromMap(respMap)));

        return response;
    }

    private boolean isRefIdProcessing(String refId) {
        return deviceInfo.getTopUpHistory().containsKey(refId);
    }

    private Map<String, String> getTopUpResultMap(boolean topUpResult) {
        Map<String, String> respMap = new HashMap<String, String>();
        respMap.put(ServerConstants.JsonConst.TOP_UP_RESULT, String.valueOf(topUpResult));
        respMap.put(ServerConstants.JsonConst.TOP_UP_REFERENCE_ID, topUp.getReferenceId());
        respMap.put(ServerConstants.JsonConst.TOP_UP_RESULT_TIMESTAMP, topUp.getUpdateTime().toString());
        logger.info("Response content is: {}", respMap);
        return respMap;
    }

    @Override
    public boolean await() {
        return MessageUtil.isKeepWaiting(getRequestTime(), getUpdateTime());
    }

    @Override
    public Date getRequestTime() {
        return requestTime;
    }

    @Override
    public Date getUpdateTime() {
        return topUp.getUpdateTime();
    }

    public void removeRepliedRecord() throws Exception {
        String referenceId = topUp.getReferenceId();
        if (topUp.isDeviceReplied() && deviceInfo.getTopUpHistory().containsKey(referenceId)) {
            deviceInfo.getTopUpHistory().remove(referenceId);
            logger.info("Remove topup history {} success!",
                    deviceInfo.getTopUpHistory().containsKey(referenceId) ? "is NOT" : "is");
        } else {
            logger.warn("Remove topup request record with reference Id [{}] failed and may since not yet replied or such topup history is not found!");
        }
    }
}
