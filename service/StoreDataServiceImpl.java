package com.thread_exec.thread_executor.service;

import com.thread_exec.thread_executor.Utils.FileUtils;
import com.thread_exec.thread_executor.Utils.pdf.PdfService;
import com.thread_exec.thread_executor.Utils.pdf.StoreDataPdfService;
import com.thread_exec.thread_executor.constants.ProcessTypeEnum;
import com.thread_exec.thread_executor.constants.StatusEnum;
import com.thread_exec.thread_executor.constants.TaskNameEnum;
import com.thread_exec.thread_executor.model.ServiceRequest;
import com.thread_exec.thread_executor.model.StoreData;
import com.thread_exec.thread_executor.model.UploadRequest;
import com.thread_exec.thread_executor.model.UploadResponse;
import com.thread_exec.thread_executor.repo.StoreDataRepo;
import com.thread_exec.thread_executor.repo.TaskRepo;
import com.univocity.parsers.common.IterableResult;
import com.univocity.parsers.common.ParsingContext;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
@Log4j
public class StoreDataServiceImpl {

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private UploadServiceHelper uploadServiceHelper;

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private ApplicationContext applicationContext;


    @Autowired
    @Qualifier("taskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private StoreDataRepo storeDataRepo;

    @Autowired
    private StoreDataPdfService storeDataPdfService;



    public UploadResponse uploadUsingMultiThread(ServiceRequest<UploadRequest> request){

        UploadRequest request1 = request.getData();
        if (request1 != null && StringUtils.hasLength(request1.getPath())){
            try{
                Long startTime  = new Date().getTime();
                int numberOfLines = fileUtils.numberOfLines(request1.getPath()) - 1;
                IterableResult<StoreData, ParsingContext> storeDataIterator = fileUtils.readDataFromCSV(request1.getPath(), StoreData.class);
                String taskId = uploadServiceHelper.createTask(numberOfLines, TaskNameEnum.STORE_DATA.name(), ProcessTypeEnum.MULTI_CORE_THREAD.name());
                if (StringUtils.hasLength(taskId)){
                    taskRepo.updateTaskStatus(StatusEnum.PROCESSING.name(),taskId);
                    ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//                        storeDataRepo.saveAll(storeDataIterator);
                    for (StoreData storeData : storeDataIterator){
                        storeDataRepo.save(storeData);
                        taskRepo.updateProcessCount(taskId);
                    }

                    executorService.shutdown();
                    while(!executorService.isTerminated()){
                        Thread.sleep(1000);
                    }
                    Long totalTime = (new Date().getTime() - startTime)/ 1000;
                    log.info("file processing is completed in : "+ totalTime);
                    taskRepo.updateTaskStatusWithTime(StatusEnum.COMPLETED.name(), taskId, totalTime);
                    return UploadResponse.builder().count(numberOfLines).taskId(taskId).timeTaken(totalTime).build();
                }


            }catch (Exception e){
                log.error("Exception while Reading the data from csv file");
                e.printStackTrace();
            }
        }
        log.info("invalid data : file path is requried ");
        return null;
    }

//    todo: properly calculate the time , do it using single thread

    public ByteArrayInputStream getStoreDateWithProfitGreateThan(Double totalPrice){
        if (totalPrice != null && totalPrice > 0){
            List<StoreData> storeDataList = storeDataRepo.findAllByTotalProfitGreaterThan(totalPrice).stream().limit(50).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(storeDataList)){
                ByteArrayInputStream storeDataPdf = storeDataPdfService.createStoreDataPdf(storeDataList);
                return  storeDataPdf;
            }
        }
        return null;
    }

}
