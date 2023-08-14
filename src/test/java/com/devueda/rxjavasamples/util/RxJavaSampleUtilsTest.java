package com.devueda.rxjavasamples.util;

import org.junit.jupiter.api.Test;

public class RxJavaSampleUtilsTest {
    @Test
    public void generateCSV() {
        String filename = "transacoes.csv";
        int numRows = 100000000;

        long startTime = System.currentTimeMillis();
        RxJavaSampleUtils.generateCSV(filename, numRows);
        long endTime = System.currentTimeMillis();

        System.out.println("Arquivo CSV gerado com sucesso.");
        System.out.println("Tempo de processamento: " + (endTime - startTime) + " milissegundos");
    }
}
