package com.example.encrypt.service;

import com.example.encrypt.domain.FileInformation;
import com.example.encrypt.dto.FileLoadResponse;
import com.example.encrypt.mapper.FileMapper;
import com.example.encrypt.repository.FileRepository;
import com.example.encrypt.util.FileUtils;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class FileServiceImpl implements FileService{

    private final FileRepository fileRepository;

    @Override
    public void upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String saveFilename = FileUtils.getSaveFilename();
        String ivValue = makeIvValue();

        try {
            FileUtils.write(file.getBytes(), saveFilename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        fileRepository.save(FileMapper.toEntity(originalFilename, saveFilename, ivValue));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileLoadResponse> load() {
        return FileMapper.entitiesToFileLoad(fileRepository.findAll());
    }

    @Override
    public FileLoadResponse download(String filename) {
        FileInformation fileInformation = fileRepository.findBySaveFilename(filename)
                .orElseThrow(IllegalArgumentException::new);
        return FileMapper.entityToFileLoad(fileInformation, new FileSystemResource(FileUtils.from(filename)));
    }

    private String makeIvValue() {
        return "123";
    }
}
