private const val milliseconds_in_second = 1000
private const val seconds_in_minute = 60
private const val minutes_in_hour = 60

val Int.milliseconds: Time
    get() = calculateTime(0, this)

val Int.seconds: Time
    get() = this.milliseconds * milliseconds_in_second

val Int.minutes: Time
    get() = this.seconds * seconds_in_minute

val Int.hours: Time
    get() = this.minutes * minutes_in_hour

operator fun Time.plus(other: Time): Time {
    return calculateTime(seconds + other.seconds, milliseconds + other.milliseconds)
}

operator fun Time.minus(other: Time): Time {
    return calculateTime(seconds - other.seconds, milliseconds - other.milliseconds)
}

operator fun Time.times(times: Int): Time {
    return calculateTime(seconds * times, milliseconds * times)
}

private fun calculateTime(totalSeconds: Long, totalMilliseconds: Int): Time {
    return Time(
        totalSeconds + totalMilliseconds / milliseconds_in_second,
        totalMilliseconds % milliseconds_in_second,
    )
}
