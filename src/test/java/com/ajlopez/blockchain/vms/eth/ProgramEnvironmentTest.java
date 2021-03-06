package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 14/12/2018.
 */
public class ProgramEnvironmentTest {
    @Test
    public void createEnvironment() {
        Address address = FactoryHelper.createRandomAddress();
        Address origin = FactoryHelper.createRandomAddress();
        Address caller = FactoryHelper.createRandomAddress();

        MessageData messageData = new MessageData(address, origin, caller, Coin.ONE, 0, null, null, false);

        long number = 1;
        long timestamp = 2;
        Address coinbase = FactoryHelper.createRandomAddress();

        BlockData blockData = new BlockData(number, timestamp, coinbase, null);

        ProgramEnvironment environment = new ProgramEnvironment(messageData, blockData, null);

        Assert.assertEquals(address, environment.getAddress());
        Assert.assertEquals(origin, environment.getOrigin());
        Assert.assertEquals(caller, environment.getCaller());
        Assert.assertEquals(Coin.ONE, environment.getValue());
        Assert.assertEquals(coinbase, environment.getCoinbase());
        Assert.assertEquals(number, environment.getNumber());
        Assert.assertEquals(timestamp, environment.getTimestamp());
    }
}
