package com.ajlopez.blockchain;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.config.NetworkConfiguration;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by ajlopez on 26/02/2020.
 */
public class RpcRunnerTest {
    @Test
    public void simpleRequest() throws IOException {
        RpcRunner rpcRunner = new RpcRunner(6000, null, null);

        rpcRunner.start();

        Socket socket = new Socket("127.0.0.1", 6000);
        PrintWriter writer = new PrintWriter(socket.getOutputStream());

        writer.println("POST /\r\n");
        writer.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String result = reader.readLine();

        rpcRunner.stop();
        socket.close();

        Assert.assertNotNull(result);
        Assert.assertEquals("HTTP/1.1 404 ERROR", result);
    }

    @Test
    public void getBlockNumber() throws IOException {
        BlockChain blockChain = FactoryHelper.createBlockChain(10);

        RpcRunner rpcRunner = new RpcRunner(6001, blockChain, null);

        rpcRunner.start();

        Socket socket = new Socket("127.0.0.1", 6001);
        PrintWriter writer = new PrintWriter(socket.getOutputStream());

        String request = "POST /\r\n\r\n{ \"id\": 1, \"jsonrpc\": \"2.0\", \"method\": \"eth_blockNumber\", \"params\": [] }";
        writer.println(request);
        writer.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String result = reader.readLine() + "\r\n"
            + reader.readLine() + "\r\n"
            + reader.readLine();

        rpcRunner.stop();
        socket.close();

        Assert.assertNotNull(result);
        Assert.assertEquals("HTTP/1.1 200 OK\r\n\r\n{ \"id\": \"1\", \"jsonrpc\": \"2.0\", \"result\": \"0x0a\" }", result);
    }

    @Test
    public void getNetworkVersion() throws IOException {
        NetworkConfiguration networkConfiguration = new NetworkConfiguration((short)42);

        RpcRunner rpcRunner = new RpcRunner(6002, null, networkConfiguration);

        rpcRunner.start();

        Socket socket = new Socket("127.0.0.1", 6002);
        PrintWriter writer = new PrintWriter(socket.getOutputStream());

        String request = "POST /\r\n\r\n{ \"id\": 1, \"jsonrpc\": \"2.0\", \"method\": \"net_version\", \"params\": [] }";
        writer.println(request);
        writer.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String result = reader.readLine() + "\r\n"
                + reader.readLine() + "\r\n"
                + reader.readLine();

        rpcRunner.stop();
        socket.close();

        Assert.assertNotNull(result);
        Assert.assertEquals("HTTP/1.1 200 OK\r\n\r\n{ \"id\": \"1\", \"jsonrpc\": \"2.0\", \"result\": \"0x2a\" }", result);
    }
}
