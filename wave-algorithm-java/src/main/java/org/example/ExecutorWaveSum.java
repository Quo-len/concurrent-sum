package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class ExecutorWaveSum {

    private final long[] array;
    private volatile int currentLength;
    private final ExecutorService executor;

    public ExecutorWaveSum(long[] array, int workerCount) {
        this.array = array;
        this.currentLength = array.length;
        workerCount = Math.max(1, workerCount);
        this.executor = Executors.newFixedThreadPool(workerCount);
    }

    public long run() throws InterruptedException {
        int wave = 0;

        while (currentLength > 1) {
            System.out.println((++wave) + " хвиля:");
            int pairCount = currentLength/2;
            if (pairCount == 0) break;

            List<Callable<Void>> tasks = new ArrayList<>();
            for (int i = 0; i < pairCount; i++) {
                final int index = i;
                tasks.add(() -> {
                    int end = currentLength - 1 - index;
                    System.out.println(Thread.currentThread().getName() + " performed: " + index + ":" + array[index] + " + " + end + ":" + array[end] + ";");
                    array[index] += array[end];
                    Thread.sleep(100);
                    return  null;
                });
            }

            executor.invokeAll(tasks);
            
            currentLength = (currentLength + 1) / 2;

            System.out.println("Проміжний результат: " +
                    Arrays.toString(Arrays.copyOf(array, currentLength)));
            System.out.println();
        }
        executor.shutdown();
        return array[0];
    }
}
