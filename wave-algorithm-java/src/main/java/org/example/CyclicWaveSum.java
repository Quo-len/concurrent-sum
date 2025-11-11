package org.example;

import java.util.Arrays;
import java.util.concurrent.*;

public class CyclicWaveSum {

    private final long[] array;
    private final int workerCount;
    private volatile int currentLength;

    private CyclicBarrier barrier;
    private volatile boolean finished = false;

    public CyclicWaveSum(long[] array, int workerCount) {
        this.array = array;
        this.currentLength = array.length;
        this.workerCount = Math.max(1, workerCount);
    }

    public long run() throws InterruptedException {
        int pairCount = currentLength / 2;
        barrier = new CyclicBarrier(workerCount, this::finishWave);

        Thread[] workers = new Thread[workerCount];
        for (int i = 0; i < workerCount; i++) {
            workers[i] =  new Thread(new Worker(i));
            workers[i].start();
        }

        for (Thread thread : workers) {
            thread.join();
        }

        return array[0];
    }

    private void finishWave() {
        if (currentLength <= 1 || finished) return;

        currentLength = (currentLength + 1) / 2;
        System.out.println("Проміжний результат: " +
                Arrays.toString(Arrays.copyOf(array, currentLength)));

        if (currentLength <= 1) {
            finished = true;
            System.out.println("Фінальний результат: " + array[0]);
        }
    }

    private class Worker implements Runnable {
        private final int id;

        Worker(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (!finished) {
                    int pairCount = currentLength / 2;
                    for (int i = id; i < pairCount; i += workerCount) {
                        int end = currentLength - 1 - i;
                        System.out.println(Thread.currentThread().getName() + " performed: " + i + ":" + array[i] + " + " + end + ":" + array[end] + ";");
                        array[i] += array[end];
                        Thread.sleep(100);
                    }
                    barrier.await();
                }
                System.out.println(Thread.currentThread().getName() + " stopped working.");
            } catch (BrokenBarrierException e) {
                Thread.currentThread().interrupt();

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
