import kotlinx.coroutines.*

@OptIn(DelicateCoroutinesApi::class)
fun testRunParallel(nThreads: Int, block: suspend CoroutineScope.() -> Unit) {
    var error: Throwable? = null
    runBlocking {
        newFixedThreadPoolContext(nThreads, "ctx").use { ctx ->
            val handler = CoroutineExceptionHandler { _, throwable -> error = throwable }
            val scope = CoroutineScope(ctx + handler)
            scope.block()
            scope
        }
    }
    if (error != null) {
        throw error!!
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun testRunParallel(nThreads: Int, nRuns: Int, block: suspend CoroutineScope.(i: Int) -> Unit) {
    testRunParallel(nThreads) {
        (0..<nRuns).map { i ->
            launch { block(i) }
        }.joinAll()
    }
}
