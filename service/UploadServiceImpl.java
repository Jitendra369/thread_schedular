package com.thread_exec.thread_executor.service;

import com.thread_exec.thread_executor.Utils.FileUtils;
import com.thread_exec.thread_executor.constants.ProcessTypeEnum;
import com.thread_exec.thread_executor.constants.StatusEnum;
import com.thread_exec.thread_executor.constants.TaskNameEnum;
import com.thread_exec.thread_executor.model.*;
import com.thread_exec.thread_executor.repo.CustomerRepo;
import com.thread_exec.thread_executor.repo.HappinessIndexRepo;
import com.thread_exec.thread_executor.repo.TaskRepo;
import com.thread_exec.thread_executor.thread.ProcessFileUploadTask;
import com.univocity.parsers.common.IterableResult;
import com.univocity.parsers.common.ParsingContext;
import lombok.extern.log4j.Log4j;
import org.apache.catalina.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

@Service
@Log4j
public class UploadServiceImpl implements UploadService{

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private UploadServiceHelper uploadServiceHelper;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private ApplicationContext applicationContext;


    @Autowired
    @Qualifier("taskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private HappinessIndexRepo happinessIndexRepo;



    @Override
    public UploadResponse uploadCsvMultiThreaded(UploadRequest upRequest) {
        try {
            if (upRequest != null && StringUtils.hasLength(upRequest.getPath())) {
                Long start = new Date().getTime();
                int numberOfLines = fileUtils.numberOfLines(upRequest.getPath()) - 1; // Reduce by 1 to ignore header line
                IterableResult<CustomerInfo, ParsingContext> customerIterator = fileUtils.readDataFromCSV(upRequest.getPath(), CustomerInfo.class);
                String taskId = uploadServiceHelper.createTask(numberOfLines);
                if (io.micrometer.common.util.StringUtils.isNotEmpty(taskId)) {
//                    update the task status , from task id
                    taskRepo.updateTaskStatus(StatusEnum.PROCESSING.name(), taskId);
                    int availableCore = Runtime.getRuntime().availableProcessors();
                    ExecutorService executorService = Executors.newFixedThreadPool(availableCore);
                    for (CustomerInfo customerInfo : customerIterator) {
                        executorService.submit(() -> {
                            customerRepo.save(uploadServiceHelper.requestToEntity(customerInfo));
                            taskRepo.updateProcessCount(taskId);
                        });
                    }
                    executorService.shutdown();
                    while (!executorService.isTerminated()) {
                        Thread.sleep(1000);
                    }

                    Long timeTaken = new Date().getTime() - start;
                    log.info("file processing completed in ms : " + timeTaken);
                    taskRepo.updateTaskStatus(StatusEnum.COMPLETED.name(), taskId);
                    return UploadResponse.builder().count(numberOfLines).taskId(taskId).timeTaken(timeTaken).build();
                } else {
                    log.info("unable to create a task ");
                }
            } else {
                log.info("invalid upload request, file path is mandatory");
            }
        } catch (Exception e) {
            log.error("An error occurred while reading csv from file path:", e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public UploadResponse uploadCsvSingleThreaded(UploadRequest request) throws IOException {

        if (request != null && StringUtils.hasLength(request.getPath())) {

            try {
                String filePath = request.getPath();
                long startDate = new Date().getTime();
                int numberOfLines = fileUtils.numberOfLines(filePath) - 1;
                IterableResult<CustomerInfo, ParsingContext> iterator = fileUtils.readDataFromCSV(filePath, CustomerInfo.class);
                String taskId = uploadServiceHelper.createTask(numberOfLines);
                if (StringUtils.hasLength(taskId)) {
                    taskRepo.updateTaskStatus(StatusEnum.PROCESSING.name(), taskId);
                    for (CustomerInfo element : iterator) {
                        customerRepo.save(uploadServiceHelper.requestToEntity(element));
                        taskRepo.updateProcessCount(taskId);
                    }
                    Long timeTaken = new Date().getTime() - startDate;
                    log.info("file processing completed in " + timeTaken);
                    taskRepo.updateTaskStatus(StatusEnum.COMPLETED.name(), taskId);
                    return UploadResponse.builder().count(numberOfLines).taskId(taskId).build();
                } else {
                    log.info("unable to create a task");
                }
            } catch (Exception e) {
                log.error("error occur while upload the file ");
            }
        } else {
            log.info("invalid upload request , file path is requried ");
        }
        return null;
    }

    @Override
    public UploadResponse uploadCsvAsynchronous(UploadRequest request) {
        try{
            if (request!= null && StringUtils.hasLength(request.getPath())){
                Long start = new Date().getTime();
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(()-> uploadCsvMultiThreaded(request));
                Long timeTake = new Date().getTime() - start;
                return UploadResponse.builder().timeTaken(timeTake).build();
            }else{
                log.info("invalid request , file Path is null");
            }
        }catch (Exception e){
            log.error("An error occurred in uploading csv in asynchronous manner:",e);
        }
        return null;
    }

    @Override
    public TaskStatus checkUploadTaskStatus(String taskId) {
        try {
            if (StringUtils.hasLength(taskId)) {
                Optional<Tasks> tasksOptional = taskRepo.findByTaskId(taskId);
                if (tasksOptional.isPresent()) {
                    Tasks tasks = tasksOptional.get();
                    log.info("Task status for task id:{} , status:{}"+ taskId +" "+ tasks.getStatus());
                    return TaskStatus.builder().taskId(taskId).total(tasks.getTotalCount()).processed(tasks.getProcessedCount()).status(tasks.getStatus()).build();
                } else {
                    log.info("Invalid task id");
                }
            } else {
                log.info("Task Id cannot be empty or null.");
            }
        } catch (Exception e) {
            log.error("An error occurred in fetching the upload task status:", e);
        }
        return null;
    }

    /**
     * This method uploads CSV and processes the request with ThreadPoolExecutorService
     *
     * @param request
     * @return
     */
    @Override
    public UploadResponse uploadCsvMultiThreadedWithThreadPoolExecutor(UploadRequest request) {
        try {
            if (request != null && StringUtils.hasLength(request.getPath())) {
                Long start = new Date().getTime();
                int numberOfLines = fileUtils.numberOfLines(request.getPath()) - 1;  // -1 ignore header
                IterableResult<CustomerInfo, ParsingContext> iterator = fileUtils.readDataFromCSV(request.getPath(), CustomerInfo.class);
                String taskId = uploadServiceHelper.createTask(numberOfLines);
                if (StringUtils.hasLength(taskId)) {
                   taskRepo.updateTaskStatus(StatusEnum.PROCESSING.name(), taskId);
                    CopyOnWriteArrayList<Boolean> response = new CopyOnWriteArrayList<>();
                    for (CustomerInfo customerInfo : iterator) {
                        ProcessFileUploadTask thread = applicationContext.getBean(ProcessFileUploadTask.class);
                        thread.setCustomerInfo(customerInfo);
                        thread.setTaskId(taskId);
                        Future<Boolean> result = threadPoolTaskExecutor.submit(thread);
                        response.add(result.get());
                    }
                    Long timeTaken = new Date().getTime() - start;
                    log.info("Total records:{}, File processing completed in :{} ms" + response.size() + " " + timeTaken);
                    taskRepo.updateTaskStatus(StatusEnum.COMPLETED.name(), taskId);
                    return UploadResponse.builder().count(numberOfLines).taskId(taskId).timeTaken(timeTaken).build();
                } else {
                    log.info("Unable to create task.");
                }
            } else {
                log.info("Invalid upload request. File path is mandatory.");
            }
        } catch (IOException e) {
            log.error("An error occurred while reading csv from file path:", e);
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
