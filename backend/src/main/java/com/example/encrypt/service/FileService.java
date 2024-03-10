package com.example.encrypt.service;

import com.example.encrypt.dto.FileDownloadResponse;
import com.example.encrypt.dto.FileLoadResponse;
import com.example.encrypt.dto.FileUploadResponse;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    FileUploadResponse upload(MultipartFile file, String saveFilename, int index, int total) throws Exception;

    Page<FileLoadResponse> load(int page, int size);

    Optional<FileDownloadResponse> download(String filename, String encryptFilename, HttpHeaders headers) throws Exception;
}
