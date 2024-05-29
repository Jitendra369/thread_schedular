package com.thread_exec.thread_executor.service;

import com.thread_exec.thread_executor.model.Customer;
import com.thread_exec.thread_executor.repo.CustomerRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j
public class CustomerService {

    private final CustomerRepo customerRepo;
    public Customer saveCustomer(Customer customer){
        if (customer!= null){
           return customerRepo.save(customer);
        }
        return null;
    }

    public List<Customer> getAllCustomers(){
        List<Customer> savedCustomerList = new ArrayList<>();
        List<Customer> customerList = customerRepo.findAll();
        if (CollectionUtils.isEmpty(customerList)){
            log.info("customer list is empty");
            return savedCustomerList;
        }
        return customerList;
    }
}
