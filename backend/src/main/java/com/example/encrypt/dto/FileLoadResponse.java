package com.example.encrypt.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FileLoadResponse {
        private FileResponse originalFile;
        private FileResponse encryptFile;
        private String ivValue;
        private String uploadTime;
}
