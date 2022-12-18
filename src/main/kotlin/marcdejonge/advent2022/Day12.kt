package marcdejonge.advent2022

import marcdejonge.advent2022.util.aStar

fun main() = DaySolver.printSolutions(::Day12)

class Day12 : DaySolver(12) {
    data class Node(val x: Int, val y: Int, val char: Char) {
        val height = when (char) {
            'S' -> 0
            'E' -> 26
            in 'a'..'z' -> char - 'a'
            else -> error("Unknown height $char")
        }
    }

    private val nodes: List<Node>
    private val width: Int

    init {
        val graph = input.mapIndexed { y, line -> line.mapIndexed { x, char -> Node(x, y, char) } }.toList()
        nodes = graph.flatten()
        width = graph.first().size
    }

    private val validSteps = sequenceOf(-1, 1, -width, width)
    private fun neighbours(from: Node, isReachable: (Int) -> Boolean) = validSteps
        .mapNotNull { dIx -> nodes.getOrNull(from.x + from.y * width + dIx)?.let { it to 1L } }
        .filter { (neighbor, _) -> isReachable(neighbor.height - from.height) }

    private val startNode = nodes.first { it.char == 'S' }
    private val endNode = nodes.first { it.char == 'E' }

    override fun calcPart1() = aStar(startNode, { it == endNode }) { node ->
        neighbours(node) { heightDiff -> heightDiff <= 1 }
    }.asSequence().lastIndex

    override fun calcPart2() = aStar(endNode, { it.height == 0 }) { node ->
        neighbours(node) { heightDiff -> heightDiff >= -1 }
    }.asSequence().lastIndex
}
