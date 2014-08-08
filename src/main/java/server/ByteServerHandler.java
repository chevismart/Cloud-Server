package server;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

/**
 * 自定义的消息处理器，必须实现IoHandlerAdapter类
 * @author 何明
 *
 */
public class ByteServerHandler extends IoHandlerAdapter{

    private int count = 0;

    /**
     * 当一个客户端连接进入时
     */
    @Override
    public void sessionOpened(IoSession session) throws Exception {

        System.out.println("incoming client: " + session.getRemoteAddress());

    }

    /**
     * 当一个客户端关闭时
     */
    @Override
    public void sessionClosed(IoSession session) throws Exception {

        System.out.println(session.getRemoteAddress() + "is Disconnection");

    }

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {

        IoBuffer ioBuffer = (IoBuffer)message;
        byte[] b = new byte[ioBuffer.limit()];
        ioBuffer.get(b);

        String msg=new String(b);

        System.out.println("收到客户端发来的消息为" + "  " + msg);

        //将测试消息会送给客户端
        //session.write(str + count);
    }

}