package ru.otus.d13;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;


public class MainClass {

    private static final Logger log = LoggerFactory.getLogger(MainClass.class);

    private int runningThreadFlag = 2;
    public final int SLEEP_TIME = 300;


    public static void main(String[] args) {
        new MainClass().simpleExecutorInWork();
    }

    private synchronized void counting(int num, int delta) throws Exception {

        int start = (delta < 0) ? 10 : 1;
        for (int i = start; i > 1 && delta < 0 || i < 10 && delta > 0; i += delta) {

            while (runningThreadFlag == num) {
                this.wait();
            }
            log.info("{} - {}", Thread.currentThread().getName(), i);
            sleep();
            runningThreadFlag = num;
            this.notifyAll();

        }

    }


    private void simpleExecutorInWork() {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        runSimpleExecutor(executor, 1);
        runSimpleExecutor(executor, 2);

    }

    private void runSimpleExecutor(ExecutorService executor, int num) {
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
        while (!Thread.currentThread().isInterrupted()) {
            counting(num,direction);
            direction = -direction;
        }
    }

/////////////////////////////////////////////////////////////////////////////// sleep
    private void sleep() {
        try {
            Thread.sleep(TimeUnit.MILLISECONDS.toMillis(SLEEP_TIME));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
