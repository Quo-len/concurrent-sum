package org.example

import java.util.*
import kotlinx.coroutines.runBlocking
import kotlin.system.measureNanoTime

fun getArray(length: Int): LongArray {
    val array = LongArray(length)
    val rand = SplittableRandom(43L)
    val elapsed = measureNanoTime {
        for (i in array.indices) {
            array[i] = rand.nextLong()
        }
    }
    println("Generated %,d longs in %.3f ms".format(length, elapsed / 1_000_000.0))
    return array
}

fun benchmark(name: String, task: () -> Long) {
    val (result, elapsed) = run {
        var res = 0L
        val time = measureNanoTime {
            res = task()
        }
        res to time
    }
    println("%s: sum %,d calculated in %.3f ms".format(name, result, elapsed / 1_000_000.0))
}

fun main() = runBlocking {
    val numThreads = 12
    val length = 500_000

    val arr = getArray(length)
    val scanner = Scanner(System.`in`)
    println("Choose method to run: ")
    println("1 - CoroutineSum")
    println("2 - FixedThreadCoroutineSum")
    val choice = scanner.nextInt()

    when (choice) {
        1 -> benchmark("CoroutineSum") {
            runBlocking { CoroutineSum(SumCalc(arr, numThreads), numThreads).calculate() }
        }
        2 -> benchmark("FixedThreadCoroutineSum") {
            runBlocking { FixedThreadCoroutineSum(SumCalc(arr, numThreads), numThreads).calculate() }
        }
        else -> println("Invalid choice!")
    }
    scanner.close()
}
