package com.nulp.project2.util;

import java.util.UUID;

public final class GenerateSimpleTokenUtils {
    public static String getToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private GenerateSimpleTokenUtils() {
    }
}
