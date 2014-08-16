package server;

import ch.qos.logback.core.encoder.ByteArrayUtil;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.gamecenter.serializer.constants.MessageType;
import org.gamecenter.serializer.messages.downStream.PowerControlRequest;
import org.gamecenter.serializer.messages.upStream.LoginRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Boss on 2014/8/6.
 */
public class MinaLongConnServerHandler implements IoHandler {

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

        Map<MessageType,byte[]> messageMap = (Map<MessageType, byte[]>) message;



        LoginRequest request = new LoginRequest();
        request.parse(messageMap.get(MessageType.LoginRequest));
        String sw = ByteArrayUtil.toHexString(request.getCenterId());
        System.err.println();
//        String msg=new String(b);
//
//        System.out.println("收到客户端发来的消息为" + "  " + msg);


        logger.info("Message received in the long connect server..");

        String expression = message.toString();

        logger.info("Message is:" + expression);

//        IoSession shortConnSession = (IoSession) session.getAttribute("shortConnSession");
//
//        logger.info("ShortConnect Server Session ID =" + String.valueOf(shortConnSession.getId()));
//
//        shortConnSession.write(expression);

    }

    @Override
    public void messageSent(IoSession ioSession, Object o) throws Exception {
        logger.info("Message Sent");
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
