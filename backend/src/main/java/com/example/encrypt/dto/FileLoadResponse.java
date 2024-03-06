package com.example.encrypt.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.core.io.Resource;

@Builder
@Getter
public class FileLoadResponse {
        private String originFilename;
        private String saveFilename;
        private String ivValue;
        private LocalDateTime uploadTime;
        private Resource resource;
}
