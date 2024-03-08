package com.example.encrypt.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;

public class FileUtils {

    private static final char DELIMITER = '.';
    private static final String savePath = "file";

    public static void write(byte[] bytes, String saveFilename) {
        Path path = Paths.get(savePath, saveFilename);
        try {
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }

            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            Files.write(path, bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static File from(String saveFilename) {
        return new File(savePath + saveFilename);
    }

    public static String getSaveFilename(String filename) {
        return UUID.randomUUID() + filename.substring(filename.lastIndexOf(DELIMITER));
    }

}
