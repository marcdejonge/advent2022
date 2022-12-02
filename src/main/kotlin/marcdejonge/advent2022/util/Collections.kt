package marcdejonge.advent2022.util

fun <T> List<T>.chunkBy(predicate: (T) -> Boolean): List<List<T>> =
    fold(mutableListOf(mutableListOf<T>())) { acc, item ->
        acc.apply {
            if (predicate(item)) add(mutableListOf())
            else last().add(item)
        }
    }
