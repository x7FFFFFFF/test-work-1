package com.noname.options.server;

import com.noname.server.Extras;

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

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public EnumMap<Extras, String> getExtras() {
        return extras;
    }
}
