package com.thread_exec.thread_executor.service;

import com.thread_exec.thread_executor.Utils.FileUtils;
import com.thread_exec.thread_executor.constants.ProcessTypeEnum;
import com.thread_exec.thread_executor.constants.StatusEnum;
import com.thread_exec.thread_executor.constants.TaskNameEnum;
import com.thread_exec.thread_executor.model.HappinessIndexData;
import com.thread_exec.thread_executor.model.StoreData;
import com.thread_exec.thread_executor.model.UploadRequest;
import com.thread_exec.thread_executor.model.UploadResponse;
import com.thread_exec.thread_executor.repo.CustomerRepo;
import com.thread_exec.thread_executor.repo.HappinessIndexRepo;
import com.thread_exec.thread_executor.repo.StoreDataRepo;
import com.thread_exec.thread_executor.repo.TaskRepo;
import com.univocity.parsers.common.IterableResult;
import com.univocity.parsers.common.ParsingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class HappinessIndexService {


}
