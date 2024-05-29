package com.thread_exec.thread_executor.service;

import com.thread_exec.thread_executor.Utils.FileUtils;
import com.thread_exec.thread_executor.constants.ProcessTypeEnum;
import com.thread_exec.thread_executor.constants.StatusEnum;
import com.thread_exec.thread_executor.constants.TaskNameEnum;
import com.thread_exec.thread_executor.model.HappinessIndexData;
import com.thread_exec.thread_executor.model.UploadRequest;
import com.thread_exec.thread_executor.model.UploadResponse;
import com.thread_exec.thread_executor.repo.CustomerRepo;
import com.thread_exec.thread_executor.repo.HappinessIndexRepo;
import com.thread_exec.thread_executor.repo.TaskRepo;
import com.univocity.parsers.common.IterableResult;
import com.univocity.parsers.common.ParsingContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@Slf4j
public class HappinessServiceImpl implements HappinessService{

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
    public UploadResponse uploadHappinessData(UploadRequest upRequest) throws IOException {
        if (upRequest!= null && StringUtils.hasLength(upRequest.getPath())){
            try{
                Long start = new Date().getTime();
                int numberOfLines = fileUtils.numberOfLines(upRequest.getPath()) - 1;
                IterableResult<HappinessIndexData, ParsingContext> iterator = fileUtils.readDataFromCSV(upRequest.getPath(), HappinessIndexData.class);
                String taskId = uploadServiceHelper.createTask(numberOfLines, TaskNameEnum.HAPPINESS_INDEX.name(), ProcessTypeEnum.MULTI_CORE_THREAD.name());
                if (StringUtils.hasLength(taskId)){
                    taskRepo.updateTaskStatus(StatusEnum.PROCESSING.name(), taskId);
                    int avaliableCore = Runtime.getRuntime().availableProcessors();
                    ExecutorService executorService = Executors.newFixedThreadPool(avaliableCore);
//                    note : while insert into data , the sequence is not preserve
                    for (HappinessIndexData happinessIndexData : iterator){
                        executorService.submit(()->{
                            happinessIndexRepo.save(happinessIndexData);
                            taskRepo.updateProcessCount(taskId);
                        });
                    }
                    executorService.shutdown();
                    while (!executorService.isTerminated()){
                        Thread.sleep(1000);
                    }
                    Long timeTaken = new Date().getTime() - start;
                    log.info("file processing completed in ms : " + timeTaken);
                    taskRepo.updateTaskStatusWithTime(StatusEnum.COMPLETED.name(), taskId,timeTaken);
                    return UploadResponse.builder().count(numberOfLines).taskId(taskId).timeTaken(timeTaken).build();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            log.error("invalid request : file path is requried");
        }
        return null;
    }

//    maintain order

    /**
     *
     * The ExecutorCompletionService >> in Java is a utility class that combines an Executor (like a thread pool) with a BlockingQueue to manage the execution and retrieval of tasks.
     * The main advantage of using ExecutorCompletionService is that it simplifies the process of submitting tasks for execution and then retrieving their results in the order of
     * completion, rather than the order of submission. This can be particularly useful when the tasks have varying completion times and you want to process the results as soon
     * as they are available
     *
     * Let's say you have several tasks that need to be executed concurrently, and you want to process the results as soon as they are available. Here's how you can use ExecutorCompletionService:
     */

    /**
     * n a multithreading environment, tasks are executed concurrently, meaning their order of completion is not guaranteed to match their order of submission.
     * This is why the ExecutorCompletionService processes tasks as they complete, not necessarily in the order they were submitted.
     * If you need to maintain the order of results as they were submitted, you would need to store the results in a way that preserves this order.
     * Here’s an example of how you might achieve this using a ConcurrentHashMap or a simple list where you can collect the results by their index:
     */
    public UploadResponse uploadHappDataWithSequenceProcessing(UploadRequest upRequest) throws IOException {
        if (upRequest!= null && StringUtils.hasLength(upRequest.getPath())){
            List<Future<Void>> futureList = new ArrayList<>();
            List<HappinessIndexData> happinessIndexDataList = new ArrayList<>();
            try{
                Long start = new Date().getTime();
                int numberOfLines = fileUtils.numberOfLines(upRequest.getPath()) - 1;
                IterableResult<HappinessIndexData, ParsingContext> iterator = fileUtils.readDataFromCSV(upRequest.getPath(), HappinessIndexData.class);
                String taskId = uploadServiceHelper.createTask(numberOfLines, TaskNameEnum.HAPPINESS_INDEX.name(), ProcessTypeEnum.MULTI_CORE_THREAD_SEQUENCE_PROCESSING.name());
                if (StringUtils.hasLength(taskId)){
                    taskRepo.updateTaskStatus(StatusEnum.PROCESSING.name(), taskId);
                    int avaliableCore = Runtime.getRuntime().availableProcessors();
                    ExecutorService executorService = Executors.newFixedThreadPool(avaliableCore);
                    ExecutorCompletionService executorCompletionService = new ExecutorCompletionService(executorService);
//                    note :ExecutorCompletionService using this , we can preserve the sequence of insertion

                    for (HappinessIndexData happinessIndexData : iterator){
                        happinessIndexDataList.add(happinessIndexData);
                        executorCompletionService.submit(()->{
                            happinessIndexRepo.save(happinessIndexData);
                            taskRepo.updateProcessCount(taskId);
                            return null;
                        });
                    }

                    /* HOW TO PRESERVE THE DATA ?
                    The ExecutorCompletionService does not provide a direct way to take a specific response based on criteria, as the take() method retrieves completed tasks in the order they finish, not based on a specific condition.
                    However, if you need to process specific responses, you can implement a custom approach where you:
                    Collect all the results.
                    Post-process them to filter or select the specific results you need.*/

                    happinessIndexDataList.forEach(happinessIndexData -> {
                        Future<Void> future = null;
                        try {
                            future = executorCompletionService.take();
                            if (future!=null){
                                futureList.add(future);
                            }else{
                                log.info("getting value from future");
                            }
                        } catch (InterruptedException e) {
                            log.error("exception in getting data from future ");
                            throw new RuntimeException(e);
                        }
                    });

                    executorService.shutdown();
                    while (!executorService.isTerminated()){
                        Thread.sleep(1000);
                    }
                    Long timeTaken = new Date().getTime() - start;
                    log.info("file processing completed in ms : " + timeTaken);
                    taskRepo.updateTaskStatusWithTime(StatusEnum.COMPLETED.name(), taskId,timeTaken);
                    return UploadResponse.builder().count(numberOfLines).taskId(taskId).timeTaken(timeTaken).build();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            log.error("invalid request : file path is requried");
        }
        return null;
    }

    public UploadResponse uploadUsingSingleThread(UploadRequest request) throws IOException {
        if (request != null && StringUtils.hasLength(request.getPath())) {
           try{
               String filePath = request.getPath();
               Long startTime = new Date().getTime();
               int numberOfLines = fileUtils.numberOfLines(filePath) - 1;
               IterableResult<HappinessIndexData, ParsingContext> iterator = fileUtils.readDataFromCSV(filePath, HappinessIndexData.class);
               String taskId = uploadServiceHelper.createTask(numberOfLines, TaskNameEnum.HAPPINESS_INDEX.name(), ProcessTypeEnum.SINGLE_THREAD.name());
               if (StringUtils.hasLength(taskId)) {
                   taskRepo.updateTaskStatus(StatusEnum.PROCESSING.name(), taskId);
//                happinessIndexRepo.saveAll(iterator);
                   for (HappinessIndexData happinessIndexData : iterator) {
                       happinessIndexRepo.save(happinessIndexData);
                       taskRepo.updateProcessCount(taskId);
                   }
                   Long timeTaken = new Date().getTime() - startTime;
                   log.info("file processing is completed");
                   taskRepo.updateTaskStatusWithTime(StatusEnum.COMPLETED.name(), taskId,timeTaken);
                   return UploadResponse.builder().count(numberOfLines).taskId(taskId).timeTaken(timeTaken).build();
               } else {
                   log.info("unble to create a task ");
               }
           }catch (Exception e){
               log.error("Exception while saving the data , using single thread ");
               e.printStackTrace();
           }

        } else {
            log.info("Invalid upload request , file path is requried ");
        }
        return null;
    }

    @Override
    public List<HappinessIndexData> getAllHappinessIndexData() {
        List<HappinessIndexData> happinessIndexDataList = happinessIndexRepo.findAll();
        if (CollectionUtils.isNotEmpty(happinessIndexDataList)){
            return happinessIndexDataList;
        }else{
            log.info("no data found in happinessIndex tables ");
        }
        return null;
    }
}
