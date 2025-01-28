package org.example.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Port {
    private static final Logger logger = LogManager.getLogger(Port.class);
    private static Port instance;
    private static final Lock lock = new ReentrantLock();

    private final int capacity;
    private final Semaphore docks;
    private int currentLoad;
    private final Lock warehouseLock;

    private Port(int capacity, int dockCount) {
        this.capacity = capacity;
        this.docks = new Semaphore(dockCount);
        this.currentLoad = 0;
        this.warehouseLock = new ReentrantLock();
    }

    public static Port getInstance(int capacity, int dockCount) {
        if (instance == null) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = new Port(capacity, dockCount);
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    public boolean dockShip() {
        try {
            docks.acquire();
            logger.info("Dock acquired by a ship");
            return true;
        } catch (InterruptedException e) {
            logger.error("Failed to acquire dock: {}", e.getMessage());
            return false;
        }
    }

    public void releaseDock() {
        docks.release();
        logger.info("Dock released by a ship");
    }

    public boolean loadUnloadContainers(int shipLoad, boolean isLoading) {
        warehouseLock.lock();
        try {
            if (isLoading) {
                if (currentLoad >= shipLoad) {
                    currentLoad -= shipLoad;
                    logger.info("Loaded {} containers from port. Current load: {}", shipLoad, currentLoad);
                    return true;
                } else {
                    logger.warn("Not enough containers to load. Current load: {}", currentLoad);
                    return false;
                }
            } else {
                if (currentLoad + shipLoad <= capacity) {
                    currentLoad += shipLoad;
                    logger.info("Unloaded {} containers to port. Current load: {}", shipLoad, currentLoad);
                    return true;
                } else {
                    logger.warn("Not enough space to unload. Current load: {}", currentLoad);
                    return false;
                }
            }
        } finally {
            warehouseLock.unlock();
        }
    }
}
