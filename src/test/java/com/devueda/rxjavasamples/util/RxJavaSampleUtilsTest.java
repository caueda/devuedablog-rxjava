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
    public void generateCSV() throws IOException {//PC - 136.3427136 segundos
        String filename = "transacoes.csv";

        int numRows = 100_000_000;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        transactionService.generateCSV(filename, numRows);

        stopWatch.stop();
        System.out.println(Thread.currentThread().getName() + " - Tempo de processamento: " + stopWatch.getTotalTimeSeconds() + " segundos.");
    }

    @Test
    public void generateCSV_in_parallel() throws IOException, InterruptedException {//PC - 57.2900219 segundos
        int parallelism = 10;
        CountDownLatch countDownLatch = new CountDownLatch(parallelism);
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
