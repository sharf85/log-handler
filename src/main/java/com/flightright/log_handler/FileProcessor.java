package com.flightright.log_handler;

import com.flightright.log_handler.service.UserEntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class FileProcessor implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileProcessor.class);

    private static final String DEFAULT_INPUT_FILENAME = "input.csv";
    private static final String DEFAULT_OUTPUT_FILENAME = "output.csv";

    private final UserEntryService userEntryService;
    private final ThreadPoolExecutor threadPool;
    private final int batchSize;

    public FileProcessor(UserEntryService userEntryService,
                         @Qualifier("fileHandlerExecutor") ThreadPoolExecutor threadPool,
                         @Value("${com.flightright.java_spring.batch_size}") int batchSize) {
        this.userEntryService = userEntryService;
        this.threadPool = threadPool;
        this.batchSize = batchSize;
    }

    @Override
    public void run(String... args) throws Exception {
        String inputFilename = args.length > 0 ? args[0] : DEFAULT_INPUT_FILENAME;
        String outputFilename = args.length > 1 ? args[1] : DEFAULT_OUTPUT_FILENAME;


        try (BufferedReader br = new BufferedReader(new FileReader(inputFilename));
             PrintWriter pw = new PrintWriter(outputFilename)) {

            long timestamp = System.currentTimeMillis();
            // read and handle the file
            processInputFileBatched(br);

            long afterProcessingTimestamp = System.currentTimeMillis();
            LOGGER.info("Saved logs for: " + (afterProcessingTimestamp - timestamp) + " millis");

            // compose the statistics
            Map<String, Integer> output = userEntryService.getUniqueUsersNumberGroupedBySource();
            // write it to the file
            writeOutputFile(pw, output);
            LOGGER.info("Wrote result to file for: " + (System.currentTimeMillis() - afterProcessingTimestamp) + " millis");
        }

    }

    private boolean processInputFileBatched(BufferedReader br) throws IOException, InterruptedException {
        // read the header
        br.readLine();
        String line;

        List<String> linesBatch = new ArrayList<>();

        while ((line = br.readLine()) != null) {
            linesBatch.add(line);
            if (linesBatch.size() >= batchSize) {
                // run the batch execution asynchronously
                executeBatch(linesBatch);
                linesBatch = new ArrayList<>();
            }
        }
        executeBatch(linesBatch);

        threadPool.shutdown();
        return threadPool.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
    }


    AtomicInteger counter = new AtomicInteger();

    void executeBatch(List<String> linesBatch) throws InterruptedException {
        threadPool.getQueue().put(() -> {
            userEntryService.handleCsvLinesBatch(linesBatch);
            LOGGER.debug(String.valueOf(counter.addAndGet(linesBatch.size())));
        });

    }

    private void writeOutputFile(PrintWriter pw, Map<String, Integer> output) {
        pw.println("source,qty");

        output.forEach((key, value) -> {
            pw.print(key);
            pw.print(',');
            pw.println(value);
        });
    }

}
