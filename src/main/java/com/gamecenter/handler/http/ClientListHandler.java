package com.gamecenter.handler.http;

import com.gamecenter.constants.ServerConstants;
import com.gamecenter.handler.HttpJsonHandler;
import com.gamecenter.handler.HttpServerHandler;
import com.gamecenter.handler.tcp.DeviceListProxy;
import com.gamecenter.model.HttpRequestMessage;
import com.gamecenter.model.HttpResponseMessage;
import com.gamecenter.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chevis on 14-9-10.
 */
public class ClientListHandler extends HttpServerHandler implements HttpJsonHandler {

    private static Logger logger = LoggerFactory.getLogger(ClientListHandler.class);
    private final DeviceListProxy deviceListProxy;

    public ClientListHandler(DeviceListProxy deviceListProxy) {
        this.deviceListProxy = deviceListProxy;
    }


    @Override
    public HttpResponseMessage handle(HttpRequestMessage request) {

        HttpResponseMessage response = new HttpResponseMessage();

        String centerId = request.getParameter(ServerConstants.JsonConst.CENTER_ID);


        List<String> devicesMacList = deviceListProxy.getOninceDevicesByCenterId(centerId);

        Map<String, List<String>> jsonMap = new HashMap<String, List<String>>();
        jsonMap.put("mac", devicesMacList);


//        JsonFactory jsonFactory = new JsonFactory();
//        ObjectMapper objectMapper =  new ObjectMapper();
//        JsonGenerator jsonGenerator = objectMapper.getFactory();


//        JSONObject jsonObject = JSONObject.fromObject(jsonMap);
//
//        JSONArray jsonArray = JSONArray.fromObject(devicesMacList);
//
        response.appendBody(buildJsonResponse(request, JsonUtil.getJsonFromList(devicesMacList)));


//        Map<String, DeviceInfo> deviceSessions = SessionUtil.getDeviceInfoByCenterId(centerId);
//
//        List<String> devicesMacList = new ArrayList<String>();
//
//        for (String sessionKey : deviceSessions.keySet()) {
//            devicesMacList.add(SessionUtil.getMacFromSessionKey(sessionKey, centerId));
//        }
//
//        IoSession session = deviceSessions.get(0).getSession();
//
//        ReadFuture readFuture = session.read();
//
//        readFuture.addListener(new IoFutureListener<IoFuture>() {
//            @Override
//            public void operationComplete(IoFuture future) {
//
////                future.getSession().
////                future.removeListener(this);
//
//            }
//        });
//
//        if (readFuture.getException() != null) {
//            //异常处理
//        } else {
//            //接收到消息，进行业务处理
//        }
//
//        WriteFuture future = session.write(new byte[]{00});
//
//        future.addListener(new IoFutureListener<IoFuture>() {
//            @Override
//            public void operationComplete(IoFuture future) {
//
//            }
//        });

        logger.info("The mac list of center id {} is {}", centerId, devicesMacList);

//        response.appendBody();
//
//        logger.info("Session Map  = {}", map);
//        if (null != map && !map.isEmpty()) {
//            for (String ip : map.keySet()) {
//
//            }
//        }


        return response;
    }
}
