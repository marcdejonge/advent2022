package marcdejonge.advent2022.util

inline fun <T, R> depthFirstSearch(
    start: T,
    initial: R,
    crossinline next: T.() -> Sequence<T>,
    crossinline visit: T.(R) -> R = { it }
): Map<T, R> = search(start, initial, next, visit, ArrayDeque<T>::removeLast)

inline fun <T, R> breadFirstSearch(
    start: T,
    initial: R,
    crossinline next: T.() -> Sequence<T>,
    crossinline visit: T.(R) -> R = { it }
): Map<T, R> = search(start, initial, next, visit, ArrayDeque<T>::removeFirst)

inline fun <T, R> search(
    start: T,
    initial: R,
    crossinline next: T.() -> Sequence<T>,
    crossinline visit: T.(R) -> R = { it },
    crossinline nextFromQueue: ArrayDeque<T>.() -> T
): Map<T, R> {
    val queue = ArrayDeque<T>()
    queue.add(start)
    val visited = HashMap<T, R>()
    visited[start] = initial

    while (queue.isNotEmpty()) {
        val current = nextFromQueue(queue)
        for (nextNode in next(current)) {
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
    crossinline visitNext: T.() -> Boolean = { true },
) = search(start, nextSequence, visitNext, ArrayDeque<T>::removeLast)

inline fun <T> breadFirstSearch(
    start: T,
    crossinline nextSequence: T.() -> Sequence<T>,
    crossinline visitNext: T.() -> Boolean = { true },
) = search(start, nextSequence, visitNext, ArrayDeque<T>::removeFirst)

inline fun <T> search(
    start: T,
    crossinline nextSequence: T.() -> Sequence<T>,
    crossinline visitNext: T.() -> Boolean = { true },
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

inline fun <T> findMaxNonOverlappingCombinations(
    input: Iterable<T>,
    bitCount: Int = 8,
    crossinline getBitMask: T.() -> Long,
    crossinline getScore: T.() -> Int
): Pair<T, T> {
    val splitMask = (1 shl bitCount) - 1
    val buckets = Array<ArrayList<T>>(splitMask + 1) { ArrayList() }
    input.sortedByDescending(getScore).forEach { buckets[getBitMask(it).toInt() and splitMask].add(it) }

    var max = 0
    var maxFirstItem: T? = null
    var maxSecondItem: T? = null
    for (firstIx in buckets.indices) {
        for (firstItem in buckets[firstIx]) {
            for (secondIx in (firstIx + 1)..splitMask) {
                if ((getBitMask(firstItem).toInt() and secondIx) != 0) continue // Skip any group that overlaps with me

                for (secondItem in buckets[secondIx]) {
                    val score = getScore(firstItem) + getScore(secondItem)
                    if (score < max) break
                    if (getBitMask(firstItem) and getBitMask(secondItem) == 0L) {
                        max = score
                        maxFirstItem = firstItem
                        maxSecondItem = secondItem
                        break
                    }
                }
            }
        }
    }

    if (maxFirstItem == null || maxSecondItem == null) error("Could not find any pair")
    return maxFirstItem to maxSecondItem
}
