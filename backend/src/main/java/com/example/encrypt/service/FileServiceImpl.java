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
import java.io.IOException;
import java.nio.file.Path;
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

        FileUtils.write(from, saveFilename);
        FileUtils.write(encrypt, encryptSaveFilename);

        FileInformation originalFile = FileMapper.toEntity(originalFilename, saveFilename);
        FileInformation encryptFile = FileMapper.toEntity(encryptFilename, encryptSaveFilename);

        encryptRepository.save(EncryptMapper.toEntity(originalFile, encryptFile, ivValue));
        return FileMapper.toFileUploadResponse(HttpStatus.OK, saveFilename);
    }

    private void saveChunkFile(MultipartFile file, String filename, int index) throws IOException {
        String chunkFile = filename + SUFFIX + index;
        FileUtils.write(file.getBytes(), chunkFile);
    }

    private void merge(String saveFilename, int total) {
        for (int i = 1; i <= total; i++) {
            String filename = saveFilename + SUFFIX + i;
            byte[] from = FileUtils.from(filename);
            FileUtils.write(from, saveFilename);
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
    public Optional<FileDownloadResponse> download(String saveFilename, String filename, HttpHeaders headers)
            throws Exception {
        Path path = FileUtils.getPath(saveFilename);

        FileSystemResource data = new FileSystemResource(path);

        long chunkSize = 1024 * 1024;
        long dataLength = data.contentLength();

        HttpRange httpRange = headers.getRange().stream().findFirst()
                .orElse(HttpRange.createByteRange(0, dataLength - 1));
        log.info(httpRange.toString());

        long start = httpRange.getRangeStart(dataLength);
        long end = httpRange.getRangeEnd(dataLength);
        long rangeLength = Long.min(chunkSize, end - start);
        if (start > rangeLength) {
            return Optional.empty();
        }

        MediaType mediaType = MediaTypeFactory.getMediaType(data).orElse(MediaType.APPLICATION_OCTET_STREAM);
        ResourceRegion region = new ResourceRegion(data, httpRange.getRangeStart(dataLength), rangeLength);
        return Optional.of(FileDownloadResponse.builder()
                .mediaType(mediaType)
                .path(String.valueOf(path))
                .region(region)
                .build());
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
