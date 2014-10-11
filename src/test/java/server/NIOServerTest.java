package server;

import ch.qos.logback.core.encoder.ByteArrayUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class NIOServerTest {
    public static void main(String[] args) throws Exception {

        NIOServerTest serverTest = new NIOServerTest();

        int times = 1000;
        for (int i = 1; i < times; i++) {
            serverTest.getAClient();
            Thread.sleep(500);

        }

//        Socket s = new Socket("127.0.0.1", 8002);
//
//        InputStream inStram = s.getInputStream();
//        OutputStream outStream = s.getOutputStream();
//
//
//
//        // 输出
//        PrintWriter out = new PrintWriter(outStream, true);
//        String str = new String(new byte[]{0x2a, 0x01, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x01, 0x10, 0x04, 0x00, 0x59, 0x00, 0x00, 0x00,0x00, 0x00, 0x23}, "UTF-8");
//        System.err.println(str);
//        System.err.println(str.length());
//        System.err.println(ByteArrayUtil.toHexString(str.getBytes()));
//        out.print(str);
//        out.flush();
//        PrintWriter out1 = new PrintWriter(outStream, true);
//        out1.print(str);
//        out1.flush();
//        s.shutdownOutput();// 输出结束

//        Thread.sleep(5000);
//
//        System.err.println(str);
//        System.err.println(str.length());
//        System.err.println(ByteArrayUtil.toHexString(str.getBytes()));
//        PrintWriter out1 = new PrintWriter(outStream, true);
//        out1.print(str);
//        out1.flush();
//
//
//        // 输入
//        Scanner in = new Scanner(inStram);
//        StringBuilder sb = new StringBuilder();
//        while (in.hasNextLine()) {
//            String line = in.nextLine();
//            sb.append(line);
//            System.out.println("response=" + sb);
//        }
//        String response = sb.toString();
//        System.out.println("response=" + response);

    }

    private void getAClient() throws IOException {
//        Socket s = new Socket("192.168.11.2", 8002);

        Socket s = new Socket("alcock.gicp.net", 8002);
//        Socket s = new Socket("127.0.0.1", 8002);

        InputStream inStram = s.getInputStream();
        OutputStream outStream = s.getOutputStream();

        PrintWriter out = new PrintWriter(outStream, true);
        String str = new String(new byte[]{0x2a, 0x01, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x11, 0x0a, 0x00, 0x0a, 0x59, 0x00, 0x00, 0x00, 0x6c, (byte) 0xf0, 0x049, (byte) 0xb6, 0x4d, (byte) 0xf3, 0x00, 0x00, 0x23}, "UTF-8");
        System.err.println(str);
        System.err.println(str.length());
        System.err.println(ByteArrayUtil.toHexString(str.getBytes()));
        out.print(str);
        out.flush();

//        输入
        Scanner in = new Scanner(inStram);
        StringBuilder sb = new StringBuilder();
        while (in.hasNextLine()) {
            String line = in.nextLine();
            sb.append(line);
            System.out.println("response=" + sb);
        }
        String response = sb.toString();
        System.out.println("response=" + response);
    }
}