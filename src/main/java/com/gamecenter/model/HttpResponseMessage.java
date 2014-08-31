package com.gamecenter.model;

import org.apache.mina.core.buffer.IoBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HttpResponseMessage {
    /** HTTP response codes */
    public static final int HTTP_STATUS_SUCCESS = 200;

    public static final int HTTP_STATUS_NOT_FOUND = 404;

    /** Map<String, String> */
    private final Map<String, String> headers = new HashMap<String, String>();

    /** Storage for body of HTTP response. */
    private final ByteArrayOutputStream body = new ByteArrayOutputStream(1024);

    private int responseCode = HTTP_STATUS_SUCCESS;

    public HttpResponseMessage() {
        // headers.put("Server", "HttpServer (" + Server.VERSION_STRING + ')');
        headers.put("Server", "HttpServer (" + "Mina 2.0" + ')');
        headers.put("Cache-Control", "private");
//        headers.put("Content-Type", "json/html; charset=iso-8859-1");
        headers.put("Connection", "keep-alive");
        headers.put("Keep-Alive", "200");
        headers.put("Date", new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss zzz").format(new Date()));
        headers.put("Last-Modified", new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss zzz").format(new Date()));
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setContentType(String contentType) {
        headers.put("Content-Type", contentType);
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    public void appendBody(byte[] b) {
        try {
            body.write(b);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void appendBody(String s) {
        try {
            body.write(s.getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public IoBuffer getBody() {
        return IoBuffer.wrap(body.toByteArray());
    }

    public int getBodyLength() {
        return body.size();
    }

    public String buildHTTPResponseMessage(){

        if(responseCode == HttpURLConnection.HTTP_OK){

            this.setContentType("application/json");

            StringBuilder sb = new StringBuilder();

            sb.append("HTTP/1.1\r\n");

            for(Map.Entry entry: headers.entrySet()){
                sb.append(entry.getKey())
                        .append(":")
                        .append(entry.getValue())
                        .append(";\r\n");
            }
            sb.append("\r\n\r\n");
            sb.append(body.toString());
//
//            sb.append("HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=UTF-8\r\nContent-Length: ");
//            sb.append(JadyerUtil.getBytes(httpResponseMessageBody, "UTF-8").length);
//            sb.append("\r\n\r\n");
//            sb.append(httpResponseMessageBody);
            return sb.toString();
        }
        if(responseCode == HttpURLConnection.HTTP_BAD_REQUEST){
            return "HTTP/1.1 400 Bad Request";
        }
        if(responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR){
            return "HTTP/1.1 500 Internal Server Error";
        }
        return "HTTP/1.1 501 Not Implemented";
    }

}