package com.example.encrypt.mapper;

import com.example.encrypt.domain.EncryptInformation;
import com.example.encrypt.domain.FileInformation;
import com.example.encrypt.dto.FileLoadResponse;

public class EncryptMapper {

    public static EncryptInformation toEntity(FileInformation originalFile, FileInformation encryptFile, String ivValue) {
        return EncryptInformation.builder()
                .originalFile(originalFile)
                .encryptFile(encryptFile)
                .ivValue(ivValue)
                .build();
    }

    public static FileLoadResponse entityToFileLoadResponse(EncryptInformation encryptInformation) {
        return FileLoadResponse.builder()
                .originalFile(FileMapper.entityToFileLoad(encryptInformation.getOriginalFile()))
                .encryptFile(FileMapper.entityToFileLoad(encryptInformation.getEncryptFile()))
                .ivValue(encryptInformation.getIvValue())
                .uploadTime(encryptInformation.getUploadTime())
                .build();
    }
}
