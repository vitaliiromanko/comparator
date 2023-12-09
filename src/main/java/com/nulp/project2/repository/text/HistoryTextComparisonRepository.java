package com.nulp.project2.repository.text;

import com.nulp.project2.model.text.HistoryTextComparison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoryTextComparisonRepository extends JpaRepository<HistoryTextComparison, Long> {
    Optional<List<HistoryTextComparison>> findAllByUserId(Long userId);
}
