package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CompletableFutureSum {
    private final SumCalc sumCalc;
    private final int numThreads;

    public CompletableFutureSum(SumCalc sumCalc, int numThreads) {
        this.sumCalc = sumCalc;
        this.numThreads = numThreads;
    }

    public long calculate() {
        List<CompletableFuture<Long>> futures = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            final int index = i;
            CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
                int[] bounds = sumCalc.getChunkBounds(index);
                return sumCalc.subArrSum(bounds[0], bounds[1]);
            }); // uses ForkJoinPool.commonPool()
            futures.add(future);
        }

        CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        all.join();

        long totalSum = futures.stream().mapToLong(CompletableFuture::join).sum();
        return totalSum;
    }
}
