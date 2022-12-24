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

fun <T> Sequence<T>.every(skip: Int) = mapIndexedNotNull { ix, item -> if (ix % skip == 0) item else null }