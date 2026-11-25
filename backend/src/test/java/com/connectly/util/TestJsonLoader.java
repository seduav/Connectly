package com.connectly.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class TestJsonLoader {

    private TestJsonLoader() {
    }

    public static String load(String path) {

        try (InputStream inputStream =
                     TestJsonLoader.class
                             .getClassLoader()
                             .getResourceAsStream(path)) {

            if (inputStream == null) {
                throw new IllegalArgumentException(
                        "Test JSON file not found: " + path
                );
            }

            return new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );

        } catch (IOException exception) {
            throw new RuntimeException(
                    "Failed to load test JSON: " + path,
                    exception
            );
        }
    }
}
