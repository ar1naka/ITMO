import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class ParallelEvaluator {
    suspend fun run(task: Task, n: Int, context: CoroutineContext) {
        coroutineScope {
            (0 until n).map { i ->
                async(context) {
                    try {
                        task.run(i)
                    } catch (e: Exception) {
                        throw TaskEvaluationException(e)
                    }
                }
            }
        }
    }
}
