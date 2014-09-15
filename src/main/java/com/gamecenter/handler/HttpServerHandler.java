package com.gamecenter.handler;

import com.gamecenter.constants.ServerConstants;
import com.gamecenter.constants.ServerEnum;
import com.gamecenter.model.HttpRequestMessage;
import com.gamecenter.model.HttpResponseMessage;
import com.gamecenter.model.Initialization;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
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

    public HttpJsonHandler getHandler() {
        return handler;
    }

    public void setHandler(HttpJsonHandler handler) {
        this.handler = handler;
    }

    private Map<String, String> tokenMap;

    public HttpServerHandler() {
        tokenMap = new HashMap<String, String>();
        tokenMap.put("centerId", "tokenStr");
    }

    @Override
    public void sessionOpened(IoSession session) {
        // set idle time to 60 seconds
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60);
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
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

        if(null != this.tokenMap && token.equals(tokenMap.get(centerId))){
            HashMap<String, IoSession> map = Initialization.getInstance().getClientMap();
            logger.info("Session Map  = {}", map);
            if (null != map && !map.isEmpty()) {
                for (String ip : map.keySet()) {
                    System.err.println(ip);
                }
            }


//        HttpResponseMessage response = handler.handle(request);
//        response.appendBody("chevis");
            response.setContentType("application/json");
//        response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);


            Map<String, Object> jsonMap = new HashMap<String, Object>();
            jsonMap.put("ip", Initialization.getInstance().getClientMap().keySet());


//        JSONArray jsonObject = JSONArray.fromObject(Initialization.getInstance().getClientMap().keySet());
            JSONObject jsonObject = JSONObject.fromObject(jsonMap);
//        response.appendBody(buildJsonResponse(request, "{\"name\":\"abc\"})"));
            response.appendBody(buildJsonResponse(request, jsonObject.toString()));
            System.err.println(buildJsonResponse(request, jsonObject.toString(1, 1)));


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


        }else{
            response.setResponseCode(HttpResponseMessage.HTTP_STATUS_NOT_FOUND);
        }


        if (response != null) {
            session.write(response).addListener(IoFutureListener.CLOSE);
        }
    }

    private String buildJsonResponse(HttpRequestMessage request, String jsonStr) {
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