package marcdejonge.advent2022.util

import java.awt.image.BufferedImage

class IntGrid(
    val xRange: IntRange,
    val yRange: IntRange,
) {
    val width = (xRange.last - xRange.first) + 1
    val height = (yRange.last - yRange.first) + 1
    private val grid = IntArray(width * height)

    operator fun get(v: Vec2) = get(v.x, v.y)

    operator fun get(x: Long, y: Long) = get(x.toInt(), y.toInt())

    operator fun get(x: Int, y: Int) = getOrNull(x, y) ?: throw IndexOutOfBoundsException()

    fun getOrNull(v: Vec2) = getOrNull(v.x, v.y)

    fun getOrNull(x: Long, y: Long) = getOrNull(x.toInt(), y.toInt())

    fun getOrNull(x: Int, y: Int) =
        if (x !in xRange || y !in yRange) null
        else grid[(x - xRange.first) + (y - yRange.first) * width]

    operator fun set(v: Vec2, value: Int) = set(v.x, v.y, value)

    operator fun set(x: Long, y: Long, value: Int) = set(x.toInt(), y.toInt(), value)

    operator fun set(x: Int, y: Int, value: Int) {
        if (x !in xRange) error("$x is outside of range $xRange")
        if (y !in yRange) error("$y is outside of range $yRange")
        grid[(x - xRange.first) + (y - yRange.first) * width] = value
    }

    fun render(transform: (Vec2, Int) -> Int): BufferedImage {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        for (x in 0 until width) {
            for (y in 0 until height) {
                image.setRGB(x, y, transform(Vec2(x.toLong(), y.toLong()), grid[x + y * width]))
            }
        }
        return image
    }

    fun deepCopy() = IntGrid(xRange, yRange).also { copy ->
        System.arraycopy(grid, 0, copy.grid, 0, grid.size)
    }

    fun toString(transform: (Int) -> Char): String {
        val sb = StringBuilder()
        for (y in 0 until height) {
            for (x in 0 until width) {
                sb.append(transform(grid[x + y * width]))
            }
            sb.append("\n")
        }
        return sb.toString()
    }
}
