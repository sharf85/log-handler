package com.flightright.log_handler;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

class FileProcessorTest {
    @Test
    public void fillInputCsv() throws FileNotFoundException {
        PrintWriter pw = new PrintWriter("input.csv");

        for (int i = 0; i < 50_000_000; i++) {
            pw.println("test@test.com,123,google.com");
        }

        pw.println("test@test.com,123,google.com");
        pw.println("test@test.com,123,linkedin.com");
        pw.println("test@test.com,,google.com");
        pw.println("test@test.com,123,");
        pw.println("test@test.com,321,google.com");
        pw.println("test@test.com,239,google.com");
        pw.println("test@test.com,321,linkedin.com");
        
        pw.close();
    }
}
