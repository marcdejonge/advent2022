package marcdejonge.advent2022

import marcdejonge.advent2022.util.Vec2
import kotlin.math.abs

fun main() = DaySolver.printSolutions(::Day15)

class Day15 : DaySolver(15) {
    data class Sensor(val pos: Vec2, private val closestBeacon: Vec2) {
        val dist = closestBeacon.manhattanDistance(pos)

        fun getOverlap(row: Long): LongRange? = (dist - abs(pos.y - row)).let { space ->
            if (space >= 0) (pos.x - space)..(pos.x + space) else null
        }
    }

    private val lineFormat = Regex("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)")
    private val sensors = input.map { line ->
        val (sensorX, sensorY, beaconX, beaconY) = lineFormat.matchEntire(line)?.destructured
            ?: error("Invalid line: $line")
        Sensor(Vec2(sensorX.toLong(), sensorY.toLong()), Vec2(beaconX.toLong(), beaconY.toLong()))
    }.toMutableList()

    private fun getOverlappedRanged(checkRow: Long) = sensors.mapNotNull { it.getOverlap(checkRow) }

    private val checkRow = if (sensors.size < 20) 10L else 2_000_000L

    override fun calcPart1() = getOverlappedRanged(checkRow).let { ranges ->
        ranges.maxOf { it.last } - ranges.minOf { it.first }
    }

    private val validRange = if (sensors.size < 20) 0..20 else 0..4_000_000

    override fun calcPart2(): Long {
        val downLines = sensors.flatMap { s -> (s.pos.x - s.pos.y).let { listOf(it + s.dist + 1L, it - s.dist - 1L) } }
        val upLines = sensors.flatMap { s -> (s.pos.x + s.pos.y).let { listOf(it + s.dist + 1L, it - s.dist - 1L) } }

        for (downLine in downLines.sorted()) {
            for (upLine in upLines.sorted()) {
                if ((downLine + upLine) % 2 == 0L) { // If we can't average them, there is no crossing
                    val x = (downLine + upLine) / 2 // The average of the 2 lines is where they cross
                    if (x < 0) continue
                    if (x > 4_000_000L) break
                    val y = x - downLine
                    if (y in validRange && sensors.all { it.pos.manhattanDistance(x, y) > it.dist }) {
                        return x * 4_000_000L + y
                    }
                }
            }
        }

        return 0
    }
}