package com.gamecenter.handler;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.gamecenter.handler.queue.Queues;
import com.gamecenter.handler.tcp.*;
import com.gamecenter.model.DeviceInfo;
import com.gamecenter.model.Initialization;
import com.gamecenter.utils.SessionUtil;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.gamecenter.serializer.constants.MessageType;
import org.gamecenter.serializer.messages.MessageHeader;
import org.gamecenter.serializer.messages.downStream.*;
import org.gamecenter.serializer.messages.upStream.LoginRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gamecenter.model.Initialization.getInstance;

public class MinaTcpLongConnServerHandler implements IoHandler {

    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    List<String> authorizedSession;
    LoginHandler loginHandler;
    HeartbeatHandler heartbeatHandler;
    PowerControlHandler powerControlHandler;
    TopUpHandler topUpHandler;
    CounterHandler counterHandler;
    CounterResetHandler counterResetHandler;
    RuntimeHandler runtimeHandler;
    CounterStatusHandler counterStatusHandler;
    //temp
    private String switcher = "I";
    private int counterIndex = 0;
    private final Queues queues;

    public MinaTcpLongConnServerHandler(Queues queues) {
        this.queues = queues;
        this.authorizedSession = new ArrayList<String>();
        loginHandler = new LoginHandler();
        heartbeatHandler = new HeartbeatHandler();
        powerControlHandler = new PowerControlHandler(queues);
        topUpHandler = new TopUpHandler(this.queues);
        counterHandler = new CounterHandler(this.queues);
        counterResetHandler = new CounterResetHandler(queues);
        runtimeHandler = new RuntimeHandler();
        counterStatusHandler = new CounterStatusHandler(queues);
    }

    @Override
    public void sessionCreated(IoSession ioSession) throws Exception {
        logger.info("Session is created [{}]", ioSession.getId());
    }

    @Override

    public void sessionOpened(IoSession session) {

        InetSocketAddress remoteAddress = (InetSocketAddress) session.getRemoteAddress();

        String clientIp = remoteAddress.getAddress().getHostAddress();

        logger.info("LongConnect Server opened Session ID =" + String.valueOf(session.getId()));
//
//        logger.info("接收来自客户端 :" + clientIp + "的连接.");
//
//        HashMap<String, DeviceInfo> clientMap = getInstance().getClientMap();
//
//
//
//        clientMap.put(clientIp, session);

//        logger.info(clientMap.toString());
    }

    @Override
    public void sessionClosed(IoSession ioSession) throws Exception {
        logger.info("Session is closed.");
        SessionUtil.removeSession(ioSession);
        logger.info("{} is removed from system.", ioSession);
    }


    @Override
    public void messageReceived(IoSession session, Object message) throws IllegalAccessException, NoSuchFieldException, IOException {

        logger.info("Message received in the long connect server...");

        Map<MessageType, byte[]> messageMap = (Map<MessageType, byte[]>) message;

        TcpHandler handler = null;

        for (MessageType msgType : messageMap.keySet()) {

            String sessionId = String.valueOf(session.getId());
            logger.debug("Received message from session id: {}", sessionId);

            if ((!authorizedSession.contains(sessionId) && msgType.equals(MessageType.LoginRequest))) {
                if (verifyUser(messageMap.get(msgType), session)) {
                    authorizedSession.add(sessionId);
                    logger.info("Authorize devices {}", authorizedSession);
                    session.write(loginHandler.handle(messageMap.get(msgType), session));
                }
            } else if (authorizedSession.contains(sessionId)) {

                switch (msgType) {
                    case LoginRequest:
                        handler = loginHandler;
                        break;
                    case HeartbeatRequest:
                        handler = heartbeatHandler;
                        break;
                    case PowerStatusResponse:
                        handler = powerControlHandler;
                        break;
                    case TopUpResponse:
                        handler = topUpHandler;
                        break;
                    case CounterResponse:
                        handler = counterHandler;
                        break;
                    case ResetCounterResponse:
                        handler = counterResetHandler;
                        break;
                    case RuntimeResponse:
                        handler = runtimeHandler;
                        break;
                    case CounterStatusResponse:
                        handler = counterStatusHandler;
                        break;
                }
                if (null != handler) {
                    byte[] msg = handler.handle(messageMap.get(msgType), session);

                    if (null != msg) {
                        logger.info("Message will be sent = {}", ByteArrayUtil.toHexString(msg));
                        IoFuture future = session.write(msg);
//                        future.awaitUninterruptibly();
                    } else {
                        logger.info("return is null, not sent back to tcp client device");
                    }

                } else {
                    logger.error("There is no handler for message type {}", msgType);
                }
            } else {
                logger.info("Not yet auth request!");
            }
        }


    }

    private void deviceControl(IoSession session) {

        MessageHeader header = new MessageHeader();
        header.setMessageId(new byte[]{00, 00, 00, 00});
        header.setMsgType(MessageType.PowerControlRequest);
        header.setMessageSN(new byte[]{00, 00, 00, 00});
        header.setDeviceId(new byte[]{00, 00, 00, 01});

        counterIndex = counterIndex > 6 ? 0 : counterIndex;

        byte[] req = null;
        if (counterIndex == 0 || counterIndex == 2) {
            CounterRequest counterRequest = new CounterRequest();
            counterRequest.setHeader(header);
            counterRequest.setReqCoin(true);
            counterRequest.setReqPrize(false);
            req = counterRequest.build();
            logger.info("Requesting counter with {}", ByteArrayUtil.toHexString(req));
        } else if (counterIndex == 1) {
            TopUpRequest request = new TopUpRequest();
            request.setHeader(header);
            request.setReferenceId("ABCDEF0001");
            request.setTopUpQuantity(1);
            req = request.build();
            logger.info("Top up coin with {}", ByteArrayUtil.toHexString(req));
        } else if (counterIndex == 3) {
            ResetCounterRequest resetCounterRequest = new ResetCounterRequest();
            resetCounterRequest.setHeader(header);
            resetCounterRequest.setResetCoin(true);
            resetCounterRequest.setResetPrize(true);
            req = resetCounterRequest.build();
            logger.info("Reset counter request {}", ByteArrayUtil.toHexString(req));
        } else if (counterIndex == 4) {
            RuntimeRequest request = new RuntimeRequest();
            request.setHeader(header);
            req = request.build();
            logger.info("Requesting device runtime.");
        } else if (counterIndex == 5) {
            CounterStatusRequest request = new CounterStatusRequest();
            request.setHeader(header);
            req = request.build();
            logger.info("Request counter status. {}", ByteArrayUtil.toHexString(req));
        } else if (counterIndex == 6) {
            switcher = "N".equals(switcher) ? "Y" : "N";
            CounterSwitchRequest request = new CounterSwitchRequest();
            request.setHeader(header);
            request.setSwitcher(switcher);
            req = request.build();
            logger.info("Switch counter status to {}. {}", switcher, ByteArrayUtil.toHexString(req));
        }
        counterIndex++;

        if (null != req) session.write(req);
    }

    private void powerControl(IoSession session) {

        switcher = "I".equals(switcher) ? "O" : "I";


        MessageHeader header = new MessageHeader();
        header.setMessageId(new byte[]{00, 00, 00, 00});
        header.setMsgType(MessageType.PowerControlRequest);
        header.setMessageSN(new byte[]{00, 00, 00, 00});
        header.setDeviceId(new byte[]{00, 00, 00, 01});

        if (counterIndex % 2 == 0) {
            PowerStatusRequest pwdStatusReq = new PowerStatusRequest();
            pwdStatusReq.setHeader(header);
            logger.info("Query power status of the device. {}", ByteArrayUtil.toHexString(pwdStatusReq.build()));
            session.write(pwdStatusReq.build());
        } else {
            PowerControlRequest pwdReq = new PowerControlRequest();
            pwdReq.setHeader(header);
            pwdReq.setSwitcher(switcher);
            logger.info(switcher + " message {} is sent", ByteArrayUtil.toHexString(pwdReq.build()));
            session.write(pwdReq.build());
        }

    }

    private boolean verifyUser(byte[] message, IoSession session) throws IllegalAccessException, NoSuchFieldException, IOException {
        boolean isAuthroized = false;
        LoginRequest request = new LoginRequest();

        request.parse(message);

        byte[] mac = request.getMac();
        if (null != mac && isValid()) {

            logger.info("Login device: {}", ByteArrayUtil.toHexString(mac));

            isAuthroized = true;
            String sessionKey = SessionUtil.createSessionKey(request.getCenterId(), mac);
            Initialization init = getInstance();
            HashMap<String, DeviceInfo> clientMap = init.getClientMap();

            DeviceInfo deviceInfo = new DeviceInfo(ByteArrayUtil.toHexString(mac));
            deviceInfo.setSession(session);
            deviceInfo.setMessageHeader(request.getHeader());

            clientMap.put(sessionKey, deviceInfo);
        }

        return isAuthroized;
    }

    private boolean isValid() {

        //TODO: verify user here!
        return true;
    }

    @Override
    public void messageSent(IoSession ioSession, Object o) throws Exception {
        logger.info("Message is sent for session [{}].", ioSession.getId());
    }


    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {

        logger.info("Disconnecting the idle.");
        SessionUtil.removeSession(session);
        // disconnect an idle client
        logger.debug("Session is removed since idle.");
        session.close(true);

    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {

        // close the connection on exceptional situation

        logger.warn(cause.getMessage(), cause);
        SessionUtil.removeSession(session);
        logger.warn("Exception is caught, remove session {}", session.getId());
        session.close(true);

    }
}
