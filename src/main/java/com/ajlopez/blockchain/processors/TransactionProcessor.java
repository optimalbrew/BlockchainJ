package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Transaction;

import java.util.List;

/**
 * Created by ajlopez on 27/01/2018.
 */
public class TransactionProcessor {
    TransactionPool transactionPool;

    public TransactionProcessor(TransactionPool transactionPool) {
        this.transactionPool = transactionPool;
    }

    public List<Transaction> processTransaction(Transaction transaction) {
        // TODO add transaction validation
        return this.transactionPool.addTransaction(transaction);
    }
}
