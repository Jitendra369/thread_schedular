package com.thread_exec.thread_executor.model;

import lombok.Data;

@Data
public class ServiceResponse<T>{
    private String status;
    private String message;
    private T data;
}
