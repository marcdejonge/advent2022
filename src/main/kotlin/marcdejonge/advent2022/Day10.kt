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

    private fun execute(actions: Iterable<Action>) = sequence {
        var x = 1
        actions.forEach { action ->
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

    private fun Sequence<Int>.mapToScreen() = StringBuilder(256).also { screen ->
        forEachIndexed { time, spriteIx ->
            if (time % 40 == 0) screen.append('\n')
            screen.append(if (abs(spriteIx - (time % 40)) <= 1) '#' else '.')
        }
    }.toString()

    override fun calcPart1() = execute(actions)
        .mapIndexed { time, x -> if (time % 40 == 19) (time + 1) * x else 0 }.sum()

    override fun calcPart2() = execute(actions).mapToScreen()
}
