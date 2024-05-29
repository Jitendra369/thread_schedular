package com.thread_exec.thread_executor.service;

import com.thread_exec.thread_executor.model.StoreData;
import com.thread_exec.thread_executor.repo.StoreDataRepo;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j
public class StoreDataService {

    @Autowired
    private StoreDataRepo storeDataRepo;

    public StoreData saveStoreData(StoreData storeData){
        if (storeData!= null){
            return this.storeDataRepo.save(storeData);
        }else{
            log.info("Invalid or null store data ");
        }
        return null;
    }

}
