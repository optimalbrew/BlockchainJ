package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ajlopez on 21/01/2018.
 */
public class TransactionPoolTest {
    @Test
    public void noTransactions() {
        TransactionPool pool = new TransactionPool();

        List<Transaction> result = pool.getTransactions();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void noTransactionsWithSender() {
        Address sender = FactoryHelper.createRandomAddress();
        TransactionPool pool = new TransactionPool();

        List<Transaction> result = pool.getTransactionsWithSender(sender);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void addTransaction() {
        TransactionPool pool = new TransactionPool();
        Transaction transaction = FactoryHelper.createTransaction(100);

        List<Transaction> added = pool.addTransaction(transaction);

        Assert.assertNotNull(added);
        Assert.assertFalse(added.isEmpty());
        Assert.assertEquals(1, added.size());
        Assert.assertSame(transaction, added.get(0));

        List<Transaction> result = pool.getTransactions();

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(transaction, result.get(0));
    }

    @Test
    public void transactionWithSender() {
        TransactionPool pool = new TransactionPool();
        Transaction transaction = FactoryHelper.createTransaction(100);
        Transaction otherTransaction = FactoryHelper.createTransaction(2000);

        pool.addTransaction(transaction);
        pool.addTransaction(otherTransaction);

        List<Transaction> result = pool.getTransactionsWithSender(transaction.getSender());

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(transaction, result.get(0));
    }

    @Test
    public void transactionsWithSender() {
        TransactionPool pool = new TransactionPool();
        Transaction transaction = FactoryHelper.createTransaction(100);
        Transaction otherTransaction = FactoryHelper.createTransaction(2000);
        Transaction transaction2 = transaction.withNonce(transaction.getNonce() + 1);

        pool.addTransaction(transaction);
        pool.addTransaction(otherTransaction);
        pool.addTransaction(transaction2);

        List<Transaction> result = pool.getTransactionsWithSender(transaction.getSender());

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains(transaction));
        Assert.assertTrue(result.contains(transaction2));
    }

    @Test
    public void transactionsWithSenderFromNonce() {
        TransactionPool pool = new TransactionPool();
        Transaction transaction = FactoryHelper.createTransaction(100);
        Transaction transaction2 = transaction.withNonce(transaction.getNonce() + 1);
        Transaction transaction3 = transaction.withNonce(transaction.getNonce() + 2);
        Transaction transaction4 = transaction.withNonce(transaction.getNonce() + 4);
        Transaction otherTransaction = FactoryHelper.createTransaction(2000);

        pool.addTransaction(transaction);
        pool.addTransaction(otherTransaction);
        pool.addTransaction(transaction4);
        pool.addTransaction(transaction3);
        pool.addTransaction(transaction2);

        List<Transaction> result = pool.getTransactionsWithSenderFromNonce(transaction.getSender(), transaction.getNonce() + 1);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains(transaction2));
        Assert.assertTrue(result.contains(transaction3));
        Assert.assertEquals(transaction2, result.get(0));
        Assert.assertEquals(transaction3, result.get(1));
    }

    @Test
    public void transactionsWithSenderFromNonceWhenTransactionsHasRepeatedNonce() {
        Address sender = FactoryHelper.createRandomAddress();
        TransactionPool pool = new TransactionPool();
        Transaction transaction = FactoryHelper.createTransaction(100, sender, 0);
        Transaction transaction2 = transaction.withNonce(transaction.getNonce() + 1);
        Transaction transaction2b = FactoryHelper.createTransaction(200, sender, transaction.getNonce() + 1);
        Transaction transaction3 = transaction.withNonce(transaction.getNonce() + 2);
        Transaction transaction4 = transaction.withNonce(transaction.getNonce() + 4);
        Transaction otherTransaction = FactoryHelper.createTransaction(2000);

        pool.addTransaction(transaction);
        pool.addTransaction(otherTransaction);
        pool.addTransaction(transaction4);
        pool.addTransaction(transaction3);
        pool.addTransaction(transaction2);
        pool.addTransaction(transaction2b);

        List<Transaction> result = pool.getTransactionsWithSenderFromNonce(transaction.getSender(), transaction.getNonce() + 1);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(3, result.size());
        Assert.assertTrue(result.contains(transaction2));
        Assert.assertTrue(result.contains(transaction2b));
        Assert.assertTrue(result.contains(transaction3));
    }

    @Test
    public void addAndRemoveTransaction() {
        TransactionPool pool = new TransactionPool();
        Transaction transaction = FactoryHelper.createTransaction(100);

        pool.addTransaction(transaction);
        pool.removeTransaction(transaction);

        List<Transaction> result = pool.getTransactions();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void addAndRemoveTwiceATransaction() {
        TransactionPool pool = new TransactionPool();
        Transaction transaction = FactoryHelper.createTransaction(100);

        pool.addTransaction(transaction);
        pool.removeTransaction(transaction);
        pool.removeTransaction(transaction);

        List<Transaction> result = pool.getTransactions();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void removeUnknownTransaction() {
        TransactionPool pool = new TransactionPool();
        Transaction transaction = FactoryHelper.createTransaction(100);

        pool.removeTransaction(transaction);

        List<Transaction> result = pool.getTransactions();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void addSameTransactionTwice() {
        TransactionPool pool = new TransactionPool();
        Transaction transaction = FactoryHelper.createTransaction(100);

        pool.addTransaction(transaction);
        List<Transaction> added = pool.addTransaction(transaction);

        Assert.assertNotNull(added);
        Assert.assertTrue(added.isEmpty());

        List<Transaction> result = pool.getTransactions();

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(transaction, result.get(0));
    }

    @Test
    public void addTransactionTwice() {
        TransactionPool pool = new TransactionPool();
        Transaction transaction = FactoryHelper.createTransaction(100);
        Transaction transaction2 = new Transaction(transaction.getSender(), transaction.getReceiver(), transaction.getValue(), transaction.getNonce(), transaction.getData(), transaction.getGas(), transaction.getGasPrice());

        pool.addTransaction(transaction);
        List<Transaction> added = pool.addTransaction(transaction2);

        Assert.assertNotNull(added);
        Assert.assertTrue(added.isEmpty());

        List<Transaction> result = pool.getTransactions();

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(transaction, result.get(0));
    }

    @Test
    public void addTransactionGetListAddTransaction() {
        TransactionPool pool = new TransactionPool();
        Transaction transaction1 = FactoryHelper.createTransaction(100);
        Transaction transaction2 = FactoryHelper.createTransaction(200);

        pool.addTransaction(transaction1);
        List<Transaction> result = pool.getTransactions();
        pool.addTransaction(transaction2);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(transaction1, result.get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNullTransaction() {
        TransactionPool pool = new TransactionPool();

        pool.addTransaction(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullTransaction() {
        TransactionPool pool = new TransactionPool();

        pool.removeTransaction(null);
    }

    @Test
    public void nonceWhenNoTransaction() {
        TransactionPool transactionPool = new TransactionPool();

        Assert.assertEquals(42, transactionPool.getTransactionNonceBySenderFromNonce(FactoryHelper.createRandomAddress(), 42));
    }

    @Test
    public void nonceWhenTransactions() {
        TransactionPool transactionPool = new TransactionPool();
        Transaction transaction = FactoryHelper.createTransaction(100);
        Transaction transaction2 = transaction.withNonce(transaction.getNonce() + 1);
        Transaction transaction3 = transaction.withNonce(transaction.getNonce() + 2);
        Transaction transaction4 = transaction.withNonce(transaction.getNonce() + 4);
        Transaction otherTransaction = FactoryHelper.createTransaction(2000);

        transactionPool.addTransaction(transaction);
        transactionPool.addTransaction(otherTransaction);
        transactionPool.addTransaction(transaction4);
        transactionPool.addTransaction(transaction3);
        transactionPool.addTransaction(transaction2);

        Assert.assertEquals(transaction.getNonce() + 3, transactionPool.getTransactionNonceBySenderFromNonce(transaction.getSender(), transaction.getNonce() + 1));
        Assert.assertEquals(transaction.getNonce() + 5, transactionPool.getTransactionNonceBySenderFromNonce(transaction.getSender(), transaction.getNonce() + 4));
        Assert.assertEquals(transaction.getNonce() + 42, transactionPool.getTransactionNonceBySenderFromNonce(transaction.getSender(), transaction.getNonce() + 42));
    }

    @Test
    public void updateTransactions() {
        TransactionPool pool = new TransactionPool();
        Transaction transaction1 = FactoryHelper.createTransaction(100);
        Transaction transaction2 = FactoryHelper.createTransaction(200);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction1);
        transactions.add(transaction2);

        pool.updateTransactions(Collections.emptyList(), transactions);
        List<Transaction> result = pool.getTransactions();
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains(transaction1));
        Assert.assertTrue(result.contains(transaction2));

        pool.updateTransactions(transactions, transactions);
        List<Transaction> result2 = pool.getTransactions();
        Assert.assertNotNull(result2);
        Assert.assertFalse(result2.isEmpty());
        Assert.assertEquals(2, result2.size());
        Assert.assertTrue(result2.contains(transaction1));
        Assert.assertTrue(result2.contains(transaction2));

        pool.updateTransactions(transactions, Collections.emptyList());
        List<Transaction> result3 = pool.getTransactions();
        Assert.assertNotNull(result3);
        Assert.assertTrue(result3.isEmpty());
    }
}
