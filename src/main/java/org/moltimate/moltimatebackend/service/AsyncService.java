package org.moltimate.moltimatebackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class AsyncService {

    private ExecutorService executorService;

    @Autowired
    private GenerateMotifService generateMotifService;

    @PostConstruct
    private void create() {
        executorService = Executors.newSingleThreadExecutor();
    }

    public void process() {
        Callable<Integer> c = () -> generateMotifService.threadUpdateMotifs();
        executorService.submit(c);
    }

    @PreDestroy
    private void destroy() {
        executorService.shutdown();
    }
}