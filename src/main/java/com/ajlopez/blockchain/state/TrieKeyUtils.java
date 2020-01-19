package com.ajlopez.blockchain.state;

/**
 * Created by ajlopez on 18/01/2020.
 */
public class TrieKeyUtils {
    public static int getOffset(byte[] key, int position) {
        if (position % 2 == 0)
            return (key[position / 2] >> 4) & 0x0f;

        return key[position / 2] & 0x0f;
    }

    public static byte[] getSubKey(byte[] key, int from, int length) {
        byte[] subkey = new byte[(length + 1) / 2];

        if (from % 2 == 0)
            for (int k = 0; k < length / 2; k++)
                subkey[k] = key[from / 2 + k];
        else
            for (int k = 0; k < length; k++) {
                int position = from + k;
                int offset = getOffset(key, position);

                if (k % 2 == 0)
                    subkey[k / 2] = (byte)(offset << 4);
                else
                    subkey[k / 2] |= (byte)offset;
            }

        if (length % 2 == 1)
            subkey[subkey.length - 1] = (byte)(getOffset(key, from + length - 1) << 4);

        return subkey;
    }
}
