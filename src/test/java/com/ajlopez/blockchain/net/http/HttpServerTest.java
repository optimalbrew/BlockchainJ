package com.ajlopez.blockchain.net.http;

import com.ajlopez.blockchain.jsonrpc.TopProcessor;
//import jdk.internal.util.xml.impl.Input;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.net.Socket;

/**
 * Created by ajlopez on 14/04/2019.
 */
public class HttpServerTest {
    @Test
    public void processGet() throws IOException {
        TopProcessor topProcessor = new TopProcessor();
        HttpServer httpServer = new HttpServer(5000, topProcessor);

        httpServer.start();

        Socket socket = new Socket("127.0.0.1", 5000);
        PrintWriter writer = new PrintWriter(socket.getOutputStream());

        writer.println("GET /");
        writer.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String result = reader.readLine();

        Assert.assertNotNull(result);
        Assert.assertEquals("HTTP/1.1 404 ERROR", result);

        httpServer.stop();
    }

    @Test
    public void processPostWithInvalidPayload() throws IOException {
        TopProcessor topProcessor = new TopProcessor();
        HttpServer httpServer = new HttpServer(5001, topProcessor);

        httpServer.start();

        Socket socket = new Socket("127.0.0.1", 5001);
        PrintWriter writer = new PrintWriter(socket.getOutputStream());

        writer.println("POST /\r\n\r\n42");
        writer.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String result = reader.readLine();

        Assert.assertNotNull(result);
        Assert.assertEquals("HTTP/1.1 404 ERROR", result);

        httpServer.stop();
    }

    @Test
    public void processPostWithoutPayload() throws IOException {
        TopProcessor topProcessor = new TopProcessor();
        HttpServer httpServer = new HttpServer(5002, topProcessor);

        httpServer.start();

        Socket socket = new Socket("127.0.0.1", 5002);
        PrintWriter writer = new PrintWriter(socket.getOutputStream());

        writer.println("POST /\r\n");
        writer.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String result = reader.readLine();

        Assert.assertNotNull(result);
        Assert.assertEquals("HTTP/1.1 404 ERROR", result);

        httpServer.stop();
    }
}
