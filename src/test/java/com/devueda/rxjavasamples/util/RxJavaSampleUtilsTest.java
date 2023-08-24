package com.devueda.rxjavasamples.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class RxJavaSampleUtilsTest {
    @Autowired
    TransactionService transactionService;

    @Test
    public void generateCSV() throws IOException {
        String filename = "transacoes.csv";

        int numRows = 100_000_000;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        transactionService.generateCSV(filename, numRows);

        stopWatch.stop();
        System.out.println(Thread.currentThread().getName() + " - Tempo de processamento: " + stopWatch.getTotalTimeSeconds() + " segundos.");
    }

    @Test
    public void generateCSV_in_parallel() throws IOException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        String filename = "transacoes";
        int numRows = 100_000_000;

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        transactionService.generateCSVInParallel(filename, numRows, countDownLatch);
        countDownLatch.await();
        stopWatch.stop();
        System.out.println(Thread.currentThread().getName() + " - Tempo de processamento: " + stopWatch.getTotalTimeSeconds() + " segundos.");
    }
}
