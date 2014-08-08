package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MinaLongConnServerTest {
    private String ip;

    private int port;

    private static Socket socket = null;

    private static int timeout = 50 * 1000;


    public MinaLongConnServerTest(String ip, int port) {

        this.ip = ip;

        this.port = port;

    }


    public void receiveAndSend() throws IOException {

        InputStream input = null;

        OutputStream output = null;


        try {

            if (socket == null || socket.isClosed() || !socket.isConnected()) {

                socket = new Socket();

                InetSocketAddress addr = new InetSocketAddress(ip, port);

                socket.connect(addr, timeout);

                socket.setSoTimeout(timeout);

                System.out.println("Tcp Keep Alive Client ");

                // 输出
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                out.print(new byte[]{0x01});
                out.flush();
            }


            input = socket.getInputStream();

            output = socket.getOutputStream();


            // read body

            byte[] receiveBytes = {};// 收到的包字节数组

            while (true) {

                if (input.available() > 0) {

                    receiveBytes = new byte[input.available()];

                    input.read(receiveBytes);


                    // send

                    System.out.println("Tcp Keep Alive Client send date :" + new String(receiveBytes));

                    output.write(receiveBytes, 0, receiveBytes.length);

                    output.flush();

                }

            }


        } catch (Exception e) {

            e.printStackTrace();

            System.out.println("Tcp Client new socket error");

        }

    }


    public static void main(String[] args) throws Exception {

        MinaLongConnServerTest client = new MinaLongConnServerTest("127.0.0.1", 8002);

        client.receiveAndSend();

    }
}