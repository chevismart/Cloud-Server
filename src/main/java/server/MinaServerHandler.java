package server;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

/**
 * Created by Boss on 2014/8/6.
 */
public class MinaServerHandler extends IoHandlerAdapter {

    @Override
    public void messageReceived(IoSession session, Object message){
        //服务器端打印出请求的来源
        System.out.println("Request From:" + session.getRemoteAddress());
        //返给客户端的消息内容：服务器系统当前时间
        session.write("Server Time: "+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
    }

    public static void main(String[] args) throws IOException {
        //创建一个接收器，接收客户端请求
        IoAcceptor acceptor = new NioSocketAcceptor();

        //Don't know how to handle message of type 'java.lang.String'.  Are you missing a protocol encoder?
        //编码过滤器，使得服务器可以处理string类型的消息
        acceptor.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));

        //设置服务器端的处理程序
        acceptor.setHandler(new MinaServerHandler());
        acceptor.bind(new InetSocketAddress(60001));
    }
}
