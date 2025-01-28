package org.example.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class Ship implements Runnable {
    private static final Logger logger = LogManager.getLogger(Ship.class);

    private static final int TIMEOUT = 2000;

    private final Port port;
    private final int capacity;

    public Ship(Port port, int capacity) {
        this.port = port;
        this.capacity = capacity;
    }

    @Override
    public void run() {
        try {
            if (port.dockShip()) {
                TimeUnit.MILLISECONDS.sleep(TIMEOUT);
                // Если в порту достаточно груза, загружаем корабль, если нет - разгружаем
                boolean loaded = port.loadUnloadContainers(capacity, true);
                if (loaded) {
                    logger.info("Ship loaded {} containers and departed", capacity);
                } else {
                    TimeUnit.MILLISECONDS.sleep(TIMEOUT);

                    boolean unloaded = port.loadUnloadContainers(capacity, false);
                    if (unloaded) {
                        logger.info("Ship unloaded {} containers and departed", capacity);
                    } else {
                        logger.warn("Ship could not perform any operation and departed");
                    }
                }
                TimeUnit.MILLISECONDS.sleep(TIMEOUT);
                port.releaseDock();
            }
        } catch (InterruptedException e) {
            logger.error("Thread interrupted during ship operation: {}", e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error("Error in ship operation: {}", e.getMessage());
        }
    }
}
