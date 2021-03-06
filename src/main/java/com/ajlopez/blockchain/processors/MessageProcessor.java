package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.net.peers.Peer;
import com.ajlopez.blockchain.net.messages.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by ajlopez on 27/01/2018.
 */
public class MessageProcessor {
    private final BlockProcessor blockProcessor;
    private final TransactionProcessor transactionProcessor;
    private final PeerProcessor peerProcessor;
    private final SendProcessor outputProcessor;
    private final WarpProcessor warpProcessor;

    public MessageProcessor(BlockProcessor blockProcessor, TransactionProcessor transactionProcessor, PeerProcessor peerProcessor, SendProcessor outputProcessor, WarpProcessor warpProcessor) {
        this.blockProcessor = blockProcessor;
        this.transactionProcessor = transactionProcessor;
        this.peerProcessor = peerProcessor;
        this.outputProcessor = outputProcessor;
        this.warpProcessor = warpProcessor;
    }

    public void processMessage(Message message, Peer sender) {
        MessageType msgtype = message.getMessageType();

        try {
            if (msgtype == MessageType.BLOCK)
                this.processBlockMessage((BlockMessage) message, sender);
            else if (msgtype == MessageType.GET_BLOCK_BY_HASH)
                this.processGetBlockByHashMessage((GetBlockByHashMessage) message, sender);
            else if (msgtype == MessageType.GET_BLOCK_BY_NUMBER)
                this.processGetBlockByNumberMessage((GetBlockByNumberMessage) message, sender);
            else if (msgtype == MessageType.TRANSACTION)
                this.processTransactionMessage((TransactionMessage) message, sender);
            else if (msgtype == MessageType.STATUS)
                this.processStatusMessage((StatusMessage) message, sender);
            else if (msgtype == MessageType.TRIE_NODE)
                this.processTrieNodeMessage((TrieNodeMessage) message);
        }
        catch (IOException ex) {
            // Add to logger
            ex.printStackTrace();
        }
    }

    private void processBlockMessage(BlockMessage message, Peer sender) throws IOException {
        List<Block> processed = this.blockProcessor.processBlock(message.getBlock());

        if (this.outputProcessor == null)
            return;

        int nprocessed = 0;

        for (Block block : processed) {
            Message outputMessage = new BlockMessage(block);

            if (nprocessed == 0 && sender != null)
                this.outputProcessor.postMessage(outputMessage, Collections.singletonList(sender.getId()));
            else
                this.outputProcessor.postMessage(outputMessage);

            nprocessed++;
        }

        if (nprocessed > 0)
            return;

        BlockHash blockHash = message.getBlock().getHash();
        BlockHash ancestorHash = this.blockProcessor.getUnknownAncestorHash(blockHash);

        if (ancestorHash != null && !ancestorHash.equals(blockHash))
            this.outputProcessor.postMessage(sender, new GetBlockByHashMessage(ancestorHash));
    }

    private void processTransactionMessage(TransactionMessage message, Peer sender) {
        List<Transaction> processed = this.transactionProcessor.processTransaction(message.getTransaction());

        if (this.outputProcessor == null)
            return;

        int nprocessed = 0;

        for (Transaction transaction: processed) {
            Message outputMessage = new TransactionMessage(transaction);

            if (nprocessed == 0 && sender != null)
                this.outputProcessor.postMessage(outputMessage, Collections.singletonList(sender.getId()));
            else
                this.outputProcessor.postMessage(outputMessage);

            nprocessed++;
        }
    }

    private void processStatusMessage(StatusMessage message, Peer sender) {
        if (message.getStatus().getNetworkNumber() != this.peerProcessor.getNetworkNumber())
            return;

        Hash senderId = sender.getId();

        long peerNumber = this.peerProcessor.getPeerBestBlockNumber(senderId);

        this.peerProcessor.registerBestBlockNumber(senderId, message.getStatus().getNetworkNumber(), message.getStatus().getBestBlockNumber());

        long fromNumber = this.blockProcessor.getBestBlockNumber();

        if (fromNumber < peerNumber)
            fromNumber = peerNumber;

        long toNumber = this.peerProcessor.getPeerBestBlockNumber(senderId);

        for (long number = fromNumber + 1; number <= toNumber; number++)
            outputProcessor.postMessage(sender, new GetBlockByNumberMessage(number));
    }

    private void processGetBlockByHashMessage(GetBlockByHashMessage message, Peer sender) {
        Block block = this.blockProcessor.getBlockByHash(message.getHash());

        if (block != null)
            outputProcessor.postMessage(sender, new BlockMessage(block));
    }

    private void processGetBlockByNumberMessage(GetBlockByNumberMessage message, Peer sender) throws IOException {
        Block block = this.blockProcessor.getBlockByNumber(message.getNumber());

        if (block != null)
            outputProcessor.postMessage(sender, new BlockMessage(block));
    }

    private void processTrieNodeMessage(TrieNodeMessage message) throws IOException {
        this.warpProcessor.processAccountNode(message.getTopHash(), message.getTrieNode());
    }
}
