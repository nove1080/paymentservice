package com.ordertogether.paymentservice.common.util;

import java.util.UUID;
import lombok.experimental.UtilityClass;

@UtilityClass
public class IdempotencyKeyGenerator {

    public static String generate(String seed) {
        return UUID.nameUUIDFromBytes(seed.getBytes()).toString();
    }

}
