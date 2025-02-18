import java.lang.Exception
import kotlinx.coroutines.*

fun CoroutineScope.runApplication(
    runUI: suspend () -> Unit,
    runApi: suspend () -> Unit,
) {
    launch {
        while (true) {
            try {
                runApi()
                break
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                delay(1000)
            }
        }
    }

    launch {
        runUI()
    }
}
