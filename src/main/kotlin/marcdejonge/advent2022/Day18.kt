package marcdejonge.advent2022

import marcdejonge.advent2022.util.Vec3
import marcdejonge.advent2022.util.depthFirstSearch

fun main() = DaySolver.printSolutions(::Day18)

class Day18 : DaySolver(18) {
    private val coordinates = input.map(Vec3.Companion::valueOf).toSet()
    private val directions = listOf(Vec3(x = 1), Vec3(x = -1), Vec3(y = 1), Vec3(y = -1), Vec3(z = 1), Vec3(z = -1))

    override fun calcPart1() = coordinates.sumOf { block -> directions.count { !coordinates.contains(block - it) } }

    override fun calcPart2(): Int {
        val min = Vec3(coordinates.minOf(Vec3::x) - 1, coordinates.minOf(Vec3::y) - 1, coordinates.minOf(Vec3::z) - 1)
        val max = Vec3(coordinates.maxOf(Vec3::x) + 1, coordinates.maxOf(Vec3::y) + 1, coordinates.maxOf(Vec3::z) + 1)

        val outsideBlocks = depthFirstSearch(min, 1, {
            directions.asSequence().map { this + it }
                .filter { it.between(min, max) && !coordinates.contains(it) }
        })

        return coordinates.sumOf { block ->
            directions.count { outsideBlocks.containsKey(block - it) }
        }
    }
}