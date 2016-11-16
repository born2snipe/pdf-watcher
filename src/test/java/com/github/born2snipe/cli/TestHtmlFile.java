package com.github.born2snipe.cli;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class TestHtmlFile {
    public static void writeTo(File destination) {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.html")) {
            Files.copy(input, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
