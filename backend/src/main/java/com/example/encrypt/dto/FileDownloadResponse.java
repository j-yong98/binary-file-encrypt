package com.example.encrypt.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.MediaType;

@Builder
@Getter
public class FileDownloadResponse {
    MediaType mediaType;
    String path;
    ResourceRegion region;
}
