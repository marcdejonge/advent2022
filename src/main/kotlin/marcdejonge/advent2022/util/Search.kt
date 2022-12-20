package marcdejonge.advent2022.util

inline fun <T, R> depthFirstSearch(
    start: T,
    initial: R,
    crossinline next: T.() -> Sequence<T>,
    crossinline visit: T.(R) -> R
): Map<T, R> = search(start, initial, next, visit, ArrayDeque<T>::removeLast)

inline fun <T, R> breadFirstSearch(
    start: T,
    initial: R,
    crossinline next: T.() -> Sequence<T>,
    crossinline visit: T.(R) -> R
): Map<T, R> = search(start, initial, next, visit, ArrayDeque<T>::removeFirst)

inline fun <T, R> search(
    start: T,
    initial: R,
    crossinline next: T.() -> Sequence<T>,
    crossinline visit: T.(R) -> R,
    crossinline nextFromQueue: ArrayDeque<T>.() -> T
): Map<T, R> {
    val queue = ArrayDeque<T>()
    queue.add(start)
    val visited = HashMap<T, R>()
    visited[start] = initial

    while (queue.isNotEmpty()) {
        val current = nextFromQueue(queue)
        next(current).forEach { nextNode ->
            if (!visited.contains(nextNode)) {
                visited[nextNode] = visit(nextNode, visited[current]!!)
                queue.add(nextNode)
            }
        }
    }

    return visited
}

inline fun <T> depthFirstSearch(
    start: T,
    crossinline nextSequence: T.() -> Sequence<T>,
    crossinline visitNext: T.() -> Boolean,
) = search(start, nextSequence, visitNext, ArrayDeque<T>::removeLast)

inline fun <T> breadFirstSearch(
    start: T,
    crossinline nextSequence: T.() -> Sequence<T>,
    crossinline visitNext: T.() -> Boolean,
) = search(start, nextSequence, visitNext, ArrayDeque<T>::removeFirst)

inline fun <T> search(
    start: T,
    crossinline nextSequence: T.() -> Sequence<T>,
    crossinline visitNext: T.() -> Boolean,
    crossinline nextFromQueue: ArrayDeque<T>.() -> T
) {
    val queue = ArrayDeque<T>()
    queue.add(start)

    while (queue.isNotEmpty()) {
        val current = nextFromQueue(queue)
        nextSequence(current).forEach { nextNode ->
            if (visitNext(nextNode)) {
                queue.add(nextNode)
            }
        }
    }
}

inline fun <T> findBiggestCombination(
    input: Iterable<T>,
    bitCount: Int = 8,
    crossinline getMark: T.() -> Long,
    crossinline getScore: T.() -> Int
): Int {
    val splitMask = (1L shl bitCount) - 1
    val searchGroups = input.sortedByDescending(getScore).groupBy {
        getMark(it) and splitMask
    }

    var max = 0
    for ((thisGroupIx, thisGroup) in searchGroups) {
        for (thisItem in thisGroup) {
            for (otherGroupIx in (thisGroupIx + 1)..splitMask) {
                if ((getMark(thisItem) and otherGroupIx) != 0L) continue // Skip any group that overlaps with me
                val otherGroup = searchGroups[otherGroupIx] ?: continue // Skip any group that doesn't exist

                for (otherItem in otherGroup) {
                    val score = getScore(thisItem) + getScore(otherItem)
                    if (score < max) break
                    if (getMark(thisItem) and getMark(otherItem) == 0L) {
                        max = score
                        break
                    }
                }
            }
        }
    }

    return max
}
