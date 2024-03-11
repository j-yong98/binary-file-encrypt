package com.example.encrypt.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

public class FileUtils {

    private static final char DELIMITER = '.';
    private static final String SAVE_PATH = "file";

    public static void write(byte[] bytes, String saveFilename, StandardOpenOption option) {
        Path path = Paths.get(SAVE_PATH, saveFilename);
        try {
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }

            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            Files.write(path, bytes, option);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static InputStream getInputStream(String filename) throws FileNotFoundException {
        return new FileInputStream(SAVE_PATH + '/' + filename);
    }

    public static long getContentLength(String filename) {
        return new File(SAVE_PATH + '/' + filename).length();
    }

    public static Path getPath(String saveFilename) {
        return Paths.get(SAVE_PATH, saveFilename);
    }

    public static byte[] from(String saveFilename) {
        Path path = Paths.get(SAVE_PATH, saveFilename);
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void delete(String saveFilename) {
        Path path = Paths.get(SAVE_PATH, saveFilename);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getSaveFilename(String ext) {
        return UUID.randomUUID() + ext;
    }

    public static String getExt(String filename) {
        return filename.substring(filename.lastIndexOf(DELIMITER));
    }
}
