package com.thread_exec.thread_executor.model;

import lombok.Data;

@Data
public class ServiceRequest<T> {
    private T data;
}
