package com.gamecenter.handler;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.gamecenter.model.Initialization;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.gamecenter.serializer.constants.MessageType;
import org.gamecenter.serializer.messages.downStream.LoginResponse;
import org.gamecenter.serializer.messages.upStream.LoginRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Boss on 2014/8/6.
 */
public class MinaTcpLongConnServerHandler implements IoHandler {

    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());


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


        LoginRequest request = new LoginRequest();
        request.parse(messageMap.get(MessageType.LoginRequest));
        String msg = ByteArrayUtil.toHexString(request.getCenterId());

        System.out.println("收到客户端发来的消息为" + "  " + msg);



        String expression = message.toString();

        Initialization init = Initialization.getInstance();

        HashMap<String, IoSession> clientMap = init.getClientMap();

        if (clientMap == null || clientMap.size() == 0) {

            session.write("error");

        } else {

            IoSession longConnSession = null;

            Iterator<String> iterator = clientMap.keySet().iterator();

            String key = "";

            while (iterator.hasNext()) {

                key = iterator.next();

                longConnSession = clientMap.get(key);

            }

            logger.info("LongConnect Server Session ID :" + String.valueOf(longConnSession.getId()));

            longConnSession.setAttribute("shortConnSession", session);

            LoginResponse response = new LoginResponse();
            response.setHeader(request.getHeader());
            logger.info("Message will be sent = {}",ByteArrayUtil.toHexString(response.build()));
            longConnSession.write(response.build());



        }



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
