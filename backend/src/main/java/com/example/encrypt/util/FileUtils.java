package com.example.encrypt.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;

public class FileUtils {

    @Value("${file.save.path}")
    private static String savePath;

    public static void write(byte[] bytes, String saveFilename) {
        try {
            Path path = Path.of(savePath, saveFilename);
            Files.write(path, bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static File from(String saveFilename) {
        return new File(savePath + saveFilename);
    }

    public static String getSaveFilename() {
        return UUID.randomUUID().toString();
    }
}
