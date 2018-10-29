package com.ajlopez.blockchain.json;

import java.util.List;

/**
 * Created by ajlopez on 29/10/2018.
 */
public class ArrayValue extends Value {
    private List<Value> values;

    public ArrayValue(List<Value> values) {
        super(ValueType.ARRAY, values);
        this.values = values;
    }

    public int size() {
        return this.values.size();
    }

    public Value getValue(int index) {
        return this.values.get(index);
    }
}