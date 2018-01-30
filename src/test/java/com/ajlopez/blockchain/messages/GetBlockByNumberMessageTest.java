package com.ajlopez.blockchain.messages;

import com.ajlopez.blockchain.core.Hash;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 30/01/2018.
 */
public class GetBlockByNumberMessageTest {
    @Test
    public void createWithNumber() {
        GetBlockByNumberMessage message = new GetBlockByNumberMessage(12345678L);

        Assert.assertEquals(MessageType.GET_BLOCK_BY_NUMBER, message.getMessageType());
        Assert.assertEquals(12345678L, message.getNumber());
    }
}