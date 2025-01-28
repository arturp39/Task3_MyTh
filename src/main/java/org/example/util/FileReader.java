package org.example.util;

import org.example.entity.Port;
import org.example.entity.Ship;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileReader {
    private static final Logger logger = LogManager.getLogger(FileReader.class);

    public static Port readPortConfiguration(String filePath) {
        try {
            List<String> config = Files.readAllLines(Path.of(filePath))
                    .stream()
                    .filter(line -> !line.trim().startsWith("#") && !line.trim().isEmpty()) // Ignore comments and empty lines
                    .toList();

            if (config.size() < 2) {
                throw new IllegalArgumentException("Config file must have at least 2 lines: port capacity and dock count");
            }

            int portCapacity = Integer.parseInt(config.get(0));
            int dockCount = Integer.parseInt(config.get(1));

            logger.info("Port capacity: {}, Dock count: {}", portCapacity, dockCount);
            return Port.getInstance(portCapacity, dockCount);
        } catch (IOException | NumberFormatException e) {
            logger.error("Error reading port configuration: {}", e.getMessage());
            throw new RuntimeException("Port configuration could not be read", e);
        }
    }

    public static List<Ship> readShips(String filePath, Port port) {
        try {
            List<String> config = Files.readAllLines(Path.of(filePath))
                    .stream()
                    .filter(line -> !line.trim().startsWith("#") && !line.trim().isEmpty()) // Ignore comments and empty lines
                    .toList();

            if (config.size() <= 2) {
                throw new IllegalArgumentException("Config file must have ship capacities after port capacity and dock count");
            }

            // Ship capacities start from the 3rd line onward
            List<Ship> ships = new ArrayList<>();
            for (int i = 2; i < config.size(); i++) {
                int shipCapacity = Integer.parseInt(config.get(i));
                ships.add(new Ship(port, shipCapacity));
                logger.info("Created ship with capacity: {}", shipCapacity);
            }

            logger.info("Total ships created: {}", ships.size());
            return ships;
        } catch (IOException | NumberFormatException e) {
            logger.error("Error reading ships configuration: {}", e.getMessage());
            throw new RuntimeException("Ships configuration could not be read", e);
        }
    }
}
