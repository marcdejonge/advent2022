package marcdejonge.advent2022.util

import java.util.*

data class AStoreNode<N>(
    val node: N,
    val cameFrom: AStoreNode<N>? = null,
    var score: Long = Long.MAX_VALUE,
    var heuristicScore: Long = score,
) {
    fun asSequence() = generateSequence(this, AStoreNode<N>::cameFrom).map(AStoreNode<N>::node).toList().reversed()
}

inline fun <N> aStar(
    start: N,
    crossinline isGoal: (N) -> Boolean,
    crossinline heuristic: (N) -> Long = { 0L }, // By default we can skip the heuristic, but it's less efficient
    crossinline neighbours: (N) -> Sequence<Pair<N, Long>>,
): AStoreNode<N> {
    val openSet = PriorityQueue<AStoreNode<N>>(compareBy { it.heuristicScore })
    val storedNodes = HashMap<N, AStoreNode<N>>()
    AStoreNode(start, null, 0, heuristic(start)).apply {
        openSet.add(this)
        storedNodes[start] = this
    }

    while (openSet.isNotEmpty()) {
        val current = openSet.remove()
        if (isGoal(current.node)) {
            return current
        }

        neighbours(current.node).forEach { (neighbor, distance) ->
            val tentativeScore = current.score + distance
            val neighborStore = storedNodes[neighbor] ?: AStoreNode(neighbor, current)

            if (tentativeScore < neighborStore.score) {
                // This path to neighbor is better than any previous one. Record it!
                neighborStore.score = tentativeScore
                neighborStore.heuristicScore = tentativeScore + heuristic(neighbor)

                openSet -= neighborStore // Remove first to make sure it's always re-added with the new score
                openSet += neighborStore
                storedNodes.putIfAbsent(neighbor, neighborStore)
            }
        }
    }

    error("Could not find path from $start to the goal")
}
