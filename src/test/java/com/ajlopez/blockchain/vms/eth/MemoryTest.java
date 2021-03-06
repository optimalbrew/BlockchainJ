package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.ByteUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 11/12/2018.
 */
public class MemoryTest {
    @Test
    public void initialSizeIsZero() {
        Memory memory = new Memory();

        Assert.assertEquals(0, memory.size());
    }

    @Test
    public void getUndefinedValueAsZero() {
        Memory memory = new Memory();

        Assert.assertEquals(DataWord.ZERO, memory.getValue(42));
    }

    @Test
    public void setAndGetValue() {
        Memory memory = new Memory();

        memory.setValue(144, DataWord.fromUnsignedInteger(42));

        Assert.assertEquals(144 + DataWord.DATAWORD_BYTES, memory.size());

        Assert.assertEquals(DataWord.fromUnsignedInteger(42), memory.getValue(144));
    }

    @Test
    public void setAndGetValueCrossingChunk() {
        Memory memory = new Memory();
        byte[] data = FactoryHelper.createRandomBytes(DataWord.DATAWORD_BYTES);
        int address = Memory.CHUNK_SIZE - 10;
        DataWord value = DataWord.fromBytes(data, 0, data.length);

        memory.setValue(address, value);

        Assert.assertEquals(address + DataWord.DATAWORD_BYTES, memory.size());

        Assert.assertEquals(value, memory.getValue(address));
    }

    @Test
    public void setAndGetTwoValues() {
        Memory memory = new Memory();

        memory.setValue(144, DataWord.fromUnsignedInteger(42));
        memory.setValue(200, DataWord.fromUnsignedInteger(100));

        Assert.assertEquals(200 + DataWord.DATAWORD_BYTES, memory.size());

        Assert.assertEquals(DataWord.fromUnsignedInteger(42), memory.getValue(144));
        Assert.assertEquals(DataWord.fromUnsignedInteger(100), memory.getValue(200));
    }

    @Test
    public void setTwoBytesAndGetValue() {
        Memory memory = new Memory();

        memory.setByte(0, (byte)0x01);
        memory.setByte(1, (byte)0x02);

        Assert.assertEquals(2, memory.size());

        Assert.assertEquals("0x0102000000000000000000000000000000000000000000000000000000000000", memory.getValue(0).toNormalizedString());
    }

    @Test
    public void setBytes() {
        Memory memory = new Memory();
        byte[] bytes = FactoryHelper.createRandomBytes(42);

        memory.setBytes(10, bytes, 2, 4);

        Assert.assertEquals(14, memory.size());

        byte[] expected = new byte[DataWord.DATAWORD_BYTES];
        System.arraycopy(bytes, 2, expected, 10, 4);
        Assert.assertArrayEquals(expected, memory.getValue(0).getBytes());
    }

    @Test
    public void setBytesCrossingChunk() {
        Memory memory = new Memory();
        byte[] bytes = FactoryHelper.createRandomBytes(42);
        int address = Memory.CHUNK_SIZE - 10;

        memory.setBytes(address, bytes, 0, 42);

        Assert.assertEquals(address + 42, memory.size());

        Assert.assertArrayEquals(bytes, memory.getBytes(address, 42));
    }

    @Test
    public void setBytesCrossingManyChunks() {
        Memory memory = new Memory();
        byte[] bytes = FactoryHelper.createRandomBytes(Memory.CHUNK_SIZE * 4 + 100);
        int address = Memory.CHUNK_SIZE - 10;

        memory.setBytes(address, bytes, 0, bytes.length);

        Assert.assertEquals(address + bytes.length, memory.size());

        Assert.assertArrayEquals(bytes, memory.getBytes(address, bytes.length));
    }

    @Test
    public void setBytesFillingWithZeros() {
        Memory memory = new Memory();
        byte[] bytes = FactoryHelper.createRandomBytes(Memory.CHUNK_SIZE * 4 + 100);
        int address = Memory.CHUNK_SIZE - 10;

        memory.setBytes(address, bytes, 0, bytes.length);
        memory.setBytes(address, new byte[] { 0x01 }, 0, bytes.length);

        byte[] result = memory.getBytes(address, bytes.length);

        Assert.assertNotNull(result);
        Assert.assertEquals(0x01, result[0]);
        Assert.assertEquals(bytes.length, result.length);
        Assert.assertTrue(ByteUtils.areZero(result, 1, result.length - 1));
    }

    @Test
    public void getNonExistentBytes() {
        Memory memory = new Memory();

        byte[] bytes = memory.getBytes(1024, 42);

        Assert.assertNotNull(bytes);
        Assert.assertEquals(42, bytes.length);
        Assert.assertTrue(ByteUtils.areZero(bytes));
    }

    @Test
    public void getBytesBeyondUsedMemory() {
        Memory memory = new Memory();
        byte[] data = FactoryHelper.createRandomBytes(42);
        memory.setBytes(0, data, 0, data.length);

        byte[] bytes = memory.getBytes(41, 42);

        Assert.assertNotNull(bytes);
        Assert.assertEquals(42, bytes.length);
        Assert.assertEquals(data[41], bytes[0]);
        Assert.assertTrue(ByteUtils.areZero(bytes, 1, bytes.length - 1));
    }
}
