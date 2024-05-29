package com.thread_exec.thread_executor.controller;

import com.thread_exec.thread_executor.model.Customer;
import com.thread_exec.thread_executor.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/core")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping()
    public Customer addCustomer(@RequestBody Customer customer){
        if (customer!= null){
           return customerService.saveCustomer(customer);
        }
        return null;
    }

    @GetMapping
    public List<Customer> getAllCustomers(){
        return customerService.getAllCustomers();
    }

}
