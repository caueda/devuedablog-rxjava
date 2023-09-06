package com.devueda.rxjavasamples.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

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

        ExecutorService executorService = Executors.newFixedThreadPool(11);

        Runtime.getRuntime().addShutdownHook(new Thread(executorService::shutdown));

        List<Future<?>> futures = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            final int index = i;
            Future<?> future = executorService.submit(() -> {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename + "_" +
                        String.format("%02d", index)+ ".csv"))) {
                    writer.write("id,transacao,moeda_origem,moeda_destino,valor\n");
                    for (int j = 1; j <= numRows/10; j++) {
                        String transacao = String.format("%s_Transacao_%010d", Thread.currentThread().getName(), j);
//                        log.info("{}", transacao);
                        String moedaOrigem = getRandomCurrency();
                        String moedaDestino = getDifferentCurrencyFromCurrency(moedaOrigem);
                        double valor = getRandomValue();
                        writer.write(j + "," + transacao + "," + moedaOrigem + "," + moedaDestino + "," + valor + "\n");
                    }
                    writer.flush();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            });
            futures.add(future);
        }

        futures.forEach(future -> {
            executorService.execute(() -> {
                try {
                    future.get(); // This will block until the future is done
                    countDownLatch.countDown();
                } catch (InterruptedException | ExecutionException e) {
                    log.error(e.getMessage());
                }
            });
        });
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
