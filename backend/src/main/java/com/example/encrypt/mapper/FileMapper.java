package com.example.encrypt.mapper;

import com.example.encrypt.domain.FileInformation;
import com.example.encrypt.dto.FileResponse;
import com.example.encrypt.dto.FileUploadResponse;
import org.springframework.http.HttpStatus;

public class FileMapper {

    public static FileInformation toEntity(String originalFilename, String saveFilename) {
        return FileInformation.builder()
                .originalFilename(originalFilename)
                .saveFilename(saveFilename)
                .build();
    }

    public static FileUploadResponse toFileUploadResponse(HttpStatus status, String saveFilename) {
        return FileUploadResponse.builder()
                .status(status)
                .saveFilename(saveFilename)
                .build();
    }


    public static FileResponse entityToFileLoad(FileInformation fileInformation) {
        return FileResponse.builder()
                .originalFilename(fileInformation.getOriginalFilename())
                .saveFilename(fileInformation.getSaveFilename())
                .build();
    }

}