package com.thread_exec.thread_executor.service;

import com.thread_exec.thread_executor.model.HappinessIndexData;
import com.thread_exec.thread_executor.model.UploadRequest;
import com.thread_exec.thread_executor.model.UploadResponse;

import java.io.IOException;
import java.util.List;

public interface HappinessService {
    public UploadResponse uploadHappinessData(UploadRequest upRequest) throws IOException;
    public UploadResponse uploadHappDataWithSequenceProcessing(UploadRequest upRequest) throws IOException;

    public List<HappinessIndexData> getAllHappinessIndexData();

    public UploadResponse uploadUsingSingleThread(UploadRequest request) throws IOException;
}
