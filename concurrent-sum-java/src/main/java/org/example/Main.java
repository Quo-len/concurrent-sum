package org.example;

import java.util.*;
import java.util.concurrent.*;

public class Main {

    private static long[] getArray(int length) {
        long[] array = new long[length];
        // ThreadLocalRandom rand = ThreadLocalRandom.current();
        SplittableRandom rand = new SplittableRandom(43);
        // Random rand = new Random(43);
        long start = System.nanoTime();
        for (int i = 0; i < length; i++) {
            array[i] = rand.nextLong();
        }
        long end = System.nanoTime();
        double elapsedMillis = (end - start) / 1_000_000.0;
        System.out.printf("Generated %,d longs in %.3f ms%n", length, elapsedMillis);
        return array;
    }

    private static void benchmark(String name, Callable<Long> task) throws Exception {
        long start = System.nanoTime();
        long result = task.call();
        long end = System.nanoTime();
        double elapsedMillis = (end - start) / 1_000_000.0;
        System.out.printf("%s: sum %,d calculated in %.3f ms%n", name, result, elapsedMillis);
    }

    public static void main(String[] args) throws Exception {
        int numThreads = 12;
        int length = 500_000;

        long[] arr = getArray(length);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose method to run: ");
        System.out.println("1 - CompletableFutureSum");
        System.out.println("2 - ExecutorServiceSum");
        System.out.println("3 - ForkJoinSum");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1 -> benchmark("CompletableFutureSum", () ->
                    new CompletableFutureSum(new SumCalc(arr, numThreads), numThreads).calculate()
            );
            case 2 -> benchmark("ExecutorServiceSum", () ->
                    new ExecutorServiceSum(new SumCalc(arr, numThreads), numThreads).calculate()
            );
            case 3 -> benchmark("ForkJoinSum", () ->
                    new ForkJoinSum(new SumCalc(arr, numThreads), numThreads).calculate()
            );
            default -> System.out.println("Invalid choice!");
        }
        scanner.close();
    }
}