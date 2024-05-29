package com.thread_exec.thread_executor.repo;

import com.thread_exec.thread_executor.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Long> {
    Optional<Customer> findCustomerById(Long id);
    List<Customer> findAllByName(String name);

}
