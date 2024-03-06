package com.example.encrypt.service;

import com.example.encrypt.dto.FileLoadResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    void upload(MultipartFile file);

    List<FileLoadResponse> load();

    FileLoadResponse download(String filename);
}
