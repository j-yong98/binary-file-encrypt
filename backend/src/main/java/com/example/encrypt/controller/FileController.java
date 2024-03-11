package com.example.encrypt.controller;

import com.example.encrypt.dto.FileLoadResponse;
import com.example.encrypt.dto.FileUploadResponse;
import com.example.encrypt.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.OutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping
    public ResponseEntity<Page<FileLoadResponse>> loadFiles(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "5") int size) {
        log.info("page : {}, size : {}", page, size);
        return ResponseEntity.ok(fileService.load(page, size));
    }

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> upload(
            @RequestPart(value = "file") MultipartFile file,
            @RequestHeader(name = "SaveFilename") String saveFilename,
            @RequestHeader(name = "ChunkIndex") int index,
            @RequestHeader(name = "ChunkTotal") int total
    ) throws Exception {
        log.info("request upload : {}, {}, {}", saveFilename, index, total);
        FileUploadResponse upload = fileService.upload(file, saveFilename, index, total);
        return ResponseEntity.status(upload.getStatus()).body(upload);
    }

    @GetMapping("/download/{saveFilename}/{filename}")
    public void download(@PathVariable String saveFilename, @PathVariable String filename, HttpServletResponse response)
            throws Exception {
        fileService.download(saveFilename, filename, response);
    }
}
