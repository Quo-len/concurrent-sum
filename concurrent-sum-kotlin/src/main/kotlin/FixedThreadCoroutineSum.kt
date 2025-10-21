package org.example

import kotlinx.coroutines.*
import java.util.concurrent.Executors

class FixedThreadCoroutineSum(
    private val sumCalc: SumCalc,
    private val numThreads: Int
) {

    private val dispatcher = Executors.newFixedThreadPool(numThreads).asCoroutineDispatcher()

    suspend fun calculate(): Long = coroutineScope {
        try {
            val deferredList = (0 until numThreads).map { index ->
                async(dispatcher) {
                    val bounds = sumCalc.getChunkBounds(index)
                    sumCalc.subArrSum(bounds[0], bounds[1]) // return
                }
            }
            deferredList.awaitAll().sum()
        } finally {
            dispatcher.close()
        }
    }
}
