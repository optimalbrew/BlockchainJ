package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class BlockTest {
    @Test
    public void createWithNumberAndParentHash() {
        BlockHash hash = new BlockHash(HashUtilsTest.generateRandomHash());
        Block block = new Block(1L, hash, HashUtilsTest.generateRandomHash());

        Assert.assertEquals(1L, block.getNumber());
        Assert.assertEquals(hash, block.getParentHash());
        Assert.assertNotNull(block.getHash());
    }

    @Test
    public void noTransactions() {
        BlockHash hash = new BlockHash(HashUtilsTest.generateRandomHash());
        Block block = new Block(1L, hash, HashUtilsTest.generateRandomHash());

        List<Transaction> transactions = block.getTransactions();

        Assert.assertNotNull(transactions);
        Assert.assertTrue(transactions.isEmpty());
    }

    @Test
    public void blockWithDifferentParentHashesHaveDifferentHashes() {
        Block block1 = new Block(1L, new BlockHash(HashUtilsTest.generateRandomHash()), HashUtilsTest.generateRandomHash());
        Block block2 = new Block(1L, new BlockHash(HashUtilsTest.generateRandomHash()), HashUtilsTest.generateRandomHash());

        Assert.assertNotEquals(block1.getHash(), block2.getHash());
    }

    @Test
    public void blockWithDifferentNumbersHaveDifferentHashes() {
        BlockHash parentHash = new BlockHash(HashUtilsTest.generateRandomHash());
        Block block1 = new Block(1L, parentHash, HashUtilsTest.generateRandomHash());
        Block block2 = new Block(2L, parentHash, HashUtilsTest.generateRandomHash());

        Assert.assertNotEquals(block1.getHash(), block2.getHash());
    }

    @Test
    public void blockWithDifferentTransactionsHaveDifferentHashes() {
        Transaction tx1 = FactoryHelper.createTransaction(42);
        Transaction tx2 = FactoryHelper.createTransaction(144);

        List<Transaction> txs1 = new ArrayList<>();
        List<Transaction> txs2 = new ArrayList<>();

        txs1.add(tx1);
        txs2.add(tx2);

        BlockHash hash = new BlockHash(HashUtilsTest.generateRandomHash());

        Block block1 = new Block(1L, hash, txs1, HashUtilsTest.generateRandomHash());
        Block block2 = new Block(1L, hash, txs2, HashUtilsTest.generateRandomHash());

        Assert.assertNotEquals(block1.getHash(), block2.getHash());
    }

    @Test
    public void withOneTransaction() {
        Transaction tx = FactoryHelper.createTransaction(42);

        List<Transaction> txs = new ArrayList<>();
        txs.add(tx);

        BlockHash hash = new BlockHash(HashUtilsTest.generateRandomHash());
        Block block = new Block(1L, hash, txs, HashUtilsTest.generateRandomHash());

        List<Transaction> transactions = block.getTransactions();

        Assert.assertNotNull(transactions);
        Assert.assertFalse(transactions.isEmpty());
        Assert.assertEquals(1, transactions.size());
        Assert.assertEquals(tx.getHash(), transactions.get(0).getHash());
    }
}
