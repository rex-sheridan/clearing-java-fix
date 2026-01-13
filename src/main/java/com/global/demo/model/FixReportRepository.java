package com.global.demo.model;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FixReportRepository extends JpaRepository<FixReport, Long> {
    List<FixReport> findAllByOrderByReceivedAtDesc();
}
