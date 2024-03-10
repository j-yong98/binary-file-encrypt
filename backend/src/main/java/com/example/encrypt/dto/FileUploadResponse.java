package com.example.encrypt.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
public class FileUploadResponse {
    private HttpStatus status;
    private String saveFilename;
}
