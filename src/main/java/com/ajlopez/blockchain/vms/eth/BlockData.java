package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.core.types.Difficulty;

/**
 * Created by ajlopez on 21/12/2018.
 */
public class BlockData {
    private final long number;
    private final long timestamp;
    private final Address coinbase;
    private final Difficulty difficulty;

    public BlockData(long number, long timestamp, Address coinbase, Difficulty difficulty) {
        this.number = number;
        this.timestamp = timestamp;
        this.coinbase = coinbase;
        this.difficulty = difficulty;
    }

    public long getNumber() { return this.number; }

    public long getTimestamp() { return this.timestamp; }

    public Address getCoinbase() { return this.coinbase; }

    public Difficulty getDifficulty() { return this.difficulty; }
}
