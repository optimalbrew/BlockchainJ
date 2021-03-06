package com.ajlopez.blockchain.json;

import java.util.Map;
import java.util.Set;

/**
 * Created by ajlopez on 27/10/2018.
 */
public class JsonObjectValue extends JsonValue {
    private Map<String, JsonValue> properties;

    public JsonObjectValue(Map<String, JsonValue> properties) {
        super(JsonValueType.OBJECT, properties);
        this.properties = properties;
    }

    public JsonValue getProperty(String name) {
        return this.properties.get(name);
    }

    public JsonValue getProperty(String name, String ...names) {
        JsonValue value = this.getProperty(name);

        for (int k = 0; k < names.length; k++)
            value = ((JsonObjectValue)value).getProperty(names[k]);

        return value;
    }

    public boolean hasProperty(String name) {
        return this.properties.containsKey(name);
    }

    public int noProperties() {
        return this.properties.size();
    }

    public Set<String> getPropertyNames() {
        return this.properties.keySet();
    }
}
