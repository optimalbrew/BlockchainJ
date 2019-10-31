package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.config.NetworkConfiguration;

import java.io.IOException;

/**
 * Created by ajlopez on 2019/04/08.
 */
public class NetworkProcessor extends AbstractJsonRpcProcessor {
    private final NetworkConfiguration networkConfiguration;

    public NetworkProcessor(NetworkConfiguration networkConfiguration) {
        this.networkConfiguration = networkConfiguration;
    }

    @Override
    public JsonRpcResponse processRequest(JsonRpcRequest request) throws JsonRpcException, IOException {
        if (request.check("net_version", 0))
            return getVersion(request);

        return super.processRequest(request);
    }

    private JsonRpcResponse getVersion(JsonRpcRequest request) throws JsonRpcException {
        int result = this.networkConfiguration.getNetworkNumber();

        return JsonRpcResponse.createResponse(request, result);
    }
}
