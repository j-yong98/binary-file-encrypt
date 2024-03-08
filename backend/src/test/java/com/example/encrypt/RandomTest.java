package com.example.encrypt;

import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class RandomTest {

    @Test
    void randomTest() {
        Random random = new Random();
        int length = 16;

        String randomString = random.ints(48,122) // 0~9, a~z, A~Z의 ASCII 값
                .filter(i-> (i <=57 || i >=65) && (i <=90 || i>= 97)) // 숫자, 소문자, 대문자만 필터링
                .limit(length) // 문자열 길이
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        System.out.println("랜덤 문자열: " + randomString);
        System.out.println(UUID.randomUUID());
    }
}
