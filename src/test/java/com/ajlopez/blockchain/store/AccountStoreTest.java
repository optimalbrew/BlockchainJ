package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.encoding.AccountEncoder;
import com.ajlopez.blockchain.state.Trie;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ajlopez on 26/11/2018.
 */
public class AccountStoreTest {
    @Test
    public void getUnknownAccount() throws IOException {
        AccountStore store = new AccountStore(new Trie());
        Address address = new Address(new byte[] { 0x01, 0x02 });

        Account result = store.getAccount(address);

        Assert.assertNotNull(result);
        Assert.assertEquals(Coin.ZERO, result.getBalance());
        Assert.assertEquals(0, result.getNonce());
    }

    @Test
    public void putAndGetAccount() throws IOException {
        AccountStore store = new AccountStore(new Trie());
        Account account = new Account(Coin.TEN, 42, null, null);
        Address address = new Address(new byte[] { 0x01, 0x02 });

        store.putAccount(address, account);

        Account result = store.getAccount(address);

        Assert.assertNotNull(result);
        Assert.assertEquals(Coin.TEN, result.getBalance());
        Assert.assertEquals(42, result.getNonce());

        Assert.assertArrayEquals(AccountEncoder.encode(account), AccountEncoder.encode(result));
    }
}
