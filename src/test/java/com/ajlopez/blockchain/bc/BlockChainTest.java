package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.BlockHeader;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.store.MemoryStores;
import com.ajlopez.blockchain.store.Stores;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ajlopez on 26/08/2017.
 */
public class BlockChainTest {
    @Test
    public void noBestBlock() {
        Stores stores = new MemoryStores();
        BlockChain blockChain = new BlockChain(stores);

        Assert.assertNull(blockChain.getBestBlock());
        Assert.assertEquals(BlockChain.NO_BEST_BLOCK_NUMBER, blockChain.getBestBlockNumber());
    }

    @Test
    public void noBlockByHash() {
        Stores stores = new MemoryStores();
        BlockChain blockChain = new BlockChain(stores);

        Assert.assertNull(blockChain.getBlockByHash(FactoryHelper.createRandomHash()));
    }

    @Test
    public void noBlockByNumber() throws IOException {
        Stores stores = new MemoryStores();
        BlockChain blockChain = new BlockChain(stores);

        Assert.assertNull(blockChain.getBlockByNumber(0));
        Assert.assertNull(blockChain.getBlockByNumber(1));
        Assert.assertNull(blockChain.getBlockByNumber(42));
    }

    @Test
    public void addFirstBlock() throws IOException {
        Stores stores = new MemoryStores();
        BlockChain blockChain = new BlockChain(stores);

        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(0, null, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        Assert.assertTrue(blockChain.connectBlock(block));

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(block.getHash(), blockChain.getBestBlock().getHash());

        Assert.assertEquals(block.getHash(), blockChain.getBlockByHash(block.getHash()).getHash());
        Assert.assertEquals(block.getHash(), blockChain.getBlockByNumber(block.getNumber()).getHash());

        Assert.assertEquals(0, blockChain.getBestBlockNumber());
    }

    @Test
    public void addSecondBlock() throws IOException {
        Stores stores = new MemoryStores();
        BlockChain blockChain = new BlockChain(stores);

        Address coinbase = FactoryHelper.createRandomAddress();

        Block genesis = new Block(0, null, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE);
        Block block = new Block(1, genesis.getHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        Assert.assertTrue(blockChain.connectBlock(genesis));
        Assert.assertTrue(blockChain.connectBlock(block));

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(block.getHash(), blockChain.getBestBlock().getHash());

        Assert.assertEquals(genesis.getHash(), blockChain.getBlockByHash(genesis.getHash()).getHash());
        Assert.assertEquals(genesis.getHash(), blockChain.getBlockByNumber(genesis.getNumber()).getHash());
        Assert.assertEquals(block.getHash(), blockChain.getBlockByHash(block.getHash()).getHash());
        Assert.assertEquals(block.getHash(), blockChain.getBlockByNumber(block.getNumber()).getHash());
    }

    @Test
    public void addSecondBlockTwice() throws IOException {
        Stores stores = new MemoryStores();
        BlockChain blockChain = new BlockChain(stores);

        Address coinbase = FactoryHelper.createRandomAddress();

        Block genesis = new Block(0, null, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
        Block block = new Block(1, genesis.getHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        Assert.assertTrue(blockChain.connectBlock(genesis));
        Assert.assertTrue(blockChain.connectBlock(block));
        Assert.assertTrue(blockChain.connectBlock(block));

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(block.getHash(), blockChain.getBestBlock().getHash());
    }

    @Test
    public void addFirstBlockTwice() throws IOException {
        Stores stores = new MemoryStores();
        BlockChain blockChain = new BlockChain(stores);

        Address coinbase = FactoryHelper.createRandomAddress();

        Block genesis = new Block(0, null, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE);
        Block block = new Block(1, genesis.getHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        Assert.assertTrue(blockChain.connectBlock(genesis));
        Assert.assertTrue(blockChain.connectBlock(block));
        Assert.assertTrue(blockChain.connectBlock(genesis));

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(block.getHash(), blockChain.getBestBlock().getHash());
    }

    @Test
    public void rejectBlockIfNotChild() throws IOException {
        Stores stores = new MemoryStores();
        BlockChain blockChain = new BlockChain(stores);

        Address coinbase = FactoryHelper.createRandomAddress();

        Block genesis = new Block(0, null, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE);
        Block block = new Block(1, FactoryHelper.createRandomBlockHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        Assert.assertTrue(blockChain.connectBlock(genesis));
        Assert.assertFalse(blockChain.connectBlock(block));

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(genesis.getHash(), blockChain.getBestBlock().getHash());
        Assert.assertNull(blockChain.getBlockByNumber(block.getNumber()));
    }

    @Test
    public void switchToABetterFork() throws IOException {
        Stores stores = new MemoryStores();
        BlockChain blockChain = new BlockChain(stores);

        Address coinbase = FactoryHelper.createRandomAddress();

        Block genesis = new Block(0, null, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE);
        Block block1 = new Block(1, genesis.getHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
        Block block1b = new Block(1, genesis.getHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
        Block block2 = new Block(2, block1b.getHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        Assert.assertTrue(blockChain.connectBlock(genesis));
        Assert.assertTrue(blockChain.connectBlock(block1));

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(block1.getHash(), blockChain.getBestBlock().getHash());

        Assert.assertTrue(blockChain.connectBlock(block1b));

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(block1.getHash(), blockChain.getBestBlock().getHash());

        Assert.assertTrue(blockChain.connectBlock(block2));

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(block2.getHash(), blockChain.getBestBlock().getHash());

        Assert.assertEquals(block1.getHash(), blockChain.getBlockByHash(block1.getHash()).getHash());
        Assert.assertEquals(block1b.getHash(), blockChain.getBlockByHash(block1b.getHash()).getHash());
        Assert.assertEquals(block2.getHash(), blockChain.getBlockByHash(block2.getHash()).getHash());

        Assert.assertEquals(block1b.getHash(), blockChain.getBlockByNumber(block1b.getNumber()).getHash());
        Assert.assertEquals(block2.getHash(), blockChain.getBlockByNumber(block2.getNumber()).getHash());
    }

    @Test
    public void switchToABetterForkWithLowerHeightButMoreTotalDifficulty() throws IOException {
        Stores stores = new MemoryStores();
        BlockChain blockChain = new BlockChain(stores);

        Address coinbase = FactoryHelper.createRandomAddress();

        Block genesis = new Block(0, null, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE);
        Block block1 = new Block(1, genesis.getHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
        Block block1b = new Block(1, genesis.getHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
        Block block1c = new Block(1, genesis.getHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
        Block block2 = new Block(2, block1.getHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
        Block block3 = new Block(3, block2.getHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        List<Transaction> transactions = Collections.emptyList();
        List<BlockHeader> uncles = new ArrayList<>();
        uncles.add(block1b.getHeader());
        uncles.add(block1c.getHeader());
        Block block2b = new Block(2, block1.getHash(), uncles, transactions, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        Assert.assertEquals(Difficulty.fromUnsignedLong(3), block2b.getCummulativeDifficulty());

        Assert.assertTrue(blockChain.connectBlock(genesis));
        Assert.assertTrue(blockChain.connectBlock(block1));
        Assert.assertTrue(blockChain.connectBlock(block2));
        Assert.assertTrue(blockChain.connectBlock(block3));

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(block3.getHash(), blockChain.getBestBlock().getHash());

        Assert.assertTrue(blockChain.connectBlock(block2b));

        Assert.assertNotNull(blockChain.getBestBlock());
        Assert.assertEquals(2, blockChain.getBestBlock().getNumber());
        Assert.assertEquals(block2b.getHash(), blockChain.getBestBlock().getHash());

        Assert.assertNull(blockChain.getBlockByNumber(3));
    }
}
