package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 21/12/2018.
 */
public class BlockDataTest {
    @Test
    public void simpleCreation() {
        long number = 1;
        long timestamp = 2;
        Address coinbase = FactoryHelper.createRandomAddress();
        Difficulty difficulty = Difficulty.ONE;

        BlockData blockData = new BlockData(number, timestamp, coinbase, difficulty);

        Assert.assertEquals(number, blockData.getNumber());
        Assert.assertEquals(timestamp, blockData.getTimestamp());
        Assert.assertEquals(coinbase, blockData.getCoinbase());
        Assert.assertEquals(difficulty, blockData.getDifficulty());
    }
}
