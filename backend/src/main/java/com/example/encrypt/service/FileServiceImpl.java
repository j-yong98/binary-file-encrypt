package com.example.encrypt.service;

import com.example.encrypt.component.Encryptor;
import com.example.encrypt.domain.FileInformation;
import com.example.encrypt.dto.FileDownloadResponse;
import com.example.encrypt.dto.FileLoadResponse;
import com.example.encrypt.dto.FileUploadResponse;
import com.example.encrypt.mapper.EncryptMapper;
import com.example.encrypt.mapper.FileMapper;
import com.example.encrypt.repository.EncryptRepository;
import com.example.encrypt.repository.FileRepository;
import com.example.encrypt.util.FileUtils;
import jakarta.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final String SUFFIX = ".part";
    private final FileRepository fileRepository;
    private final EncryptRepository encryptRepository;
    private final Encryptor encryptor;
    private final ConcurrentHashMap<String, Integer> check = new ConcurrentHashMap<>();

    @Override
    public FileUploadResponse upload(MultipartFile file, String saveFilename, int index, int total) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String ext = FileUtils.getExt(originalFilename);
        if (!ext.equals(".bin")) {
            throw new IllegalArgumentException();
        }

        if (saveFilename == null || saveFilename.isEmpty()) {
            saveFilename = FileUtils.getSaveFilename(ext);
        }

        saveChunkFile(file, saveFilename, index);
        check.put(saveFilename, check.getOrDefault(saveFilename, 0) + 1);
        if (total != check.get(saveFilename)) {
            return FileMapper.toFileUploadResponse(HttpStatus.PARTIAL_CONTENT, saveFilename);
        }

        merge(saveFilename, total);
        byte[] from = FileUtils.from(saveFilename);

        String ivValue = makeIvValue();
        String encryptFilename = makeEncryptFilename(originalFilename, ext);
        String encryptSaveFilename = FileUtils.getSaveFilename(ext);
        byte[] encrypt = encryptor.encrypt(from, ivValue);

        FileUtils.write(from, saveFilename, StandardOpenOption.WRITE);
        FileUtils.write(encrypt, encryptSaveFilename, StandardOpenOption.WRITE);

        FileInformation originalFile = FileMapper.toEntity(originalFilename, saveFilename);
        FileInformation encryptFile = FileMapper.toEntity(encryptFilename, encryptSaveFilename);

        encryptRepository.save(EncryptMapper.toEntity(originalFile, encryptFile, ivValue));
        return FileMapper.toFileUploadResponse(HttpStatus.OK, saveFilename);
    }

    private void saveChunkFile(MultipartFile file, String filename, int index) throws IOException {
        String chunkFile = filename + SUFFIX + index;
        FileUtils.write(file.getBytes(), chunkFile, StandardOpenOption.WRITE);
    }

    private void merge(String saveFilename, int total) {
        for (int i = 1; i <= total; i++) {
            String filename = saveFilename + SUFFIX + i;
            byte[] from = FileUtils.from(filename);
            FileUtils.write(from, saveFilename, StandardOpenOption.APPEND);
            FileUtils.delete(filename);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FileLoadResponse> load(int page, int size) {
        PageRequest of = PageRequest.of(page, size);
        return encryptRepository.findAll(of).map(EncryptMapper::entityToFileLoadResponse);
    }

    @Override
    public void download(String saveFilename, String filename, HttpServletResponse response)
            throws Exception {
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment=;filename" + filename);
        response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(FileUtils.getContentLength(saveFilename)));
        InputStream inputStream = FileUtils.getInputStream(saveFilename);
        OutputStream out = response.getOutputStream();

        byte[] bytes = new byte[1024 * 1024];
        while (inputStream.read(bytes) != -1) {
            out.write(bytes);
        }
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
