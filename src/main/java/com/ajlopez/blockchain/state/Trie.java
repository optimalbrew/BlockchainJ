package com.ajlopez.blockchain.state;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.store.TrieStore;
import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HashUtils;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class Trie {
    public static final Hash EMPTY_TRIE_HASH = new Trie().getHash();
    public static final int ARITY = 16;

    private byte[] value;
    private Trie[] nodes;
    private Hash[] hashes;
    private byte[] sharedKey;
    private int sharedKeyLength;
    private TrieStore store;

    private Hash hash;
    private byte[] encoded;
    private boolean saved;

    public Trie() {}

    public Trie(TrieStore store) {
        this(null, null, null, null, 0, store);
    }

    private Trie(Trie[] nodes, Hash[] hashes, byte[] value, byte[] sharedKey, int sharedKeyLength, TrieStore store) {
        this.nodes = nodes;
        this.hashes = hashes;
        this.value = value;
        this.sharedKey = sharedKey;
        this.sharedKeyLength = sharedKeyLength;
        this.store = store;
    }

    // TODO remove or visibility for testing
    public int nodesSize() {
        int count = 1;

        if (this.nodes != null)
            for (Trie node : this.nodes)
                if (node != null)
                    count += node.nodesSize();

        return count;
    }

    public Hash[] getSubHashes() {
        if (this.hashes == null)
            this.hashes = new Hash[Trie.ARITY];

        if (this.nodes != null)
            for (int k = 0; k < Trie.ARITY; k++)
                if (this.hashes[k] == null && this.nodes[k] != null)
                    this.hashes[k] = this.getSubHash(k);

        Hash[] result = new Hash[Trie.ARITY];

        System.arraycopy(this.hashes, 0, result, 0, Trie.ARITY);

        return result;
    }

    public TriePath getPath(byte[] key) throws IOException {
        TriePath path = new TriePath();

        if (this.buildPath(path, key, 0))
            return path;

        return new TriePath();
    }

    private boolean buildPath(TriePath path, byte[] key, int position) throws IOException {
        int sharedLength = TrieKeyUtils.getSharedLength(this.sharedKey, this.sharedKeyLength, key, position);

        if (sharedLength < this.sharedKeyLength)
            return false;

        position += sharedLength;

        if (position == key.length * 2) {
            path.addLastTrie(this);

            return true;
        }

        int nibble = TrieKeyUtils.getOffset(key, position);

        Trie trie = this.getSubNode(nibble);

        if (trie == null)
            return false;

        path.addTrieAndChildPosition(this, nibble);

        return trie.buildPath(path, key, position + 1);
    }

    public byte[] get(byte[] key) throws IOException {
        return this.get(key, 0);
    }

    public byte[] getValue() {
        if (this.value == null)
            return null;

        return Arrays.copyOf(this.value, this.value.length);
    }

    private byte[] get(byte[] key, int position) throws IOException {
        int sharedLength = TrieKeyUtils.getSharedLength(this.sharedKey, this.sharedKeyLength, key, position);

        if (sharedLength < this.sharedKeyLength)
            return null;

        position += sharedLength;

        if (position == key.length * 2)
            return this.value;

        int nibble = TrieKeyUtils.getOffset(key, position);

        Trie trie = this.getSubNode(nibble);

        if (trie == null)
            return null;

        return trie.get(key, position + 1);
    }

    public Trie put(byte[] key, byte[] value) throws IOException {
        Trie trie = this.put(key, 0, value);

        if (trie == null)
            return new Trie(this.store);

        return trie;
    }

    public Trie delete(byte[] key) throws IOException {
        return this.put(key, null);
    }

    public Hash getHash() {
        if (this.hash != null)
            return this.hash;

        this.hash = HashUtils.calculateHash(this.getEncoded());

        return this.hash;
    }

    public byte[] getEncoded() {
        if (this.encoded != null)
            return this.encoded;

        int valsizebytes = 0;
        int valbytes = 0;

        if (this.value != null && this.value.length > 0) {
            valsizebytes = Integer.BYTES;
            valbytes = this.value.length;
        }

        int sksizebytes = 0;
        int skbytes = 0;

        if (this.sharedKeyLength > 0) {
            sksizebytes = Short.BYTES;
            skbytes = this.sharedKey.length;
        }

        int nsubnodes = this.getSubNodesCount();

        int prefixLength = 1 + 1 + 1 + 1;
        int hashBitsOffset = prefixLength;
        int hashesOffset = hashBitsOffset + Short.BYTES;
        int valueSizeOffset = hashesOffset + Hash.HASH_BYTES * nsubnodes;
        int valueOffset = valueSizeOffset + valsizebytes;
        int skSizeOffset = valueOffset + valbytes;
        int skOffset = skSizeOffset + sksizebytes;
        int bsize = skOffset + skbytes;

        byte[] bytes = new byte[bsize];

        getSubNodes(bytes, 1 + 1 + 1 + 1);

        // byte[0] version == 0

        // arity
        bytes[1] = ARITY;

        // value size

        bytes[2] = (byte)valsizebytes;

        // shared key size

        bytes[3] = (byte)sksizebytes;

        // value encoding

        if (valsizebytes > 0) {
            System.arraycopy(ByteUtils.unsignedIntegerToBytes(valbytes), 0, bytes, valueSizeOffset, valsizebytes);
            System.arraycopy(this.value, 0, bytes, valueOffset, valbytes);
        }

        // shared key encoding

        if (sksizebytes > 0) {
            System.arraycopy(ByteUtils.unsignedShortToBytes(this.sharedKeyLength), 0, bytes, skSizeOffset, sksizebytes);
            System.arraycopy(this.sharedKey, 0, bytes, skOffset, skbytes);
        }

        // subnodes hashes

        this.encoded = bytes;

        return this.encoded;
    }

    public void save() throws IOException {
        if (this.saved)
            return;

        // TODO review
        if (this.store != null)
            this.store.save(this);

        this.saved = true;

        if (this.nodes == null)
            return;

        for (int k = 0; k < ARITY; k++) {
            Trie node = this.nodes[k];

            if (node != null)
                node.save();
        }
    }

    public static Trie fromEncoded(byte[] bytes, TrieStore store) {
        short valsizebytes = bytes[2];
        short sksizebytes = bytes[3];

        int subnodes = ByteUtils.bytesToUnsignedShort(bytes, 4);

        if (subnodes == 0 && valsizebytes == 0 && sksizebytes == 0)
            return new Trie(store);

        Hash[] hashes = new Hash[ARITY];
        int h = 0;

        for (int k = 0; k < ARITY; k++) {
            if ((subnodes & (1 << k)) == 0)
                continue;

            byte[] bhash = new byte[HashUtils.HASH_BYTES];
            System.arraycopy(bytes, 4 + Short.BYTES + HashUtils.HASH_BYTES * h, bhash, 0, HashUtils.HASH_BYTES);
            hashes[k] = new Hash(bhash);

            h++;
        }

        byte[] value = null;
        int lvalue = 0;
        byte[] sharedKey = null;
        int sharedKeyLength = 0;

        if (valsizebytes > 0) {
            lvalue = ByteUtils.bytesToUnsignedInteger(bytes, 4 + Short.BYTES + HashUtils.HASH_BYTES * h);

            value = new byte[lvalue];
            System.arraycopy(bytes, 4 + Short.BYTES + HashUtils.HASH_BYTES * h + valsizebytes, value, 0, lvalue);
        }

        if (sksizebytes > 0) {
            sharedKeyLength = ByteUtils.bytesToUnsignedShort(bytes, 4 + Short.BYTES + HashUtils.HASH_BYTES * h + valsizebytes + lvalue);

            sharedKey = new byte[(sharedKeyLength + 1) / 2];
            System.arraycopy(bytes, 4 + Short.BYTES + HashUtils.HASH_BYTES * h  + valsizebytes + lvalue + sksizebytes, sharedKey, 0, sharedKey.length);
        }

        return new Trie(null, hashes, value, sharedKey, sharedKeyLength, store);
    }

    private void getSubNodes(byte[] bytes, int offset) {
        int subnodes = 0;
        int nsubnode = 0;

        if (this.nodes != null || this.hashes != null)
            for (int k = 0; k < ARITY; k++) {
                Hash subhash = this.getSubHash(k);

                if (subhash == null)
                    continue;

                subnodes |= 1 << k;
                System.arraycopy(subhash.getBytes(), 0, bytes, offset + Short.BYTES + HashUtils.HASH_BYTES * nsubnode, HashUtils.HASH_BYTES);
                nsubnode++;
            }

        byte[] subnodesbits = ByteUtils.unsignedShortToBytes(subnodes);

        System.arraycopy(subnodesbits, 0, bytes, offset, subnodesbits.length);
    }

    private Hash getSubHash(int k) {
        Hash hash = this.getSubHashFromHashes(k);

        if (hash != null)
            return hash;

        hash = this.getSubHashFromNodes(k);

        if (hash == null)
            return null;

        this.setSubHash(k, hash);

        return hash;
    }

    private Hash getSubHashFromHashes(int k) {
        if (this.hashes == null)
            return null;

        return this.hashes[k];
    }

    private void setSubHash(int k, Hash hash) {
        if (this.hashes == null)
            this.hashes = new Hash[ARITY];

        this.hashes[k] = hash;
    }

    private Hash getSubHashFromNodes(int k) {
        if (this.nodes == null)
            return null;

        Trie node = this.nodes[k];

        if (node == null)
            return null;

        return node.getHash();
    }

    private Trie getSubNode(int k) throws IOException {
        if (this.nodes != null && this.nodes[k] != null)
            return this.nodes[k];

        if (this.hashes == null)
            return null;

        Hash hash = this.hashes[k];

        if (hash == null)
            return null;

        Trie trie = this.store.retrieve(hash);

        this.nodes = new Trie[ARITY];
        this.nodes[k] = trie;

        return trie;
    }

    private int getSubNodesCount() {
        boolean hasnodes = this.nodes != null;
        boolean hashashes = this.hashes != null;

        int nsubnodes = 0;

        for (int k = 0; k < ARITY; k++)
            if ((hasnodes && this.nodes[k] != null) || (hashashes && this.hashes[k] != null))
                nsubnodes++;

        return nsubnodes;
    }

    private Trie put(byte[] key, int position, byte[] value) throws IOException {
        int sharedLength = TrieKeyUtils.getSharedLength(this.sharedKey, this.sharedKeyLength, key, position);

        if (sharedLength < this.sharedKeyLength)
            if (value == null)
                return this;
            else
                return this.split(sharedLength).put(key, position, value);

        if (position + sharedLength == key.length * 2)
            if (Arrays.equals(value, this.value))
                return this;
            else
                return createNewTrie(copyNodes(this.nodes, false), copyHashes(this.hashes, false), value, sharedKey, sharedKeyLength, this.store, true);


        int offset = TrieKeyUtils.getOffset(key, position + sharedLength);

        Trie[] childNodes = copyNodes(this.nodes, true);
        Hash[] childHashes = copyHashes(this.hashes, true);

        Trie childNode = this.getSubNode(offset);

        if (childNode == null) {
            if (value == null)
                return this;

            childNodes[offset] = new Trie(this.store).put(key, position + sharedLength + 1, value);
            childHashes[offset] = null;
        }
        else {
            Trie newChildNode = childNode.put(key, position + sharedLength + 1, value);

            if (newChildNode == childNode)
                return this;

            childNodes[offset] = newChildNode;
            childHashes[offset] = null;
        }

        return createNewTrie(childNodes, childHashes, this.value, this.sharedKey, this.sharedKeyLength, this.store, true);
    }

    private Trie split(int sharedLength) throws IOException {
        Trie splitChild = createNewTrie(copyNodes(this.nodes, false), copyHashes(this.hashes, false), value, TrieKeyUtils.getSubKey(this.sharedKey, sharedLength + 1, sharedKeyLength - sharedLength - 1), sharedKeyLength - sharedLength - 1, this.store, true);
        int offset = TrieKeyUtils.getOffset(this.sharedKey, sharedLength);

        Trie[] newNodes = new Trie[Trie.ARITY];

        newNodes[offset] = splitChild;

        return createNewTrie(newNodes, null, null, TrieKeyUtils.getSubKey(this.sharedKey, 0, sharedLength), sharedLength, this.store, false);
    }

    private static Trie createNewTrie(Trie[] nodes, Hash[] hashes, byte[] value, byte[] sharedKey, int sharedKeyLength, TrieStore store, boolean tryCoalesce) throws IOException {
        if (emptyNodes(nodes))
            nodes = null;

        if (emptyHashes(hashes))
            hashes = null;

        if (sharedKeyLength == 0)
            sharedKey = null;

        if (value == null && nodes == null && hashes == null)
            return null;

        Trie trie = new Trie(nodes, hashes, value, sharedKey, sharedKeyLength, store);

        if (!tryCoalesce || !trie.canCoalesce())
            return trie;

        return trie.coalesce();
    }

    private Trie coalesce() throws IOException {
        int firstChildOffset = this.getFirstChildOffset();
        Trie firstChild = this.getSubNode(firstChildOffset);

        int newSharedKeyLength = TrieKeyUtils.concatenateKeysLength(this.sharedKeyLength, firstChild.sharedKeyLength);
        byte[] newSharedKey = TrieKeyUtils.concatenateKeys(this.sharedKey, this.sharedKeyLength, firstChildOffset, firstChild.sharedKey, firstChild.sharedKeyLength);

        Trie[] newNodes = copyNodes(firstChild.nodes, false);
        Hash[] newHashes = copyHashes(firstChild.hashes, false);

        return createNewTrie(newNodes, newHashes, firstChild.value, newSharedKey, newSharedKeyLength, this.store, true);
    }

    private int getFirstChildOffset() {
        for (int k = 0; k < Trie.ARITY; k++)
            if (this.nodes != null && this.nodes[k] != null)
                return k;
            else if (this.hashes != null && this.hashes[k] != null)
                return k;

        return -1;
    }

    private boolean canCoalesce() {
        if (value != null)
            return false;

        int nchildren = 0;

        for (int k = 0; k < Trie.ARITY; k++)
            if (this.nodes != null && this.nodes[k] != null)
                nchildren++;
            else if (this.hashes != null && this.hashes[k] != null)
                nchildren++;

        return nchildren == 1;
    }

    private static boolean emptyNodes(Trie[] nodes) {
        if (nodes == null)
            return true;

        for (int k = 0; k < nodes.length; k++)
            if (nodes[k] != null)
                return false;

        return true;
    }

    private static boolean emptyHashes(Hash[] hashes) {
        if (hashes == null)
            return true;

        for (int k = 0; k < hashes.length; k++)
            if (hashes[k] != null)
                return false;

        return true;
    }

    private static Trie[] copyNodes(Trie[] nodes, boolean create) {
        if (nodes == null)
            return create ? new Trie[ARITY] : null;

        return Arrays.copyOf(nodes, ARITY);
    }

    private static Hash[] copyHashes(Hash[] hashes, boolean create) {
        if (hashes == null)
            return create ? new Hash[ARITY] : null;

        return Arrays.copyOf(hashes, ARITY);
    }
}
