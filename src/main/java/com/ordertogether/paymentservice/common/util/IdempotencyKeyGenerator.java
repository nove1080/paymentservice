package com.ordertogether.paymentservice.common.util;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.experimental.UtilityClass;

@UtilityClass
public class IdempotencyKeyGenerator {

    public static String generate(String seed) {
        return UUID.nameUUIDFromBytes(seed.getBytes(StandardCharsets.UTF_8)).toString();
    }

}
