package com.thread_exec.thread_executor.repo;

import com.thread_exec.thread_executor.model.StoreData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreDataRepo extends JpaRepository<StoreData, Long> {

    List<StoreData> findAllByTotalProfitGreaterThan(double totalProfit);
}
