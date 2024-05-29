package com.thread_exec.thread_executor.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolExecutorConfig {

    @Value("${thread.executor.core.pool.size}")
    private Integer corePoolSize;

    @Value("${thread.executor.max.core.pool.size}")
    private Integer maxPoolSize;

    @Value("${thread.executor.queue.capacity}")
    private Integer queueCapacity;

    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor(){
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(corePoolSize);
        pool.setMaxPoolSize(maxPoolSize);
        pool.setQueueCapacity(queueCapacity);
        pool.setBeanName("thread-pool-executor");
        pool.setWaitForTasksToCompleteOnShutdown(true);
//        pool.initialize();
        return pool;
    }
}
