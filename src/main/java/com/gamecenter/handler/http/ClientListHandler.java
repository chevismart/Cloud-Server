package com.gamecenter.handler.http;

import com.gamecenter.constants.ServerConstants;
import com.gamecenter.handler.HttpJsonHandler;
import com.gamecenter.model.HttpRequestMessage;
import com.gamecenter.model.HttpResponseMessage;
import com.gamecenter.utils.SessionUtil;
import net.sf.json.util.JSONUtils;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Chevis on 14-9-10.
 */
public class ClientListHandler implements HttpJsonHandler {

    private static Logger logger = LoggerFactory.getLogger(ClientListHandler.class);

    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {

        HttpResponseMessage response = new HttpResponseMessage();

        String centerId = request.getParameter(ServerConstants.JsonConst.CENTER_ID);

        Map<String, IoSession> deviceSessions = SessionUtil.getSessionByCenterId(centerId);

        List<String> devicesMacList = new ArrayList<String>();

        for (String sessionKey : deviceSessions.keySet()) {
            devicesMacList.add(SessionUtil.getMacFromSessionKey(sessionKey, centerId));
        }

        logger.info("The mac list of center id {} is {}", centerId, devicesMacList);

//        response.appendBody();
//
//        logger.info("Session Map  = {}", map);
//        if (null != map && !map.isEmpty()) {
//            for (String ip : map.keySet()) {
//
//            }
//        }


        response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);

        return null;
    }
}
