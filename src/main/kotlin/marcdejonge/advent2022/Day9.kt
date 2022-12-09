package marcdejonge.advent2022

import marcdejonge.advent2022.util.Vec2
import kotlin.math.abs

fun main() = DaySolver.printSolutions(::Day9)

class Day9 : DaySolver(9) {
    enum class Direction(val vector: Vec2) {
        R(Vec2(1, 0)),
        L(Vec2(-1, 0)),
        U(Vec2(0, 1)),
        D(Vec2(0, -1)),
    }

    private fun Vec2.stepOneTowards(diff: Vec2) = Vec2(
        if (diff.x == 0) this.x else this.x + (diff.x / abs(diff.x)),
        if (diff.y == 0) this.y else this.y + (diff.y / abs(diff.y))
    )

    private fun move(snake: List<Vec2>, direction: Direction): List<Vec2> =
        ArrayList<Vec2>(snake.size).apply {
            add(snake.first() + direction.vector)
            for (ix in 1..snake.lastIndex) {
                val diff = get(ix - 1) - snake[ix]
                add(if (abs(diff.x) > 1 || abs(diff.y) > 1) snake[ix].stepOneTowards(diff) else snake[ix])
            }
        }

    private val directions = input.flatMap {
        val dir = Direction.valueOf(it.substring(0, 1))
        List(it.substring(2).toInt()) { dir }
    }.toList()

    private fun calculate(length: Int) =
        directions.asSequence().runningFold(List(length) { Vec2() }, ::move).map { it.last() }.distinct().count()

    override fun calcPart1() = calculate(2)
    override fun calcPart2() = calculate(10)
}
