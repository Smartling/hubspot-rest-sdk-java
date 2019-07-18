package com.smartling.connector.hubspot.sdk.serialization;

import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class RawJsonAdapter extends TypeAdapter<String> {
    @Override
    public void write(JsonWriter out, String value) throws IOException {
        out.jsonValue(value);
    }

    @Override
    public String read(JsonReader in) throws IOException {
        return new JsonParser().parse(in).toString();
    }
}
