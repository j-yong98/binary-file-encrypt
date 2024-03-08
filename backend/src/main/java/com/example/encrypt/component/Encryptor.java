package com.example.encrypt.component;

public interface Encryptor {
    byte[] encrypt(byte[] data, String ivValue) throws Exception;

    byte[] decrypt(byte[] data, String ivValue) throws Exception;
}
