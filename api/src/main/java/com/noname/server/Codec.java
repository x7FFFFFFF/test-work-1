package com.noname.server;

public interface Codec {
    Object decode(byte[] bytes) throws Exception;
    byte[] encode(Object obj) throws Exception;
}
