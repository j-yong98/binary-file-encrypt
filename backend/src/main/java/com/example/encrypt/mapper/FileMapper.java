package com.example.encrypt.mapper;

import com.example.encrypt.domain.FileInformation;
import com.example.encrypt.dto.FileLoadResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.io.Resource;

public class FileMapper {

    public static FileInformation toEntity(String originalFilename, String saveFilename, String ivValue) {
        return FileInformation.builder()
                .originalFilename(originalFilename)
                .saveFilename(saveFilename)
                .ivValue(ivValue)
                .build();
    }

    public static List<FileLoadResponse> entitiesToFileLoad(List<FileInformation> files) {
        return files.stream().map(FileMapper::entityToFileLoad).collect(Collectors.toList());
    }

    public static FileLoadResponse entityToFileLoad(FileInformation fileInformation) {
        return FileLoadResponse.builder()
                .originFilename(fileInformation.getOriginalFilename())
                .saveFilename(fileInformation.getSaveFilename())
                .ivValue(fileInformation.getIvValue())
                .uploadTime(fileInformation.getUploadTime())
                .build();
    }

    public static FileLoadResponse entityToFileLoad(FileInformation fileInformation, Resource resource) {
        return FileLoadResponse.builder()
                .originFilename(fileInformation.getOriginalFilename())
                .saveFilename(fileInformation.getSaveFilename())
                .ivValue(fileInformation.getIvValue())
                .uploadTime(fileInformation.getUploadTime())
                .resource(resource)
                .build();
    }
}