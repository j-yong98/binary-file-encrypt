package com.example.encrypt.repository;

import com.example.encrypt.domain.EncryptInformation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EncryptRepository extends JpaRepository<EncryptInformation, Integer> {
    Page<EncryptInformation> findAll(Pageable pageable);
}
