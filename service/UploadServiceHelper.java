package com.thread_exec.thread_executor.service;

import com.thread_exec.thread_executor.constants.StatusEnum;
import com.thread_exec.thread_executor.model.Customer;
import com.thread_exec.thread_executor.model.CustomerInfo;
import com.thread_exec.thread_executor.model.Tasks;
import com.thread_exec.thread_executor.repo.TaskRepo;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@Log4j
public class UploadServiceHelper {

    @Autowired
    private TaskRepo taskRepo;

    /**
     * This method creates a task entry for file process
     * @param totalCount
     * @return
     */
//    todo : add task-name
    public String createTask(Integer numberOfLines) {
        String taskUuid = UUID.randomUUID().toString();
        try {
            Tasks saveTask = taskRepo.save(
                    Tasks.builder()
                            .taskId(taskUuid)
                            .totalCount(numberOfLines)
                            .processedCount(0)
                            .status(StatusEnum.INITIATED.name())
                            .build()
            );
            if (saveTask != null){
                log.info("task is created with id "+ taskUuid + "at "+ new Date());
            }
        } catch (Exception e) {
            log.error("unable to create a task , id " + taskUuid);
        }
        return taskUuid;
    }

    public String createTask(Integer numberOfLines, String taskName,String processType) {
        String taskUuid = UUID.randomUUID().toString();
        try {
            Tasks saveTask = taskRepo.save(
                    Tasks.builder()
                            .taskId(taskUuid)
                            .totalCount(numberOfLines)
                            .processedCount(0)
                            .status(StatusEnum.INITIATED.name())
                            .taskName(taskName)
                            .processorType(processType)
                            .build()
            );
            if (saveTask != null){
                log.info("task is created with id "+ taskUuid + "at "+ new Date());
            }
        } catch (Exception e) {
            log.error("unable to create a task , id " + taskUuid);
        }
        return taskUuid;
    }

    /**
     * This method converts the request POJO to Entity POJO
     * @param customerInfo customerDto
     * @return
     */
    public Customer requestToEntity(CustomerInfo customerInfo){
        return Customer.builder()
                .customerId(customerInfo.getId())
                .name(customerInfo.getName())
                .mobile(customerInfo.getMobile())
                .city(customerInfo.getCity())
                .state(customerInfo.getState())
                .address(customerInfo.getAddress())
                .pinCode(customerInfo.getPincode())
                .build();
    }


}
