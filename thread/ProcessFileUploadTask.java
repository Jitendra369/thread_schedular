package com.thread_exec.thread_executor.thread;

import com.thread_exec.thread_executor.model.CustomerInfo;
import com.thread_exec.thread_executor.repo.CustomerRepo;
import com.thread_exec.thread_executor.repo.TaskRepo;
import com.thread_exec.thread_executor.service.UploadServiceHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

@Component
@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE) // todo : what is this ?
public class ProcessFileUploadTask implements Callable<Boolean> {

    @Autowired
    private UploadServiceHelper uploadServiceHelper;

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private CustomerRepo customerRepo;

    @Getter
    private CustomerInfo customerInfo;
    @Getter
    private String taskId;
    @Override
    public Boolean call() throws Exception {
        try{
            customerRepo.save(uploadServiceHelper.requestToEntity(customerInfo));
            taskRepo.updateProcessCount(taskId);
            return true;
        }catch (Exception e){
            log.error("An error occurred while processing thread:",e);
        }
        return false;
    }

    public void setCustomerInfo(CustomerInfo customerInfo) {
        this.customerInfo = customerInfo;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
