package ru.otus.d13;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class MainClass {

    private static final Logger log = LoggerFactory.getLogger(MainClass.class);

    private static int runningThreadFlag = 2;
    public static final int SLEEP_TIME = 300;


    public static void main(String[] args) {
//        new MainClass().executorInWork();
        new MainClass().simpleExecutorInWork();
    }

    private void executorInWork() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

        run(executor, 1);
        run(executor, 2);

    }

    private void run(ScheduledExecutorService executor, int num) {
        final AtomicInteger delta = new AtomicInteger(1);
        executor.scheduleAtFixedRate(() -> {
            try {
                counting(num, delta.getAndSet(delta.get() * -1));
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private synchronized void counting(int num, int delta) throws Exception {

        int start = (delta < 0) ? 10 : 1;
        for (int i = start; i > 1 && delta < 0 || i < 10 && delta > 0; i += delta) {

            while (runningThreadFlag == num) {
                this.wait();
            }
            log.info(String.format("%s - %d", Thread.currentThread().getName(), i));
            sleep();
            runningThreadFlag = num;
            this.notifyAll();

        }

    }

/////////////////////////////////////////////////////////////////////////////// обычный executor

    private void simpleExecutorInWork() {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        runSimpleExecutor(executor, 1);
        runSimpleExecutor(executor, 2);

    }

    private void runSimpleExecutor(ExecutorService executor, int num) {
        final AtomicInteger delta = new AtomicInteger(1);
        executor.execute(()-> {
            try {
                countingSimple(num);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });
    }

    private void countingSimple(int num) throws Exception {
        int direction = 1;
        while (true) {
            counting(num,direction);
            direction = -direction;
        }
    }

/////////////////////////////////////////////////////////////////////////////// sleep
    private static void sleep() {
        try {
            Thread.sleep(TimeUnit.MILLISECONDS.toMillis(SLEEP_TIME));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
