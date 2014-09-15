package com.gamecenter.handler.http;

import com.gamecenter.handler.HttpJsonHandler;
import com.gamecenter.model.HttpRequestMessage;
import com.gamecenter.model.HttpResponseMessage;
import com.gamecenter.model.Initialization;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Created by Chevis on 14-9-10.
 */
public class ClientListHandler implements HttpJsonHandler
{

    private static Logger logger = LoggerFactory.getLogger(ClientListHandler.class);

    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {

        HttpResponseMessage response = new HttpResponseMessage();

        HashMap<String, IoSession> map = Initialization.getInstance().getClientMap();

        HashMap<String, String> clientMap = new HashMap<String, String>();

        logger.info("Session Map  = {}", map);
        if (null != map && !map.isEmpty()) {
            for (String ip : map.keySet()) {

            }
        }



        response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);

        return null;
    }
}
