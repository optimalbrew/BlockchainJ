package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 09/11/2017.
 */
public class AccountTest {
    @Test
    public void createWithZeroBalanceAndZeroNonce() {
        Account account = new Account();

        Assert.assertEquals(Coin.ZERO, account.getBalance());
        Assert.assertEquals(0, account.getNonce());
        Assert.assertNull(account.getCodeHash());
        Assert.assertNull(account.getStorageHash());
    }

    @Test
    public void createWithNullBalanceAndNonZeroNonce() {
        Account account = new Account(null, 42, null, null);

        Assert.assertEquals(Coin.ZERO, account.getBalance());
        Assert.assertEquals(42, account.getNonce());
        Assert.assertNull(account.getCodeHash());
    }

    @Test
    public void createWithCodeHash() {
        Hash codeHash = FactoryHelper.createRandomHash();
        Account account = new Account(null, 42, codeHash, null);

        Assert.assertEquals(Coin.ZERO, account.getBalance());
        Assert.assertEquals(42, account.getNonce());
        Assert.assertEquals(codeHash, account.getCodeHash());
    }

    @Test
    public void createWithStorageHash() {
        Hash storageHash = FactoryHelper.createRandomHash();
        Account account = new Account(null, 42, null, storageHash);

        Assert.assertEquals(Coin.ZERO, account.getBalance());
        Assert.assertEquals(42, account.getNonce());
        Assert.assertEquals(storageHash, account.getStorageHash());
    }

    @Test(expected = IllegalStateException.class)
    public void negativeNonce() {
        new Account(Coin.TEN, -1, null, null);
    }
}
