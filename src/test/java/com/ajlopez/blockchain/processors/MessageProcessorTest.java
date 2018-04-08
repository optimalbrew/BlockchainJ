package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.net.OutputChannel;
import com.ajlopez.blockchain.net.Peer;
import com.ajlopez.blockchain.net.PeerId;
import com.ajlopez.blockchain.net.messages.*;
import com.ajlopez.blockchain.test.simples.SimpleOutputChannel;
import com.ajlopez.blockchain.test.simples.SimplePeer;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by ajlopez on 27/01/2018.
 */
public class MessageProcessorTest {
    @Test
    public void processBlockMessage() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();

        Block block = new Block(0, null);
        Message message = new BlockMessage(block);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor);

        processor.processMessage(message, null);

        Block result = blockProcessor.getBestBlock();

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getHash(), result.getHash());
    }

    @Test
    public void processGetBlockByHashMessage() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();
        OutputProcessor outputProcessor = new OutputProcessor();

        Block block = new Block(0, null);
        Message blockMessage = new BlockMessage(block);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, outputProcessor);

        processor.processMessage(blockMessage, null);

        Message message = new GetBlockByHashMessage(block.getHash());

        Peer sender = new Peer(HashUtilsTest.generateRandomPeerId());
        SimpleOutputChannel channel = new SimpleOutputChannel();
        outputProcessor.registerPeer(sender, channel);

        processor.processMessage(message, sender);

        Message result = channel.getMessage();

        Assert.assertNotNull(result);
        Assert.assertEquals(MessageType.BLOCK, result.getMessageType());

        BlockMessage bmessage = (BlockMessage)result;

        Assert.assertNotNull(bmessage.getBlock());
        Assert.assertEquals(block.getHash(), bmessage.getBlock().getHash());
    }

    @Test
    public void processGetBlockByNumberMessage() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();
        OutputProcessor outputProcessor = new OutputProcessor();

        Block block = new Block(0, null);
        Message blockMessage = new BlockMessage(block);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, outputProcessor);

        processor.processMessage(blockMessage, null);

        Message getBlockMessage = new GetBlockByNumberMessage(block.getNumber());
        Peer sender = new Peer(HashUtilsTest.generateRandomPeerId());
        SimpleOutputChannel channel = new SimpleOutputChannel();
        outputProcessor.registerPeer(sender, channel);

        processor.processMessage(getBlockMessage, sender);

        Message result = channel.getMessage();

        Assert.assertNotNull(result);
        Assert.assertEquals(MessageType.BLOCK, result.getMessageType());

        BlockMessage bmessage = (BlockMessage)result;

        Assert.assertNotNull(bmessage.getBlock());
        Assert.assertEquals(block.getHash(), bmessage.getBlock().getHash());
    }

    @Test
    public void processTransactionMessage() {
        TransactionPool pool = new TransactionPool();
        TransactionProcessor transactionProcessor = new TransactionProcessor(pool);

        Transaction transaction = FactoryHelper.createTransaction(100);
        Message message = new TransactionMessage(transaction);

        MessageProcessor processor = FactoryHelper.createMessageProcessor(transactionProcessor);

        processor.processMessage(message, null);

        List<Transaction> transactions = pool.getTransactions();

        Assert.assertNotNull(transactions);
        Assert.assertFalse(transactions.isEmpty());
        Assert.assertEquals(1, transactions.size());

        Transaction result = transactions.get(0);

        Assert.assertNotNull(result);
        Assert.assertEquals(transaction.getHash(), result.getHash());
    }

    @Test
    public void processStatusMessageAndStartSync() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();
        PeerProcessor peerProcessor = new PeerProcessor();
        OutputProcessor outputProcessor = new OutputProcessor();

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, peerProcessor, outputProcessor);

        PeerId peerId = HashUtilsTest.generateRandomPeerId();
        Peer peer = new Peer(peerId);
        SimpleOutputChannel channel = new SimpleOutputChannel();
        outputProcessor.registerPeer(peer, channel);

        Message message = new StatusMessage(peerId, 1, 10);

        processor.processMessage(message, peer);

        Assert.assertEquals(10, peerProcessor.getBestBlockNumber());
        Assert.assertEquals(10, peerProcessor.getPeerBestBlockNumber(peerId));

        Assert.assertEquals(11, channel.getMessages().size());

        for (int k = 0; k < 11; k++) {
            Message msg = channel.getMessages().get(k);

            Assert.assertNotNull(msg);
            Assert.assertEquals(MessageType.GET_BLOCK_BY_NUMBER, msg.getMessageType());

            GetBlockByNumberMessage gmsg = (GetBlockByNumberMessage)msg;

            Assert.assertEquals(k, gmsg.getNumber());
        }
    }

    @Test
    public void processStatusMessageTwiceWithSameHeightAndStartSync() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();
        PeerProcessor peerProcessor = new PeerProcessor();
        OutputProcessor outputProcessor = new OutputProcessor();

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, peerProcessor, outputProcessor);

        PeerId peerId = HashUtilsTest.generateRandomPeerId();
        Peer peer = new Peer(peerId);
        SimpleOutputChannel channel = new SimpleOutputChannel();
        outputProcessor.registerPeer(peer, channel);

        Message message = new StatusMessage(peerId, 1, 10);

        processor.processMessage(message, peer);
        processor.processMessage(message, peer);

        Assert.assertEquals(10, peerProcessor.getBestBlockNumber());
        Assert.assertEquals(10, peerProcessor.getPeerBestBlockNumber(peerId));

        Assert.assertEquals(11, channel.getMessages().size());

        for (int k = 0; k < 11; k++) {
            Message msg = channel.getMessages().get(k);

            Assert.assertNotNull(msg);
            Assert.assertEquals(MessageType.GET_BLOCK_BY_NUMBER, msg.getMessageType());

            GetBlockByNumberMessage gmsg = (GetBlockByNumberMessage)msg;

            Assert.assertEquals(k, gmsg.getNumber());
        }
    }

    @Test
    public void processStatusMessageTwiceWithDifferentHeightsAndStartSync() {
        BlockProcessor blockProcessor = FactoryHelper.createBlockProcessor();
        PeerProcessor peerProcessor = new PeerProcessor();
        OutputProcessor outputProcessor = new OutputProcessor();

        MessageProcessor processor = FactoryHelper.createMessageProcessor(blockProcessor, peerProcessor, outputProcessor);

        PeerId peerId = HashUtilsTest.generateRandomPeerId();
        Peer peer = new Peer(peerId);
        SimpleOutputChannel channel = new SimpleOutputChannel();
        outputProcessor.registerPeer(peer, channel);

        Message message1 = new StatusMessage(peerId, 1, 5);
        Message message2 = new StatusMessage(peerId, 1, 10);

        processor.processMessage(message1, peer);
        processor.processMessage(message2, peer);

        Assert.assertEquals(10, peerProcessor.getBestBlockNumber());
        Assert.assertEquals(10, peerProcessor.getPeerBestBlockNumber(peerId));

        Assert.assertEquals(11, channel.getMessages().size());

        for (int k = 0; k < 11; k++) {
            Message msg = channel.getMessages().get(k);

            Assert.assertNotNull(msg);
            Assert.assertEquals(MessageType.GET_BLOCK_BY_NUMBER, msg.getMessageType());

            GetBlockByNumberMessage gmsg = (GetBlockByNumberMessage)msg;

            Assert.assertEquals(k, gmsg.getNumber());
        }
    }
}
