package com.ajlopez.blockchain.storage;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.store.KeyValueStore;

import java.io.IOException;

/**
 * Created by ajlopez on 13/08/2019.
 */
public class ChunkStore {
    private final KeyValueStore store;

    public ChunkStore(KeyValueStore store) {
        this.store = store;
    }

    public Chunk getChunk(Hash hash) throws IOException {
        byte[] data = this.store.getValue(hash.getBytes());

        if (data == null)
            return null;

        return new Chunk(data);
    }

    public void saveChunk(Chunk chunk) throws IOException {
        this.store.setValue(chunk.getHash().getBytes(), chunk.getData());
    }
}
