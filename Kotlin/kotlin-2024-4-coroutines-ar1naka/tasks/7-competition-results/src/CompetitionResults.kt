import kotlinx.coroutines.flow.*

fun Flow<Cutoff>.resultsFlow(): Flow<Results> {
    return scan(Results(emptyMap())) { x, cutoff ->
        Results(x.results + (cutoff.number to cutoff.time))
    }.filter { it.results.isNotEmpty() }
}

fun Flow<Results>.scoreboard(): Flow<Scoreboard> {
    return map { results ->
        val sortedRes = results.results.toList()
            .sortedBy { (_, time) -> time }
            .mapIndexed { index, (number, time) ->
                ScoreboardRow(index + 1, number, time)
            }
        Scoreboard(sortedRes)
    }
}
