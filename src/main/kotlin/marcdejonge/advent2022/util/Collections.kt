package marcdejonge.advent2022.util

fun <T> Sequence<T>.chunkBy(predicate: (T) -> Boolean): Sequence<List<T>> {
    val it = iterator()
    return generateSequence {
        val list = mutableListOf<T>()
        while (it.hasNext()) {
            val next = it.next()
            if (predicate(next)) break
            list.add(next)
        }
        if (list.isEmpty() && !it.hasNext()) null else list
    }
}