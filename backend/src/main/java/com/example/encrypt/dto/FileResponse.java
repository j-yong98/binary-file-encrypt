package com.example.encrypt.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FileResponse {
    private String originalFilename;
    private String saveFilename;
}
