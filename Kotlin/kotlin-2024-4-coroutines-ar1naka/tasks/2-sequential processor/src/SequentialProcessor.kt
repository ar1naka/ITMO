import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext

class SequentialProcessor(private val handler: (String) -> String) : TaskProcessor {
    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    private val thread = newSingleThreadContext("thread")

    override suspend fun process(argument: String): String {
        return withContext(thread) {
            handler(argument)
        }
    }

    override fun close() {
        thread.close()
    }
}
