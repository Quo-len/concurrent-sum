package org.example;

import java.util.Scanner;
import java.util.SplittableRandom;
import java.util.concurrent.Callable;

public class Main {

    private static long[] getArray(int length) {
        long[] array = new long[length];
        SplittableRandom rand = new SplittableRandom(42);
        for (int i = 0; i < length; i++) {
            array[i] = rand.nextLong();
        }
        return array;
    }

    public static void printArray(long[] array, int i, int j) {
        for (int k = i; k < j; k++) {
            System.out.print(array[k] + (k < j - 1 ? ", " : ""));
        }
    }

    public static long singleThread(long[] array) throws InterruptedException {
        int currentSize = array.length;
        int waveNum = 0;

        System.out.println("Початковий масив: ");
        printArray(array, 0, currentSize);
        System.out.println();

        while (currentSize > 1) {
            System.out.println((waveNum + 1) + " хвиля:");
            int half = (currentSize + 1) / 2;
            System.out.println("bounds: " + 0 + " " + half);
            for (int k = 0; k < currentSize / 2; k++) {
                System.out.println(array[k] + " + " + array[currentSize - k - 1] + ";");
                array[k] += array[currentSize - k - 1];
                Thread.sleep(100);
            }

            System.out.println("Результат - ");
            System.out.print("[");
            printArray(array, 0, half);
            System.out.print("] - актуальна частина, ");
            System.out.print("{");
            printArray(array, half, array.length);
            System.out.print("} - \"відпрацьовані\" елементи.\n");

            currentSize = half;
            waveNum++;
        }
        printArray(array, 0, array.length);
        System.out.println("\n" + array[0] + " - сума елементів масиву");
        return array[0];
    }

    private static void benchmark(String name, Callable<Long> task) throws Exception {
        long start = System.nanoTime();
        long result = task.call();
        long end = System.nanoTime();
        double elapsedMillis = (end - start) / 1_000_000.0;
        System.out.printf("%s: sum %,d calculated in %.3f ms%n", name, result, elapsedMillis);
    }

    public static void main(String[] args) throws Exception {
        long[] array = getArray(1000);
        int numThreads = 16;
        System.out.println("Кількість потоків: " + numThreads);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose method to run: ");
        System.out.println("1 - Single Thread");
        System.out.println("2 - BlockingWaveSum");
        System.out.println("3 - CyclicWaveSum");
        System.out.println("4 - ExecutorWaveSum");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1 -> benchmark("singleThread", () ->
                    singleThread(array)
            );
            case 2 -> benchmark("BlockingWaveSum", () ->
                    new BlockingWaveSum(array, numThreads).run()
            );
            case 3 -> benchmark("CyclicWaveSum", () ->
                    new CyclicWaveSum(array, numThreads).run()
            );
            case 4 -> benchmark("ExecutorWaveSum", () ->
                    new ExecutorWaveSum(array, numThreads).run()
            );
            default -> System.out.println("Invalid choice!");
        }
        scanner.close();
    }
}
