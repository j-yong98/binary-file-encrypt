package com.example.encrypt.repository;

import com.example.encrypt.domain.FileInformation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileInformation, Integer> {

    Optional<FileInformation> findBySaveFilename(String saveFilename);
}
