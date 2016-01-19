package com.gamecenter.handler.http;

import com.alibaba.fastjson.JSONObject;
import com.gamecenter.handler.queue.QueueEntry;
import com.gamecenter.handler.tcp.CounterProxy;
import com.gamecenter.model.Counter;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.model.Model;
import com.gamecenter.server.http.AbstractHttpRequestHandler;
import com.gamecenter.utils.SessionUtil;
import com.sun.net.httpserver.HttpExchange;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static ch.qos.logback.core.encoder.ByteArrayUtil.hexStringToByteArray;
import static com.gamecenter.constants.ServerConstants.JsonConst.*;
import static com.gamecenter.utils.MessageUtil.isTrue;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_OK;

public class CounterRecordHttpHandler extends AbstractHttpRequestHandler {
    private final CounterProxy counterProxy;

    public CounterRecordHttpHandler(CounterProxy counterProxy) {
        this.counterProxy = counterProxy;
    }

    public String uri() {
        return "/counterRecord";
    }

    public String process(HttpExchange httpExchange) {
        String response = EMPTY;
        String mac = getParameter(httpExchange, "MAC");
        if (mac != null && mac != "") {
            Map<String, DeviceInfo> deviceInfoMap = SessionUtil.getDeviceInfoByMacAddress(hexStringToByteArray(mac));
            logger.debug("Current device map: {}", deviceInfoMap);
            if (null != deviceInfoMap && !deviceInfoMap.isEmpty()) {
                logger.debug("Device found and requesting counter record from device.");
                DeviceInfo deviceInfo = deviceInfoMap.values().iterator().next();

                String queryCoin = getParameter(httpExchange, COIN_QTY);
                String queryPrize = getParameter(httpExchange, PRIZE_QTY);
                boolean isQueryCoin = isTrue(queryCoin);
                boolean isQueryPrize = isTrue(queryPrize);

                try {
                    QueueEntry<Model> queueEntry = counterProxy.refreshCounterQty(isQueryCoin, isQueryPrize, deviceInfo);
                    Counter counter = (Counter) queueEntry.getResult();
                    logger.info("Request counter record for {} successfully and elapse {} ms", mac, queueEntry.elapse());
                    Map<String, String> respMap = new HashMap<String, String>();
                    respMap.put(COIN_QTY, String.valueOf(counter.getCoinQty()));
                    respMap.put(PRIZE_QTY, String.valueOf(counter.getPrizeQty()));
                    respMap.put(COUNTER_QTY_TIMESTAMP, String.valueOf(counter.getLastQtyTime().getTime()));
//                respMap.put(COIN_RESET_TIMESTAMP, String.valueOf(deviceInfo.getCounter().getLastPrizeResetTime().getTime()));
//                respMap.put(PRIZE_RESET_TIMESTAMP, String.valueOf(deviceInfo.getCounter().getLastPrizeResetTime().getTime()));
                    response = JSONObject.toJSONString(respMap);
                } catch (TimeoutException e) {
                    logger.warn("Request counter record failed since timeout.", e);
                }
            } else {
                logger.warn("Device [mac:{}] not found!", mac);
            }
        } else {
            logger.warn("MAC address not found!");
        }
        logger.debug("Return the result back to http client: {}" ,response);
        return response;
    }
}
