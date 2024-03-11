package com.example.encrypt.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.MediaType;

@Builder
@Getter
public class FileDownloadResponse {
    private MediaType mediaType;
    private String path;
    private String totalLength;
    private ResourceRegion region;
}
