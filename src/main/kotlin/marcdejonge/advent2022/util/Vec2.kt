package marcdejonge.advent2022.util

data class Vec2(
    val x: Int = 0,
    val y: Int = 0,
) {
    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
    operator fun div(other: Vec2) = Vec2(x / other.x, y / other.y)

    companion object {
        fun valueOf(coordinates: String): Vec2 {
            val (x, y) = coordinates.split(",")
            return Vec2(x.toInt(), y.toInt())
        }
    }
}
