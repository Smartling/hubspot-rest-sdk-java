package com.smartling.connector.hubspot.sdk.rest;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CaseInsensitiveEnumTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (gson == null || type == null) {
            return null;
        }
        Class<T> rawType = (Class<T>) type.getRawType();
        if (!rawType.isEnum()) {
            return null;
        }
        return (TypeAdapter<T>) new EnumTypeAdapter(rawType);
    }
    private static String toLowercase(Object o) {
        return o.toString().toLowerCase(Locale.US);
    }
    // TT is also template class, because T was usage so I cannot reuse T as template
    private static final class EnumTypeAdapter<TT extends Enum<TT>> extends TypeAdapter<TT> {
        private final Map<String, TT> nameToConstant = new HashMap<>();
        private final Map<TT, String> constantToName = new HashMap<>();
        EnumTypeAdapter(Class<TT> classOfT) {
            for (TT constant : classOfT.getEnumConstants()) {
                String name = constant.name();
                SerializedName serializedName;
                try {
                    serializedName = classOfT.getField(name).getAnnotation(SerializedName.class);
                } catch (NoSuchFieldException e) {
                    serializedName = null;
                }

                if (serializedName == null) {
                    name = toLowercase(constant);
                } else {
                    name = serializedName.value();
                    for (String alternate : serializedName.alternate()) {
                        nameToConstant.put(alternate, constant);
                    }
                }
                nameToConstant.put(name, constant);
                constantToName.put(constant, name);
            }
        }
        @Override
        public TT read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return nameToConstant.get(toLowercase(in.nextString()));
        }
        @Override
        public void write(JsonWriter out, TT value) throws IOException {
            out.value(value == null ? null : constantToName.get(value));
        }
    }
}
