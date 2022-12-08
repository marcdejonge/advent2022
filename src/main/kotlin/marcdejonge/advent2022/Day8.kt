package marcdejonge.advent2022

import marcdejonge.advent2022.util.bothWays
import kotlin.math.abs

fun main() = DaySolver.printSolutions(::Day8)

class Day8 : DaySolver(8) {
    private val treeHeights: IntArray
    private val gridHeight: Int
    private val gridWidth: Int

    init {
        val heights = input.map { line -> IntArray(line.length) { ix -> line[ix] - '0' } }.toList()
        gridHeight = heights.size
        gridWidth = heights[0].size
        if (heights.any { it.size != gridWidth }) error("Expecting a rectangle grid")
        treeHeights = heights.flatMap { it.toList() }.toIntArray()
    }

    private inline fun iterateTrees(eachLine: (Int) -> Unit, eachItem: (Int, Int) -> Unit) {
        iterateVertically(eachLine, eachItem)
        iterateHorizontally(eachLine, eachItem)
    }

    private inline fun iterateHorizontally(eachLine: (Int) -> Unit, eachItem: (Int, Int) -> Unit) =
        (0 until gridHeight).forEach { y ->
            (0 until gridWidth).bothWays {
                eachLine(first)
                forEach { x -> eachItem(y * gridWidth + x, x) }
            }
        }

    private inline fun iterateVertically(eachLine: (Int) -> Unit, eachItem: (Int, Int) -> Unit) =
        (0 until gridWidth).forEach { x ->
            (0 until gridHeight).bothWays {
                eachLine(first)
                forEach { y -> eachItem(y * gridWidth + x, y) }
            }
        }

    override fun calcPart1(): Int {
        val canSeeEdge = BooleanArray(treeHeights.size)
        var max = -1
        iterateTrees(eachLine = { max = -1 }) { ix, _ ->
            if (treeHeights[ix] > max) {
                canSeeEdge[ix] = true
                max = treeHeights[ix]
            }
        }
        return canSeeEdge.count { it }
    }

    override fun calcPart2(): Long {
        val scores = LongArray(treeHeights.size) { 1 }
        val lastSeen = IntArray(10)
        iterateTrees(eachLine = { firstIx ->
            lastSeen.forEachIndexed { ix, _ -> lastSeen[ix] = firstIx }
        }) { ix, column ->
            scores[ix] *= abs(column - lastSeen[treeHeights[ix]]).toLong()
            for (blockedHeight in 0..treeHeights[ix]) lastSeen[blockedHeight] = column
        }
        return scores.max()
    }
}
