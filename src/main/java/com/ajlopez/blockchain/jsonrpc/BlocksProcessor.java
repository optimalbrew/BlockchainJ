package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.bc.BlockChain;

/**
 * Created by ajlopez on 30/11/2018.
 */
public class BlocksProcessor extends AbstractJsonRpcProcessor {
    private final BlockChain blockChain;

    public BlocksProcessor(BlockChain blockChain) {
        this.blockChain = blockChain;
    }

    @Override
    public JsonRpcResponse processRequest(JsonRpcRequest request) throws JsonRpcException {
        if (request.check("eth_blockNumber", 0))
            return JsonRpcResponse.createResponse(request, this.blockChain.getBestBlockNumber());

        return super.processRequest(request);
    }
}