package org.example.main;

import org.example.entity.Port;
import org.example.entity.Ship;
import org.example.util.FileReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        String filePath = "src/main/resources/config.txt";

        Port port = FileReader.readPortConfiguration(filePath);
        List<Ship> ships = FileReader.readShips(filePath, port);

        try (ExecutorService executor = Executors.newFixedThreadPool(ships.size())) {
            ships.forEach(executor::submit);

            executor.shutdown();
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                logger.error("Executor was interrupted: {}", e.getMessage());
            }
        }

        logger.info("Ship servicing completed");
    }
}
