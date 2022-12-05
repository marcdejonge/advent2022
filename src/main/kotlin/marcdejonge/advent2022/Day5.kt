package marcdejonge.advent2022

import marcdejonge.advent2022.util.toInt

fun main() = DaySolver.printSolutions { Day5() }

class Day5 : DaySolver(5) {
    private fun parseStacks(lines: List<String>) = with(lines.reversed()) {// Reverse the order to build stacks
        List(first().trim().split(" ").last().toInt()) { ix -> // Create the number of stacks based on the number line
            val charIx = ix * 4 + 1 // Each character for this stack is on this index for each line
            mapNotNull { it.getOrNull(charIx) }.filter { it in 'A'..'Z' } // Only use the valid characters
        }
    }

    data class Command(
        val count: Int,
        val fromStackIx: Int,
        val toStackIx: Int,
    )

    private val commandRegex = Regex("move (\\d+) from (\\d+) to (\\d+)")
    private fun parseCommand(line: String) = with(
        checkNotNull(commandRegex.matchEntire(line)).groups
    ) {
        Command(get(1).toInt(), get(2).toInt() - 1, get(3).toInt() - 1) // Indices should be 0-based
    }

    private val stacks = parseStacks(input.takeWhile { it != "" })
    private val commands = input.reversed().takeWhile { it != "" }.map(::parseCommand)

    private fun calculate(reversed: Boolean) = List(stacks.size) { stackIx ->
        commands.fold(Position(stackIx, 0)) { pos, command -> pos.traceBackCommand(command, reversed) }
    }.map { pos -> stacks[pos.stackIx].let { stack -> stack[stack.lastIndex - pos.charIx] } }.joinToString("")

    override fun calcPart1() = calculate(true)
    override fun calcPart2() = calculate(false)

    data class Position(val stackIx: Int, val charIx: Int) {
        fun traceBackCommand(command: Command, reversed: Boolean) = when {
            command.fromStackIx == stackIx -> copy(charIx = charIx + command.count)
            command.toStackIx != stackIx -> this
            charIx >= command.count -> copy(charIx = charIx - command.count)
            else -> Position(command.fromStackIx, if (reversed) command.count - (charIx + 1) else charIx)
        }
    }
}
