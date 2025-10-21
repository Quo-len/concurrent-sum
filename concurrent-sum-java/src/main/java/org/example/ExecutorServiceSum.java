package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ExecutorServiceSum {
    private final SumCalc sumCalc;
    private final int numThreads;

    public ExecutorServiceSum(SumCalc sumCalc, int numThreads) {
        this.sumCalc = sumCalc;
        this.numThreads = numThreads;
    }

    public long calculate() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<Long>> futures = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            final int index = i;
            Callable<Long> task = () -> {
                int[] bounds = sumCalc.getChunkBounds(index);
                return sumCalc.subArrSum(bounds[0], bounds[1]);
            };
            futures.add(executor.submit(task));
        }

        long totalSum = 0;
        for (Future<Long> future : futures) {
            totalSum += future.get();
        }
        executor.shutdown();
        return totalSum;
    }
}
