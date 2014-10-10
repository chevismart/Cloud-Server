package com.gamecenter.handler;

import com.gamecenter.constants.ServerConstants;
import com.gamecenter.constants.ServerEnum;
import com.gamecenter.handler.http.*;
import com.gamecenter.handler.tcp.CounterProxy;
import com.gamecenter.handler.tcp.DeviceListProxy;
import com.gamecenter.handler.tcp.PowerProxy;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.model.HttpRequestMessage;
import com.gamecenter.model.HttpResponseMessage;
import com.gamecenter.model.Initialization;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class HttpServerHandler extends IoHandlerAdapter {

    public static final String JSONP_CALLBACK = "jsonpCallback";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private HttpJsonHandler handler;
    private Map<String, String> tokenMap;
    private DeviceListProxy deviceListProxy;
    private CounterProxy counterProxy;
    private PowerProxy powerProxy;

    public HttpServerHandler() {
        tokenMap = new HashMap<String, String>();
        tokenMap.put("00000000", "tokenStr");
        deviceListProxy = new DeviceListProxy();
        counterProxy = new CounterProxy();
        powerProxy = new PowerProxy();

    }

    public HttpJsonHandler getHandler() {
        return handler;
    }

    public void setHandler(HttpJsonHandler handler) {
        this.handler = handler;
    }

    @Override
    public void sessionOpened(IoSession session) {
        // set idle time to 60 seconds
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60);
    }

    @Override
    public void messageReceived(IoSession session, Object message) {

        try {
            // Check that we can service the request context
            HttpRequestMessage request = (HttpRequestMessage) message;
            String centerId = request.getParameter(ServerConstants.JsonConst.CENTER_ID);
            String token = request.getParameter(ServerConstants.JsonConst.TOKEN);
            ServerEnum.Json.DataType dataType = ServerEnum.Json.DataType.valueOf(request.getParameter(ServerConstants.JsonConst.DATA_TYPE));
            ServerEnum.Json.RequestType requestType = ServerEnum.Json.RequestType.valueOf(request.getParameter(ServerConstants.JsonConst.REQUEST_TYPE));


            logger.info("centerId = {}", centerId);
            logger.info("token = {}", token);
            logger.info("dataType = {}", dataType);
            logger.info("requestType = {}", requestType);

            HttpResponseMessage response = new HttpResponseMessage();

            if (null != this.tokenMap && token.equals(tokenMap.get(centerId))) {
                HashMap<String, DeviceInfo> map = Initialization.getInstance().getClientMap();
                logger.info("Session Map  = {}", map);
                if (null != map && !map.isEmpty()) {
                    for (String ip : map.keySet()) {
                        System.err.println(ip);
                    }
                }

                switch (requestType) {
                    case CLIENT_LIST:
                        handler = new ClientListHandler(deviceListProxy);
                        break;
                    case COUNTER_STATUS:
                        handler = new CounterStatusHandler(counterProxy);
                        break;
                    case COUNTER_QTY:
                        handler = new CounterQtyHandler(counterProxy);
                        break;
                    case COUNTER_RESET:
                        handler = new CounterResetHandler(counterProxy);
                        break;
                    case COUNTER_SWITCH:
                        handler = new CounterSwitchHandler(counterProxy);
                        break;
                    case TOP_UP:
                        handler = new TopUpHandler(counterProxy);
                        break;
                    case POWER_STATUS:
                        handler = new PowerStatusHandler(powerProxy);
                        break;
                    case POWER_CONTROL:
                        handler = new PowerControlHandler(powerProxy);
                        break;
                }


                response.setContentType("application/json");

                if (handler != null) {

                    response = handler.handle(request);

                    response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
                } else {
                    logger.error("There is no handler for http request of {}", requestType);
                    response.setResponseCode(HttpResponseMessage.HTTP_STATUS_NOT_FOUND);
                }


//        msg.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
//        byte[] b = new byte[ta.buffer.limit()];
//        ta.buffer.rewind().get(b);
//        msg.appendBody(b);
//        System.out.println("####################");
//        System.out.println("  GET_TILE RESPONSE SENT - ATTACHMENT GOOD DIAMOND.SI="+d.si+
//        ", "+new
//        java.text.SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss.SSS").format(new
//        java.util.Date()));
//        System.out.println("#################### - status="+ta.state+", index="+message.getIndex());

                // Unknown request
//        response = new HttpResponseMessage();
//        response.setResponseCode(HttpResponseMessage.HTTP_STATUS_NOT_FOUND);
//        response.appendBody(String.format(
//        "<html><body><h1>UNKNOWN REQUEST %d</h1></body></html>",
//        HttpResponseMessage.HTTP_STATUS_NOT_FOUND));

                System.err.println(response);


            } else {
                logger.warn("Invalid token({}) for centerId {}", token, centerId);
            }


            if (response != null) {
                session.write(response).addListener(IoFutureListener.CLOSE);
            }

        } catch (Exception ex) {
            logger.error("Invalid http request, connection abort!");
            session.close(true);
        }
    }

    protected String buildJsonResponse(HttpRequestMessage request, String jsonStr) {
        return request.getParameter(JSONP_CALLBACK) + "(" + jsonStr + ")";
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        System.out.println("已回应给Client");
        if (session != null) {
            session.close(true);
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
        System.out.println("请求进入闲置状态....回路即将关闭....");
        session.close(true);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        System.out.println("请求处理遇到异常....回路即将关闭....");
        cause.printStackTrace();
        session.close(true);
    }
}