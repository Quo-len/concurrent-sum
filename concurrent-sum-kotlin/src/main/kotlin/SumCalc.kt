package org.example

class SumCalc(val arr: LongArray, numSplits: Int) {
    private val chunkSize: Int = kotlin.math.ceil(arr.size.toDouble() / numSplits).toInt()

    fun subArrSum(start: Int, end: Int): Long {
        var sum = 0L
        for (i in start until end) {
            sum += arr[i]
        }
        return sum
    }

    fun getChunkBounds(index: Int): IntArray {
        val start = index * chunkSize
        val end = kotlin.math.min(start + chunkSize, arr.size)
        return intArrayOf(start, end)
    }
}
