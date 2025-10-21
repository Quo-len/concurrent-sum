package org.example;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinSum {
    private final SumCalc sumCalc;
    private final int numThreads;

    public ForkJoinSum(SumCalc sumCalc, int numThreads) {
        this.sumCalc = sumCalc;
        this.numThreads = numThreads;
    }

    private class SumTask extends RecursiveTask<Long> {
        private final int startIndex;
        private final int endIndex;

        public SumTask(int startIndex, int endIndex) {
            this.startIndex = startIndex;
            System.out.println(endIndex);
            this.endIndex = endIndex;
        }

        @Override
        protected Long compute() {
            int length = endIndex - startIndex;
            if (length == 1) {
                int[] bounds = sumCalc.getChunkBounds(startIndex);
                return sumCalc.subArrSum(bounds[0], bounds[1]);
            } else {
                int mid = startIndex + length / 2;
                SumTask left = new SumTask(startIndex, mid);
                SumTask right = new SumTask(mid, endIndex);
                left.fork();
                long rightResult = right.compute();
                long leftResult = left.join();
                return leftResult + rightResult;
            }
        }
    }

    public long calculate() {
        ForkJoinPool pool = new ForkJoinPool(numThreads);
        long result = pool.invoke(new SumTask(0, numThreads));
        pool.shutdown();
        return result;
    }
}
