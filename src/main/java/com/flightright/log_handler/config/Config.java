package com.flightright.log_handler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class Config {

    private final int threadsNum;
    private final int queueCapacity;

    public Config(@Value("${com.flightright.java_spring.threads_num}") int threadsNum, @Value("${com.flightright.java_spring.queue_capacity}") int queueCapacity) {
        this.threadsNum = threadsNum;
        this.queueCapacity = queueCapacity;
    }

    @Bean
    public ThreadPoolExecutor fileHandlerExecutor() {
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(threadsNum,
                threadsNum,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(queueCapacity)
        );

        // needed to use the blocking threadPool.queue.put() method instead of threadPool.execute(), which uses
        // queue.offer() under the hood, which in turn leads to RejectedExecutionException.
        threadPool.prestartAllCoreThreads();
        return threadPool;
    }

}
