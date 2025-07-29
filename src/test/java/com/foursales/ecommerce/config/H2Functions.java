package com.foursales.ecommerce.config;

import java.nio.ByteBuffer;
import java.util.UUID;

public class H2Functions {

    public static String binToUuid(byte[] bytes) {
        if (bytes == null || bytes.length != 16) return null;
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long high = bb.getLong();
        long low = bb.getLong();
        return new UUID(high, low).toString();
    }
}
