package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class BlockHeaderTest {
    @Test
    public void createWithNumberAndParentHash() {
        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Hash transactionsHash = FactoryHelper.createRandomHash();
        Hash stateRootHash = FactoryHelper.createRandomHash();
        Address coinbase = FactoryHelper.createRandomAddress();
        Difficulty difficulty = Difficulty.fromUnsignedLong(42);

        BlockHeader header = new BlockHeader(1L, hash, transactionsHash, stateRootHash, System.currentTimeMillis() / 1000, coinbase, difficulty);

        Assert.assertEquals(1L, header.getNumber());
        Assert.assertEquals(hash, header.getParentHash());
        Assert.assertEquals(transactionsHash, header.getTransactionsRootHash());
        Assert.assertNotNull(header.getHash());
        Assert.assertEquals(Difficulty.fromUnsignedLong(42), header.getDifficulty());
    }

    @Test
    public void equals() {
        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Hash transactionsHash = FactoryHelper.createRandomHash();
        Hash stateRootHash = FactoryHelper.createRandomHash();
        Address coinbase = FactoryHelper.createRandomAddress();
        Difficulty difficulty = Difficulty.fromUnsignedLong(42);

        BlockHeader header = new BlockHeader(1L, hash, transactionsHash, stateRootHash, 1L, coinbase, difficulty);
        BlockHeader header2 = new BlockHeader(1L, hash, transactionsHash, stateRootHash, 1L, coinbase, difficulty);
        BlockHeader header3 = new BlockHeader(1L, hash, transactionsHash, stateRootHash, 2L, coinbase, difficulty);

        Assert.assertTrue(header.equals(header2));
        Assert.assertTrue(header2.equals(header));
        Assert.assertFalse(header.equals(header3));
        Assert.assertFalse(header2.equals(header3));
        Assert.assertFalse(header3.equals(header));
        Assert.assertFalse(header3.equals(header2));

        Assert.assertFalse(header.equals(null));
        Assert.assertFalse(header.equals("foo"));

        Assert.assertEquals(header.hashCode(), header2.hashCode());
        Assert.assertNotEquals(header.hashCode(), header3.hashCode());
    }

    @Test
    public void twoDifferentHeadersHaveDifferentHashes() {
        BlockHeader header1 = new BlockHeader(1L, FactoryHelper.createRandomBlockHash(), FactoryHelper.createRandomHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress(), Difficulty.fromUnsignedLong(42));
        BlockHeader header2 = new BlockHeader(2L, FactoryHelper.createRandomBlockHash(), FactoryHelper.createRandomHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress(), Difficulty.fromUnsignedLong(42));

        Assert.assertNotEquals(header1.getHash(), header2.getHash());
    }

    @Test(expected = IllegalStateException.class)
    public void negativeNumber() {
        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Hash transactionsHash = FactoryHelper.createRandomHash();
        Hash stateRootHash = FactoryHelper.createRandomHash();

        new BlockHeader(-1L, hash, transactionsHash, stateRootHash, System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress(), null);
    }
}
