package marcdejonge.advent2022.util

import kotlin.math.abs

data class Vec2(
    val x: Long = 0,
    val y: Long = 0,
) {
    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
    operator fun div(other: Vec2) = Vec2(x / other.x, y / other.y)

    fun manhattanDistance(other: Vec2) = manhattanDistance(other.x, other.y)
    fun manhattanDistance(x: Long, y: Long) = abs(this.x - x) + abs(this.y - y)

    override fun toString() = "($x, $y)"

    companion object {
        fun valueOf(coordinates: String): Vec2 {
            val (x, y) = coordinates.split(",")
            return Vec2(x.toLong(), y.toLong())
        }
    }
}
