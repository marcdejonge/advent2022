package marcdejonge.advent2022

import marcdejonge.advent2022.util.GifSequenceWriter
import marcdejonge.advent2022.util.IntGrid
import marcdejonge.advent2022.util.LineSegment.Companion.lineSegments
import marcdejonge.advent2022.util.Vec2

fun main() = DaySolver.printSolutions(::Day14)

class Day14 : DaySolver(14, true) {
    private val lines = input.flatMap { line ->
        line.splitToSequence(" -> ").map(Vec2::valueOf).lineSegments()
    }.toList()
    private val maxHeight = (lines.maxOf { it.yRange.last } + 2).toInt()
    private val initialGrid = IntGrid((500 - maxHeight)..(500 + maxHeight), 0..maxHeight)

    init {
        lines.forEach { line ->
            for (x in line.xRange) {
                for (y in line.yRange) {
                    initialGrid[x, y] = LINE
                }
            }
        }
    }

    private fun IntGrid.dropSandFrom(start: Vec2): Vec2? {
        if (this[start] != EMPTY) return null // Already filled up, can't drop anything anymore!
        var (x, y) = start
        while (true) {
            when {
                this[x, y + 1] == EMPTY -> y++
                this[x - 1, y + 1] == EMPTY -> y++ + x--
                this[x + 1, y + 1] == EMPTY -> y++ + x++
                else -> break
            }
        }
        this[x, y] = SAND
        return Vec2(x, y)
    }

    private val startLocation = Vec2(500, 0)
    private val IntGrid.sandDroppingSequence get() = generateSequence { dropSandFrom(startLocation) }

    override fun calcPart1() = with(initialGrid.deepCopy()) {
        sandDroppingSequence.takeWhile { (_, y) -> y < initialGrid.yRange.last }.count()
    }

    override fun calcPart2() = with(initialGrid.deepCopy()) {
        for (x in xRange) this[x, yRange.last] = LINE
        sandDroppingSequence.count()
    }

    override fun generateAnimation(writer: GifSequenceWriter) = with(initialGrid.deepCopy()) {
        for (x in xRange) this[x, yRange.last] = LINE
        generateSequence { dropSandFrom(startLocation) }.forEachIndexed { ix, place ->
            if (ix % 55 == 0) {
                writer.writeToSequence(render(place))
            }
        }
    }

    companion object {
        private const val EMPTY = 0
        private const val SAND = 1
        private const val LINE = 2

        fun IntGrid.render(justDropped: Vec2?) = render { place, type ->
            if (place == justDropped) 0xff0000
            else when (type) {
                SAND -> 0xc2b280
                LINE -> 0xffffff
                else -> 0x000000
            }
        }
    }
}
