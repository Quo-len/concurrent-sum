package org.example

import kotlinx.coroutines.*

class CoroutineSum(private val sumCalc: SumCalc, private val numThreads: Int) {

    suspend fun calculate(): Long = coroutineScope {
        val deferredList = (0 until numThreads).map { index ->
            async(Dispatchers.Default) {
                val bounds = sumCalc.getChunkBounds(index)
                sumCalc.subArrSum(bounds[0], bounds[1])
            }
        }
        deferredList.awaitAll().sum()
    }
}
