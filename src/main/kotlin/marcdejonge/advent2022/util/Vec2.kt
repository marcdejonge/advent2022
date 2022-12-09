package marcdejonge.advent2022.util

import kotlin.math.abs

data class Vec2(
    val x: Int = 0,
    val y: Int = 0,
) {
    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
    operator fun div(other: Vec2) = Vec2(x / other.x, y / other.y)
    fun abs() = Vec2(abs(x), abs(y))
}
