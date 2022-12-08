package marcdejonge.advent2022

import marcdejonge.advent2022.util.chunkBy

fun main() = DaySolver.printSolutions { Day5() }

class Day5 : DaySolver(5) {
    // Create the number of stacks based on the number line
    private fun parseStacks(lines: List<String>) = List(lines.last().trim().split(Regex(" +")).count()) { ix ->
        val charIx = ix * 4 + 1 // Each character for this stack is on this index for each line
        lines.mapNotNull { it.getOrNull(charIx) }.filter { it in 'A'..'Z' }
            .toList() // Only use the valid characters
    }

    data class Command(
        val count: Int,
        val fromStackIx: Int,
        val toStackIx: Int,
    )

    private val commandRegex = Regex("move (\\d+) from (\\d+) to (\\d+)")
    private fun parseCommand(line: String): Command {
        val (count, fromIx, toIx) = commandRegex.matchEntire(line)!!.destructured
        return Command(count.toInt(), fromIx.toInt() - 1, toIx.toInt() - 1)
    }

    private val stacks: List<List<Char>>
    private val commands: List<Command>

    init {
        val (stackInput, commandInput) = input.chunkBy { it == "" }.toList()
        stacks = parseStacks(stackInput)
        commands = commandInput.map(::parseCommand).reversed()
    }

    data class Position(val stackIx: Int, val charIx: Int) {
        fun traceBackCommand(command: Command, reversed: Boolean) = when {
            command.fromStackIx == stackIx -> copy(charIx = charIx + command.count)
            command.toStackIx != stackIx -> this
            charIx >= command.count -> copy(charIx = charIx - command.count)
            else -> Position(command.fromStackIx, if (reversed) command.count - (charIx + 1) else charIx)
        }
    }

    private fun calculate(reversed: Boolean) = List(stacks.size) { stackIx ->
        commands.fold(Position(stackIx, 0)) { pos, command -> pos.traceBackCommand(command, reversed) }
    }.map { pos -> stacks[pos.stackIx].let { stack -> stack[pos.charIx] } }.joinToString("")

    override fun calcPart1() = calculate(true)
    override fun calcPart2() = calculate(false)
}
