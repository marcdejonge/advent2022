package marcdejonge.advent2022.util

import java.awt.image.BufferedImage

class IntGrid(
    val xRange: IntRange,
    val yRange: IntRange,
) {
    private val grid: Array<IntArray> =
        Array(xRange.last - xRange.first + 1) { IntArray(yRange.last - yRange.first + 1) }

    operator fun get(v: Vec2) = get(v.x, v.y)

    operator fun get(x: Int, y: Int) =
        if (x !in xRange || y !in yRange) null else grid[x - xRange.first][y - yRange.first()]

    operator fun set(v: Vec2, value: Int) = set(v.x, v.y, value)

    operator fun set(x: Int, y: Int, value: Int) {
        if (x !in xRange) error("$x is outside of range $xRange")
        if (y !in yRange) error("$y is outside of range $yRange")
        grid[x - xRange.first][y - yRange.first()] = value
    }

    fun render(transform: (Vec2, Int) -> Int): BufferedImage {
        val image = BufferedImage(grid.size, grid[0].size, BufferedImage.TYPE_INT_RGB)
        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                image.setRGB(x, y, transform(Vec2(x, y), grid[x][y]))
            }
        }
        return image
    }

    fun deepCopy() = IntGrid(xRange, yRange).also { copy ->
        grid.forEachIndexed { ix, line -> copy.grid[ix] = line.copyOf() }
    }

    fun toString(transform: (Int) -> Char): String {
        val sb = StringBuilder()
        for (y in yRange) {
            for (x in xRange) {
                sb.append(transform(get(x, y)!!))
            }
            sb.append("\n")
        }
        return sb.toString()
    }
}
