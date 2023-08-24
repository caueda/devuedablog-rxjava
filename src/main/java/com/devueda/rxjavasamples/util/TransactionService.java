package com.devueda.rxjavasamples.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "transaction")
@Slf4j
public class TransactionService {

    private List<String> currencies;

    @Async
    public void generateCSVInParallel(String filename, int numRows, CountDownLatch countDownLatch) throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        List<Future<?>> futures = new ArrayList<>();
        NumberFormat nf = NumberFormat.getInstance(new Locale("pt", "BR"));
        for (int i = 0; i < 10; i++) {
            final int index = i;
            Future<?> future = executorService.submit(() -> {
                try {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename + "_" + (index) + ".csv"))) {
                        writer.write("id,transacao,moeda_origem,moeda_destino,valor\n");
                        Random random = new Random();
                        for (int j = 1; index <= numRows/10; j++) {
                            log.info("Thread: " + Thread.currentThread().getName() + " - " + nf.format(j));
                            String transacao = String.format("Transacao%010d", j);
                            String moedaOrigem = getRandomCurrency();
                            String moedaDestino = getDifferentCurrencyFromCurrency(moedaOrigem);
                            double valor = getRandomValue();
                            writer.write(j + "," + transacao + "," + moedaOrigem + "," + moedaDestino + "," + valor + "\n");
                        }
                    } catch (IOException e) {
                        throw e;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            futures.add(future);
        }

        futures.forEach(f -> {
            try {
                f.get();
                countDownLatch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        stopWatch.stop();
        System.out.println(Thread.currentThread().getName() + " - Tempo de processamento: " + stopWatch.getTotalTimeSeconds() + " segundos.");
    }
    public void generateCSV(String filename, int numRows) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("id,transacao,moeda_origem,moeda_destino,valor\n");
            Random random = new Random();
            for (int i = 1; i <= numRows; i++) {
                String transacao = String.format("Transacao%010d", i);
                String moedaOrigem = getRandomCurrency();
                String moedaDestino = getDifferentCurrencyFromCurrency(moedaOrigem);
                double valor = getRandomValue();
                writer.write(i + "," + transacao + "," + moedaOrigem + "," + moedaDestino + "," + valor + "\n");
            }
        } catch (IOException e) {
            throw e;
        }
    }

    private Double getRandomValue() {
        Random random = new Random();
        return round(random.nextDouble() * (10000 - 10) + 10, 4);
    }

    private String getRandomCurrency() {
        Random random = new Random();
        return currencies.get(random.nextInt(currencies.size()));
    }

    private String getDifferentCurrencyFromCurrency(String fromCurrency) {
        Random random = new Random();
        String toCurrency = currencies.get(random.nextInt(currencies.size()));
        while (toCurrency.equals(fromCurrency)) {
            toCurrency = currencies.get(random.nextInt(currencies.size()));
        }
        return toCurrency;
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
