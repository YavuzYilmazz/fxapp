package com.yavuz.fxapp.repository;

import com.yavuz.fxapp.model.Conversion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ConversionRepository extends JpaRepository<Conversion, String> {
    List<Conversion> findAllByTransactionDateBetween(LocalDateTime start, LocalDateTime end);
}
