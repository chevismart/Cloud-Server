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
public class PowerControlHandler extends HttpServerHandler implements HttpJsonHandler {

    private final PowerProxy powerProxy;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    DeviceInfo deviceInfo;
    Date lastUpdateTime;

    public PowerControlHandler(PowerProxy powerProxy) {
        this.powerProxy = powerProxy;
    }


    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {
        HttpResponseMessage response = new HttpResponseMessage();

        String mac = request.getParameter(ServerConstants.JsonConst.MAC);
        Map<String, DeviceInfo> deviceInfoMap = SessionUtil.getDeviceInfoByMacAddress(ByteArrayUtil.hexStringToByteArray(mac));
        if (null != deviceInfoMap && !deviceInfoMap.isEmpty()) {

            deviceInfo = deviceInfoMap.values().iterator().next();

            String switcher = request.getParameter(ServerConstants.JsonConst.POWER_SWITCHER);
            boolean isOn = MessageUtil.isTrue(switcher);

            Power power = deviceInfo.getPower();

            lastUpdateTime = (Date) power.getUpdateTime().clone();

            powerProxy.powerControl(deviceInfo, isOn);

            if (MessageUtil.waitForResponse(this, MessageUtil.TCP_MESSAGE_TIMEOUT_IN_SECOND)) {
                Map<String, String> respMap = new HashMap<String, String>();
                respMap.put(ServerConstants.JsonConst.POWER_STATUS, String.valueOf(power.isStatus()));
                respMap.put(ServerConstants.JsonConst.POWER_STATUS_UPDATE_TIME, deviceInfo.getPower().getUpdateTime().toString());

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
        return lastUpdateTime;
    }

    @Override
    public Date getUpdateTime() {
        return deviceInfo.getPower().getUpdateTime();
    }
}