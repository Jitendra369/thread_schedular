package com.thread_exec.thread_executor.service;

import com.thread_exec.thread_executor.model.TaskStatus;
import com.thread_exec.thread_executor.model.UploadRequest;
import com.thread_exec.thread_executor.model.UploadResponse;

import java.io.IOException;

public interface UploadService {
    /**
     * This method uploads CSV and processes the request in multithreaded executor
     * @param request
     * @return
     */
    UploadResponse uploadCsvMultiThreaded(UploadRequest request);

    /**
     * This method uploads CSV and processes the request in single thread
     * @param request
     * @return
     */
    UploadResponse uploadCsvSingleThreaded(UploadRequest request) throws IOException;

    /**
     * This method uploads CSV and processes the request in asynchronous manner
     * @param request
     * @return
     */
    UploadResponse uploadCsvAsynchronous(UploadRequest request);

    /**
     * This method checks status of task id
     * @param taskId
     * @return
     */
    TaskStatus checkUploadTaskStatus(String taskId);

    /**
     * This method uploads CSV and processes the request with ThreadPoolTaskExecutor
     * @param request
     * @return
     */
    UploadResponse uploadCsvMultiThreadedWithThreadPoolExecutor(UploadRequest request);



}
