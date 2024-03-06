package com.example.encrypt.controller;

import com.example.encrypt.dto.FileLoadResponse;
import com.example.encrypt.service.FileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping
    public ResponseEntity<List<FileLoadResponse>> loadFiles() {
        return ResponseEntity.ok(fileService.load());
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> upload(@RequestPart MultipartFile file) {
        fileService.upload(file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> download(@PathVariable String filename) {
        FileLoadResponse download = fileService.download(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + download.getOriginFilename())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(download.getResource());
    }
}
