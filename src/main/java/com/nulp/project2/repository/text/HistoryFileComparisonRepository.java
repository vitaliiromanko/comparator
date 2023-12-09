package com.nulp.project2.repository.text;

import com.nulp.project2.model.text.HistoryFileComparison;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HistoryFileComparisonRepository extends JpaRepository<HistoryFileComparison, Long> {
    Optional<List<HistoryFileComparison>> findAllByUserId(Long userId);
}
