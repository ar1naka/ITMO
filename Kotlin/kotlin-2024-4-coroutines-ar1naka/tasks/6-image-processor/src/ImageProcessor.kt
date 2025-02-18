import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

typealias ImageGenerator = suspend (query: String) -> ByteArray

class ImageProcessor(
    private val parallelism: Int,
    private val requests: ReceiveChannel<String>,
    private val publications: SendChannel<Pair<String, ByteArray>>,
    private val generator: ImageGenerator,
) {
    private val uniqueRequests = Channel<String>()

    fun run(scope: CoroutineScope) {
        scope.launch {
            val workers = createWorkers(scope)

            val set = mutableSetOf<String>()
            for (request in requests) {
                if (request !in set) {
                    set.add(request)
                    uniqueRequests.send(request)
                }
            }
            uniqueRequests.close()

            workers.forEach { it.join() }
            publications.close()
        }
    }

    private fun createWorkers(scope: CoroutineScope): List<Job> {
        return List(parallelism) {
            scope.launch {
                for (request in uniqueRequests) {
                    val result = generator(request)
                    publications.send(request to result)
                }
            }
        }
    }
}
