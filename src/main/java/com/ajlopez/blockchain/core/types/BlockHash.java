package com.ajlopez.blockchain.core.types;

/**
 * Created by ajlopez on 22/03/2018.
 */
public class BlockHash extends Hash {
    public static BlockHash EMPTY_BLOCK_HASH = new BlockHash(new byte[HASH_BYTES]);

    public BlockHash(byte[] bytes) {
        super(bytes);
    }
}
