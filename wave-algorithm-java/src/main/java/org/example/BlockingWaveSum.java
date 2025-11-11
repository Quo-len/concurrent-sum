package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockingWaveSum {

    private final long[] array;
    private final int workerCount;
    private volatile int currentLength;

    private final BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
    private final List<Thread> workers = new ArrayList<>();
    private CountDownLatch waveLatch;

    public BlockingWaveSum(long[] array, int workerCount) {
        this.array = array;
        this.currentLength = array.length;
        this.workerCount = Math.max(1, workerCount);
    }

    public long run() throws InterruptedException {
        startWorkers();

        int activeLength = currentLength;
        int num = 0;

        while (activeLength > 1) {
            System.out.println((++num) + " хвиля:");
            int pairCount = activeLength / 2;
            if (pairCount == 0) {
                break;
            }

            currentLength = activeLength;
            waveLatch = new CountDownLatch(pairCount);

            for (int i = 0; i < pairCount; i++) {
                queue.add(i);
            }

            waveLatch.await();
            activeLength  = (activeLength + 1) / 2;
            System.out.println("Проміжний результат: " + Arrays.toString(Arrays.copyOf(array, activeLength)));
        }


        for (int i = 0; i < workerCount; i++) {
            queue.add(-1);
        }

        for (Thread thread : workers) {
            thread.join();
        }

        return array[0];
    }

    private void startWorkers() {
        for (int i = 0; i < workerCount; i++) {
            Thread thread = new Thread(new Worker(i));
            thread.start();
            workers.add(thread);
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
                while (true) {
                    int index = queue.take();
                    if(index == -1) {
                        break;
                    }
                    int end = currentLength - index - 1;
                    System.out.println(Thread.currentThread().getName() + " performed: " + index + ":" + array[index] + " + " + end + ":" + array[end] + ";");
                    array[index] += array[end];
                    Thread.sleep(100);
                    waveLatch.countDown();
                }
                System.out.println(Thread.currentThread().getName() + " stopped working.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
