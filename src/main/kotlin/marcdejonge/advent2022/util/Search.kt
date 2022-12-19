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
