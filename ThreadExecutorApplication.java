package com.thread_exec.thread_executor;

import com.thread_exec.thread_executor.model.Customer;
import com.thread_exec.thread_executor.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ThreadExecutorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThreadExecutorApplication.class, args);
    }

}
