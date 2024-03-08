package com.example.encrypt.component;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.crypto.Cipher.DECRYPT_MODE;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;

@Component
public class AES128Encryptor implements Encryptor {

    private final String secretKey = "DAtMVZHmwjYyO4nX";
    private final String ALGORITHM = "AES";
    private final String INSTANCE_TYPE = "AES/CBC/PKCS5Padding";
    private final SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(UTF_8), ALGORITHM);
    private final Cipher instance = Cipher.getInstance(INSTANCE_TYPE);

    public AES128Encryptor() throws Exception {
    }

    @Override
    public byte[] encrypt(byte[] data, String ivValue) throws Exception {
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivValue.getBytes());

        instance.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        return instance.doFinal(data);
    }

    @Override
    public byte[] decrypt(byte[] data, String ivValue) throws Exception {
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivValue.getBytes());

        instance.init(DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        return instance.doFinal(data);
    }
}
