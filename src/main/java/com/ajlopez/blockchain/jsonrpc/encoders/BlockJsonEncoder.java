package com.ajlopez.blockchain.jsonrpc.encoders;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.json.JsonBuilder;
import com.ajlopez.blockchain.json.JsonNullValue;
import com.ajlopez.blockchain.json.JsonObjectBuilder;
import com.ajlopez.blockchain.json.JsonValue;

import java.util.List;

/**
 * Created by ajlopez on 19/04/2019.
 */
public class BlockJsonEncoder {
    private BlockJsonEncoder() {}

    public static JsonValue encode(Block block) {
        if (block == null)
            return JsonNullValue.getInstance();

        JsonBuilder builder = (new JsonObjectBuilder(new JsonBuilder()))
                .name("hash")
                .value(block.getHash())
                .name("number")
                .value(block.getNumber())
                .name("parentHash")
                .value(block.getParentHash())
                .name("nonce")
                .value(0)
                .name("miner")
                .value(block.getCoinbase())
                .name("transactionRoot")
                .value(block.getTransactionRootHash())
                .name("stateRoot")
                .value(block.getStateRootHash())
                .name("timestamp")
                .value(block.getTimestamp())
                .name("difficulty")
                .value(block.getDifficulty())
                .name("uncles")
                .array()
                .end();

        List<Transaction> transactions = block.getTransactions();

        builder = builder.name("transactions").array();

        for (Transaction tx: transactions)
            builder = builder.value(tx.getHash());

        builder = builder.end();

        return builder.build();
    }
}
