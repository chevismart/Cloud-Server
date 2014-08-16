package server;

import ch.qos.logback.core.encoder.ByteArrayUtil;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class NIOServerTest {
    public static void main(String[] args) throws Exception {
        Socket s = new Socket("127.0.0.1", 8002);

        InputStream inStram = s.getInputStream();
        OutputStream outStream = s.getOutputStream();

        // 输出
        PrintWriter out = new PrintWriter(outStream, true);
        String str = new String(new byte[]{0x2a, 0x01, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x01, 0x10, 0x04, 0x00, 0x59, 0x00, 0x00, 0x00, 0x23}, "UTF-8");
        System.err.println(str);
        System.err.println(str.length());
        System.err.println(ByteArrayUtil.toHexString(str.getBytes()));
        out.print(str);
        out.flush();

        s.shutdownOutput();// 输出结束

        // 输入
        Scanner in = new Scanner(inStram);
        StringBuilder sb = new StringBuilder();
        while (in.hasNextLine()) {
            String line = in.nextLine();
            sb.append(line);
        }
        String response = sb.toString();
        System.out.println("response=" + response);
    }
}