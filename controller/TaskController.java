package com.thread_exec.thread_executor.controller;

import com.thread_exec.thread_executor.model.*;
import com.thread_exec.thread_executor.service.HappinessService;
import com.thread_exec.thread_executor.service.StoreDataService;
import com.thread_exec.thread_executor.service.StoreDataServiceImpl;
import com.thread_exec.thread_executor.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/core/upload")
public class TaskController {

    @Autowired
    private UploadService uploadService;

    @Autowired
    private HappinessService happinessService;

    @Autowired
    private StoreDataServiceImpl storeDataService;


    @PostMapping("/task/multi")
    public UploadResponse uploadCSVMulThread(@RequestBody ServiceRequest<UploadRequest> request){
        UploadResponse uploadResponse = uploadService.uploadCsvMultiThreaded(request.getData());
        return uploadResponse;
    }

    @PostMapping("/task/single")
    public UploadResponse uploadCSVUploadSingleThread(@RequestBody ServiceRequest<UploadRequest> request) throws IOException {
        UploadResponse uploadResponse = uploadService.uploadCsvSingleThreaded(request.getData());
        return uploadResponse;
    }

    @PostMapping("/happIndx/multi")
    public UploadResponse uploadHappinessMultiThread(@RequestBody ServiceRequest<UploadRequest> request) throws IOException {
        return happinessService.uploadHappDataWithSequenceProcessing(request.getData());
    }
    @GetMapping("/happIndx/all")
    public List<HappinessIndexData> getAllHappinessIndex(){
        return happinessService.getAllHappinessIndexData();
    }

    @PostMapping("/happIndx/single")
    public UploadResponse uploadHappinesSingleThread(@RequestBody ServiceRequest<UploadRequest> request) throws IOException {
        return happinessService.uploadUsingSingleThread(request.getData());
    }

    @PostMapping("/storeData/multi")
    public UploadResponse uploadStoreDateMultiThread(@RequestBody ServiceRequest<UploadRequest> request) throws IOException {
        return storeDataService.uploadUsingMultiThread(request);
    }

    @GetMapping("/storeData/pdf/{totalProfit}")
    public ResponseEntity<InputStreamResource> getTotalProfit(@PathVariable Double totalProfit) throws IOException {
        ByteArrayInputStream byteArrayInputStream = storeDataService.getStoreDateWithProfitGreateThan(totalProfit);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Disposition","inline;file=dahboard.pdf");
        return ResponseEntity.ok().headers(httpHeaders).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(byteArrayInputStream));
    }

}

