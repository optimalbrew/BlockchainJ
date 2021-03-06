package com.ajlopez.blockchain.json;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 03/11/2018.
 */
public class JsonArrayBuilder extends JsonBuilder {
    private final JsonBuilder parent;
    private final List<JsonValue> elements = new ArrayList<>();

    public JsonArrayBuilder(JsonBuilder parent) {
        this.parent = parent;
    }

    @Override
    public JsonBuilder value(int value) {
        super.value(value);
        elements.add(super.build());

        return this;
    }

    @Override
    public JsonBuilder value(boolean value) {
        super.value(value);
        elements.add(super.build());

        return this;
    }

    @Override
    public JsonBuilder value(String value) {
        super.value(value);
        elements.add(super.build());

        return this;
    }

    @Override
    public JsonBuilder value(Object value) {
        return this.value(value.toString());
    }

    @Override
    public JsonValue build() {
        return new JsonArrayValue(this.elements);
    }

    @Override
    public JsonBuilder end() {
        parent.value(this.build());

        return parent;
    }
}
