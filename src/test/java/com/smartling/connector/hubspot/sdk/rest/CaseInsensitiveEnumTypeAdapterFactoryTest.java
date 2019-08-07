package com.smartling.connector.hubspot.sdk.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import lombok.Data;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CaseInsensitiveEnumTypeAdapterFactoryTest
{
    private Gson gson;

    @Before
    public void setUp()
    {
        TypeAdapterFactory factory = new CaseInsensitiveEnumTypeAdapterFactory();
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(factory)
                .create();
    }

    @Test
    public void testCaseInsensitiveDeserialization() {

        MixedEnums mixedEnums = gson.fromJson(MIXED_ENUMS_JSON, MixedEnums.class);
        assertEquals(ScreamingCase.SCREAM_WITH_UNDERSCORES, mixedEnums.getScreamer());
        assertEquals(ScreamingCase.ANOTHER_SCREAMING_VALUE, mixedEnums.getScream());
        assertEquals(SnakeCase.snake_with_underscores, mixedEnums.getSnaker());
        assertEquals(SnakeCase.another_snake_value, mixedEnums.getSnake());
        assertEquals(CamelCase.camelCaseWith_Underscores, mixedEnums.getCamel());
        assertEquals(CamelCase.AnotherCamel_Value, mixedEnums.getTwoHumpCamel());
    }

    private static String MIXED_ENUMS_JSON = "" +
            "{" +
                "\"screamer\": \"scream_with_underscores\"," +
                "\"scream\": \"Another_Screaming_Value\"," +
                "\"snaker\": \"SNAKE_WITH_UNDERSCORES\"," +
                "\"snake\": \"anOtheR_sNakE_vaLuE\"," +
                "\"camel\": \"camelcasewith_underscores\"," +
                "\"twoHumpCamel\": \"anotHeRcamel_vaLue\"" +
            "}";

    @Data
    static class MixedEnums {
        private ScreamingCase screamer;
        private ScreamingCase scream;
        private SnakeCase snaker;
        private SnakeCase snake;
        private CamelCase camel;
        private CamelCase twoHumpCamel;
    }

    enum ScreamingCase {
        SCREAM_WITH_UNDERSCORES, ANOTHER_SCREAMING_VALUE
    }

    enum SnakeCase {
        snake_with_underscores, another_snake_value
    }

    enum CamelCase {
        camelCaseWith_Underscores, AnotherCamel_Value
    }
}
