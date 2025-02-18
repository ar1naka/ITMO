sealed interface IntResult {
    data class Ok(val value: Int) : IntResult
    data class Error(val reason: String) : IntResult

    fun getOrDefault(default: Int): Int {
        return when (this) {
            is Ok -> this.value
            is Error -> default
        }
    }

    fun getOrNull(): Int? {
        return when (this) {
            is Ok -> this.value
            is Error -> null
        }
    }

    fun getStrict(): Int {
        return when (this) {
            is Ok -> this.value
            is Error -> throw NoResultProvided(this.reason)
        }
    }
}

class NoResultProvided(reason: String) : NoSuchElementException(reason)

fun safeRun(unsafe: () -> Int): IntResult = try {
    IntResult.Ok(unsafe())
} catch (e: Exception) {
    IntResult.Error(e.message ?: "Error")
}
