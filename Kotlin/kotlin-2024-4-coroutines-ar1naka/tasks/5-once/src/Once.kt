import kotlinx.coroutines.sync.Mutex

class Once {
    private val mutex = Mutex()
    fun run(block: () -> Unit) {
        if (mutex.tryLock()) {
            block()
        }
    }
}
