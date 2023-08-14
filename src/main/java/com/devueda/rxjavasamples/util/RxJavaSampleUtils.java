package com.devueda.rxjavasamples.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class RxJavaSampleUtils {
    public static void generateCSV(String filename, int numRows) {
        String[] currencies = {"USD", "EUR", "JPY", "GBP", "AUD", "CAD", "CHF", "CNY", "SEK", "NZD", "MXN", "SGD", "HKD", "NOK", "KRW", "TRY", "RUB", "INR", "BRL"};

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("id,transacao,moeda_origem,moeda_destino,valor\n");

            Random random = new Random();
            for (int i = 1; i <= numRows; i++) {
                String transacao = String.format("Transacao%010d", i);
                String moedaOrigem = currencies[random.nextInt(currencies.length)];
                String moedaDestino = currencies[random.nextInt(currencies.length)];
                double valor = round(random.nextDouble() * (10000 - 10) + 10, 4);
                writer.write(i + "," + transacao + "," + moedaOrigem + "," + moedaDestino + "," + valor + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
