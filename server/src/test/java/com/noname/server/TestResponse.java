package com.noname.server;

import java.util.EnumMap;

public class TestResponse {
    private int resultCode;
    private final EnumMap<Extras, String> extras = new EnumMap<>(Extras.class);

    public TestResponse(int resultCode, EnumMap<Extras, String> extras) {
        this.resultCode = resultCode;
        this.extras.putAll(extras);
    }

    public TestResponse() {
    }

    public int getResultCode() {
        return resultCode;
    }

    public EnumMap<Extras, String> getExtras() {
        return extras;
    }
}
