package com.gamecenter.handler;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.gamecenter.handler.tcp.HeartbeatHandler;
import com.gamecenter.handler.tcp.LoginHandler;
import com.gamecenter.model.Initialization;
import com.gamecenter.utils.SessionUtil;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.gamecenter.serializer.constants.MessageType;
import org.gamecenter.serializer.messages.Message;
import org.gamecenter.serializer.messages.MessageHeader;
import org.gamecenter.serializer.messages.downStream.PowerControlRequest;
import org.gamecenter.serializer.messages.upStream.LoginRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Boss on 2014/8/6.
 */
public class MinaTcpLongConnServerHandler implements IoHandler {

    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());

    List<String> authorizedSession;
    LoginHandler loginHandler;
    HeartbeatHandler heartbeatHandler;

    public MinaTcpLongConnServerHandler() {
        this.authorizedSession = new ArrayList<String>();
        loginHandler = new LoginHandler();
        heartbeatHandler = new HeartbeatHandler();
    }

    @Override
    public void sessionCreated(IoSession ioSession) throws Exception {
        logger.info("Session is created");
    }

    @Override

    public void sessionOpened(IoSession session) {

        InetSocketAddress remoteAddress = (InetSocketAddress) session.getRemoteAddress();

        String clientIp = remoteAddress.getAddress().getHostAddress();

        logger.info("LongConnect Server opened Session ID =" + String.valueOf(session.getId()));

        logger.info("接收来自客户端 :" + clientIp + "的连接.");

        Initialization init = Initialization.getInstance();


        HashMap<String, IoSession> clientMap = init.getClientMap();

        clientMap.put(clientIp, session);

        logger.info(clientMap.toString());
    }

    @Override
    public void sessionClosed(IoSession ioSession) throws Exception {
        logger.info("Session is closed.");
    }


    @Override
    public void messageReceived(IoSession session, Object message) throws IllegalAccessException, NoSuchFieldException, IOException {

        logger.info("Message received in the long connect server...");


        //////////////////////////

        Map<MessageType, byte[]> messageMap = (Map<MessageType, byte[]>) message;

        TcpHandler handler = null;

        for (MessageType msgType : messageMap.keySet()) {

            String sessionId = String.valueOf(session.getId());
            logger.debug("Received message from session id: {}", sessionId);

            if ((!authorizedSession.contains(sessionId) && msgType.equals(MessageType.LoginRequest))) {
                if (verifyUser(messageMap.get(msgType), session)) {
                    authorizedSession.add(sessionId);
                    logger.info("Authorize devices {}", authorizedSession);
//                    messageReceived(session, message);
                    session.write(loginHandler.handle(messageMap.get(msgType)));
                    }
            } else if(authorizedSession.contains(sessionId)){

                switch (msgType) {
                    case LoginRequest:
                        handler = loginHandler;
                        break;
                    case HeartbeatRequest:
                        handler = heartbeatHandler;
                        break;
                }
                if (null != handler) {
                    byte[] msg = handler.handle(messageMap.get(msgType));
                    logger.info("Message will be sent = {}", msg);
                    session.write(msg);

                    try {
                        Thread.sleep(5000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    PowerControlRequest pwdReq = new PowerControlRequest();

                    MessageHeader header = new MessageHeader();
                    header.setMessageId(new byte[]{00,00,00,00});
                    header.setMsgType(MessageType.PowerControlRequest);
                    header.setMessageSN(new byte[]{00,00,00,00});
                    header.setDeviceId(new byte[]{00,00,00,01});
                    pwdReq.setHeader(header);

                    pwdReq.setSwitcher("I");

//                    session.write(pwdReq.build());


                } else {
                    logger.error("There is no handler for message type {}", msgType);
                }
            }else{
                logger.info("Not yet auth request!");
            }
        }


//        String expression = message.toString();
//
//
//        if (clientMap == null || clientMap.size() == 0) {
//
//            session.write("error");
//
//        } else {
//
//            IoSession longConnSession = null;
//
//            Iterator<String> iterator = clientMap.keySet().iterator();
//
//            String key = "";
//
//            while (iterator.hasNext()) {
//
//                key = iterator.next();
//
//                longConnSession = clientMap.get(key);
//
//            }
//
//            logger.info("LongConnect Server Session ID :" + String.valueOf(longConnSession.getId()));
//
//            longConnSession.setAttribute("shortConnSession", session);
//
//            LoginResponse response = new LoginResponse();
//            response.setHeader(request.getHeader());


    }

    private boolean verifyUser(byte[] message, IoSession session) throws IllegalAccessException, NoSuchFieldException, IOException {
        boolean isAuthroized = false;
        LoginRequest request = new LoginRequest();

        request.parse(message);

        if (null != request.getMac() && null != request.getMac() && isValid()) {

            logger.info("Login device: {}", ByteArrayUtil.toHexString(request.getMac()));

            isAuthroized = true;
            String sessionKey = SessionUtil.createSessionKey(request.getCenterId(), request.getMac());
            Initialization init = Initialization.getInstance();
            HashMap<String, IoSession> clientMap = init.getClientMap();
            clientMap.put(sessionKey, session);
        }

        return isAuthroized;
    }

    private boolean isValid() {

        //TODO: verify user here!
        return true;
    }

    @Override
    public void messageSent(IoSession ioSession, Object o) throws Exception {
        logger.info("Message is Sent.");
    }


    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {

        logger.info("Disconnecting the idle.");

        // disconnect an idle client

        session.close(true);

    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {

        // close the connection on exceptional situation

        logger.warn(cause.getMessage(), cause);

        session.close(true);

    }
}
