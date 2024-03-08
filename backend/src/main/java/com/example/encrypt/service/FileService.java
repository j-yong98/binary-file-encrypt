package com.example.encrypt.service;

import com.example.encrypt.dto.FileLoadResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    void upload(MultipartFile file) throws Exception;

    List<FileLoadResponse> load();

    FileLoadResponse download(String filename, String encryptFilename) throws Exception;
}
