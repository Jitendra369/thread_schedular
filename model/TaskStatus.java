package com.thread_exec.thread_executor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskStatus {
    private String taskId;
    private String status;
    private Integer processed;
    private Integer total;
}
