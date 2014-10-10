package com.gamecenter.handler.http;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.gamecenter.constants.ServerConstants;
import com.gamecenter.handler.HttpJsonHandler;
import com.gamecenter.handler.HttpServerHandler;
import com.gamecenter.handler.tcp.PowerProxy;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.model.HttpRequestMessage;
import com.gamecenter.model.HttpResponseMessage;
import com.gamecenter.model.Power;
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
public class PowerStatusHandler extends HttpServerHandler implements HttpJsonHandler {

    private final PowerProxy powerProxy;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public PowerStatusHandler(PowerProxy powerProxy) {
        this.powerProxy = powerProxy;
    }


    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {

        HttpResponseMessage response = null;

        String mac = request.getParameter(ServerConstants.JsonConst.MAC);
        Map<String, DeviceInfo> deviceInfoMap = SessionUtil.getDeviceInfoByMacAddress(ByteArrayUtil.hexStringToByteArray(mac));
        if (null != deviceInfoMap && !deviceInfoMap.isEmpty()) {

            DeviceInfo deviceInfo = deviceInfoMap.values().iterator().next();

            Power power = deviceInfo.getPower();

            Date lastUpdateTime = (Date) power.getUpdateTime().clone();

            powerProxy.queryPowerStatus(deviceInfo);

            if (MessageUtil.waitForResponse(lastUpdateTime, deviceInfo.getPower().getUpdateTime(), MessageUtil.TCP_MESSAGE_TIMEOUT_IN_SECOND)) {
                response = new HttpResponseMessage();
                Map<String, String> respMap = new HashMap<String, String>();
                respMap.put(ServerConstants.JsonConst.POWER_STATUS, String.valueOf(power.isStatus()));
                respMap.put(ServerConstants.JsonConst.POWER_STATUS_UPDATE_TIME, deviceInfo.getPower().getUpdateTime().toString());

                response.appendBody(buildJsonResponse(request, JsonUtil.getJsonFromMap(respMap)));
            }

        } else {
            logger.warn("Device {} not found!", mac);
        }

        return response;
    }
}
