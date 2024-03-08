package com.example.encrypt.service;

import com.example.encrypt.component.Encryptor;
import com.example.encrypt.domain.FileInformation;
import com.example.encrypt.dto.FileLoadResponse;
import com.example.encrypt.mapper.FileMapper;
import com.example.encrypt.repository.FileRepository;
import com.example.encrypt.util.FileUtils;
import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final Encryptor encryptor;

    @Override
    public void upload(MultipartFile file) throws Exception {
        if (file == null) {
            throw new IllegalArgumentException("Empty file");
        }

        String originalFilename = file.getOriginalFilename();
        String ext = FileUtils.getExt(originalFilename);
        if (!ext.equals(".bin")) {
            throw new IllegalArgumentException();
        }

        String saveFilename = FileUtils.getSaveFilename(ext);
        String encryptFilename = makeEncryptFilename(originalFilename, ext);
        String ivValue = makeIvValue();

        byte[] data = file.getBytes();
        byte[] encrypt = encryptor.encrypt(data, ivValue);

        FileUtils.write(encrypt, saveFilename);

        FileInformation save = FileMapper.toEntity(originalFilename, saveFilename, encryptFilename, ivValue);

        fileRepository.save(save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileLoadResponse> load() {
        return FileMapper.entitiesToFileLoad(fileRepository.findAll());
    }

    @Override
    public FileLoadResponse download(String saveFilename, String filename) throws Exception {
        FileInformation fileInformation = fileRepository.findBySaveFilename(saveFilename)
                .orElseThrow(IllegalArgumentException::new);

        File file = FileUtils.from(saveFilename);
        byte[] data = Files.readAllBytes(file.toPath());
        if (fileInformation.getOriginalFilename().equals(filename)) {
            data = encryptor.decrypt(data, fileInformation.getIvValue());
        }
        Files.write(Path.of(filename), data);
        return FileMapper.entityToFileLoad(fileInformation, new ByteArrayResource(data, filename));
    }

    private String makeIvValue() {
        Random random = new Random();

        int length = 16;
        return random.ints(48, 122)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length) // 문자열 길이
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private String makeEncryptFilename(String originalFilename, String ext) {
        final String suffix = "_enc";
        return originalFilename.substring(0, originalFilename.lastIndexOf('.')) + suffix + ext;
    }
}
