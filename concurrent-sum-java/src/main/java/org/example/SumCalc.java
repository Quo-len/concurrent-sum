package org.example;

public class SumCalc {
    private final long[] arr;
    private final int chunkSize;

    public SumCalc(long[] arr, int numSplits) {
        this.arr = arr;
        this.chunkSize = (int) Math.ceil((double) arr.length / numSplits);
    }

    public long subArrSum(int start, int end) {
        long sum = 0;
        for (int i = start; i < end; i++) {
            sum += arr[i];
        }
        return sum;
    }

    public int[] getChunkBounds(int index) {
        int start = index * chunkSize;
        int end = Math.min(start + chunkSize, arr.length);
        return new int[]{start, end};
    }

}
