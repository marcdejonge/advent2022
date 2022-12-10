package marcdejonge.advent2022

import kotlin.math.abs

fun main() = DaySolver.printSolutions(::Day10)

class Day10 : DaySolver(10) {
    enum class ActionType { ADDX, NOOP }
    data class Action(val type: ActionType, val amount: Int)

    private val lineRegex = Regex("(\\w+)( (-?\\d+))?")
    private val actions = input.map { line ->
        val (type, _, amount) = lineRegex.matchEntire(line)?.destructured ?: error("Invalid line")
        Action(ActionType.valueOf(type.uppercase()), amount.toIntOrNull() ?: 0)
    }.toList()

    private fun List<Action>.execute() = sequence {
        var x = 1

        forEach { action ->
            when (action.type) {
                ActionType.ADDX -> {
                    yield(x)
                    yield(x)
                    x += action.amount
                }

                ActionType.NOOP -> yield(x)
            }
        }
    }

    private fun Sequence<Int>.mapToScreen(): String {
        val screen = StringBuilder(256)
        forEachIndexed { time, spriteIx ->
            if (time % 40 == 0) screen.append('\n')
            if (abs(spriteIx - (time % 40)) <= 1) {
                screen.append('#')
            } else {
                screen.append('.')
            }
        }
        return screen.toString()
    }

    override fun calcPart1() =
        actions.execute().mapIndexed { time, x -> if (time % 40 == 19) (time + 1) * x else 0 }.sum()

    override fun calcPart2() = actions.execute().mapToScreen()
}
