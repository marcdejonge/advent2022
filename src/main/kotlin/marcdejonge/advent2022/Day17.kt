package marcdejonge.advent2022

import marcdejonge.advent2022.util.Vec2

fun main() = DaySolver.printSolutions(::Day17)

class Day17 : DaySolver(17) {
    enum class Move(val change: Vec2) { L(Vec2(-1, 0)), R(Vec2(1, 0)), D(Vec2(0, -1)) }

    sealed class Shape(vararg val points: Vec2)
    object HorizontalLine : Shape(Vec2(0, 0), Vec2(1, 0), Vec2(2, 0), Vec2(3, 0))
    object Plus : Shape(Vec2(1, 0), Vec2(0, 1), Vec2(1, 1), Vec2(2, 1), Vec2(1, 2))
    object Corner : Shape(Vec2(0, 0), Vec2(1, 0), Vec2(2, 0), Vec2(2, 1), Vec2(2, 2))
    object VerticalLine : Shape(Vec2(0, 0), Vec2(0, 1), Vec2(0, 2), Vec2(0, 3))
    object Block : Shape(Vec2(0, 0), Vec2(1, 0), Vec2(0, 1), Vec2(1, 1))

    private val shapes = listOf(HorizontalLine, Plus, Corner, VerticalLine, Block)
    private val moves = input.single().map { if (it == '<') Move.L else Move.R }

    class Field(private val moves: List<Move>) {
        private val lineRange = 0..6
        private val rocks: MutableList<CharArray> = mutableListOf()
        var moveIx: Int = 0
            private set
        val height get() = rocks.size
        val top get() = String(rocks.last())

        private fun testShapeFit(shape: Shape, point: Vec2) = shape.points.all {
            (point + it).let { (x, y) -> x in lineRange && y >= 0 && (y >= rocks.size || rocks[y][x] == '.') }
        }

        fun dropRock(shape: Shape) {
            var position = Vec2(2, height + 3)

            while (true) {
                val newPos = position + moves[moveIx].change // First shift according to the jet stream
                moveIx = (moveIx + 1) % moves.size
                if (testShapeFit(shape, newPos)) position = newPos

                val dropPos = position + Move.D.change
                if (!testShapeFit(shape, dropPos)) {
                    shape.points.forEach {
                        val (x, y) = position + it
                        if (rocks.size <= y) rocks.add(CharArray(7) { '.' })
                        rocks[y][x] = '#'
                    }
                    break
                }

                position = dropPos
            }
        }
    }

    override fun calcPart1() = Field(moves).apply {
        repeat(2022) { dropRock(shapes[it % shapes.size]) }
    }.height

    data class State(val moveIx: Int, val lastShape: Shape, val lastLine: String)

    override fun calcPart2() = with(Field(moves)) {
        val states = HashMap<State, Pair<Int, Int>>()
        for (step in 0..Int.MAX_VALUE) {
            val nextRock = shapes[step % shapes.size]
            dropRock(nextRock)
            val newState = State(moveIx, nextRock, top)

            if (states.contains(newState)) {
                val (lastHeight, lastStep) = states[newState]!!
                val cycleSteps = step - lastStep
                val cycleCount = (1_000_000_000_000L - lastStep) / cycleSteps
                if (cycleCount * cycleSteps + lastStep == 1_000_000_000_000L) {
                    return@with (cycleCount - 1) * (height - lastHeight) + (height - 1)
                }
            }
            states[newState] = height to step
        }
        return 0L
    }
}
