interface Value<T> {
    var value: T
    fun observe(observer: (T) -> Unit): Cancellation
}

fun interface Cancellation {
    fun cancel()
}

class MutableValue<T>(initialValue: T) : Value<T> {
    private val observers = mutableSetOf<(T) -> Unit>()
    private var newValue: T = initialValue

    override var value: T
        get() = newValue
        set(value) {
            newValue = value
            observers.forEach { it(newValue) }
        }

    override fun observe(observer: (T) -> Unit): Cancellation {
        observers.add(observer)
        observer(newValue)
        return Cancellation { observers.remove(observer) }
    }
}
