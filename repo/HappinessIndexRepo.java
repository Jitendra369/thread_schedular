package com.thread_exec.thread_executor.repo;

import com.thread_exec.thread_executor.model.HappinessIndexData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HappinessIndexRepo extends JpaRepository<HappinessIndexData, Long> {

}
