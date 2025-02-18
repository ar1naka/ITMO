import org.junit.Assert

inline fun <reified T> assertThrows(message: String? = null, body: () -> Unit): T {
    val result = runCatching { body() }
    Assert.assertTrue(message, result.isFailure)
    val e = result.exceptionOrNull()
    Assert.assertTrue(message, e is T)
    return e as T
}

fun assertThrows(message: String? = null, body: () -> Unit) = assertThrows<Exception>(message, body)
