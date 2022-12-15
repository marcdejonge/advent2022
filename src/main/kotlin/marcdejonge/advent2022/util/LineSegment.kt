package marcdejonge.advent2022.util

import java.lang.Integer.max
import java.lang.Integer.min

data class LineSegment(val start: Vec2, val end: Vec2) {
    val xRange = min(start.x, end.x)..max(start.x, end.x)
    val yRange = min(start.y, end.y)..max(start.y, end.y)

    companion object {
        fun Sequence<Vec2>.lineSegments() = zipWithNext { start, end -> LineSegment(start, end) }
    }
}