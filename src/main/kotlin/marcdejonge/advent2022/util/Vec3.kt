package marcdejonge.advent2022.util

import kotlin.math.abs

data class Vec3(
    val x: Int = 0,
    val y: Int = 0,
    val z: Int = 0,
) {
    operator fun plus(other: Vec3) = Vec3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vec3) = Vec3(x - other.x, y - other.y, z - other.z)

    fun manhattanDistance(other: Vec3) = manhattanDistance(other.x, other.y, other.z)
    fun manhattanDistance(x: Int, y: Int, z: Int) = abs(this.x - x) + abs(this.y - y) + abs(this.z - z)

    fun between(min: Vec3, max: Vec3): Boolean = (x in min.x..max.x) && (y in min.y..max.y) && (z in min.z..max.z)

    override fun toString() = "($x, $y, $z)"

    companion object {
        fun valueOf(coordinates: String): Vec3 {
            val (x, y, z) = coordinates.split(",")
            return Vec3(x.toInt(), y.toInt(), z.toInt())
        }
    }
}
