package server;

import ch.qos.logback.core.encoder.ByteArrayUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Boss on 2014/8/29.
 */
public class TcpKeepAliveClient {
    private String ip;

    private int port;

    private static Socket socket = null;

    private static int timeout = 50 * 1000;



    public TcpKeepAliveClient(String ip, int port) {

        this.ip = ip;

        this.port = port;

    }



    public void receiveAndSend() throws IOException {

        InputStream input = null;

        OutputStream output = null;



        try {

            if (socket == null ||socket.isClosed() || !socket.isConnected()) {

                socket = new Socket();

                InetSocketAddress addr = new InetSocketAddress(ip, port);

                socket.connect(addr, timeout);

                socket.setSoTimeout(timeout);

                System.out.println("Tcp Keep Alive Client");

            }



            input = socket.getInputStream();

            output = socket.getOutputStream();



            // read body

            byte[] receiveBytes = {};// 收到的包字节数组

            String str = new String(new byte[]{0x2a, 0x01, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x01, 0x10, 0x04, 0x00, 0x59, 0x00, 0x00, 0x00, 0x00, 0x00, 0x23}, "UTF-8");
            output.write(str.getBytes());

            while (true) {

                if (input.available() > 0) {

                    receiveBytes = new byte[input.available()];

                    input.read(receiveBytes);



                    // send

                    System.out.println("Received msg: " + ByteArrayUtil.toHexString(receiveBytes));

//                    output.write(receiveBytes, 0, receiveBytes.length);

//                    output.flush();

                }

            }



        } catch (Exception e) {

            e.printStackTrace();

            System.out.println("TcpClientnew socket error");

        }

    }



    public static void main(String[] args) throws Exception {

//        TcpKeepAliveClient client = new TcpKeepAliveClient("127.0.0.1", 8002);
        TcpKeepAliveClient client = new TcpKeepAliveClient("alcock.gicp.net", 8002);

        client.receiveAndSend();

    }


}
