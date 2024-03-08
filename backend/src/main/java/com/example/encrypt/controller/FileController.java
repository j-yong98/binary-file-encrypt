package com.example.encrypt.controller;

import com.example.encrypt.dto.FileLoadResponse;
import com.example.encrypt.service.FileService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping
    public ResponseEntity<List<FileLoadResponse>> loadFiles() {
        return ResponseEntity.ok(fileService.load());
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> upload(@RequestPart(value = "file") MultipartFile file) throws Exception {
        log.info("request upload : {}", file);
        fileService.upload(file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download/{saveFilename}/{filename}")
    public ResponseEntity<Resource> download(@PathVariable String saveFilename, @PathVariable String filename)
            throws Exception {
        FileLoadResponse download = fileService.download(saveFilename, filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(download.getResource());
    }
}
