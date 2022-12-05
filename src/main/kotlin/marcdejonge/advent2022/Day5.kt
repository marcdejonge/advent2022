package marcdejonge.advent2022

import marcdejonge.advent2022.util.toInt

fun main() = Day5.printSolution()

object Day5 : DaySolver(5) {
    private fun parseStacks(lines: List<String>) = with(lines.reversed()) {// Reverse the order to build stacks
        List(first().split(" ").last().toInt()) { ix -> // Create the number of stacks based on the number line
            val charIx = ix * 4 + 1 // Each character for this stack is on this index for each line
            ArrayList(mapNotNull { it.getOrNull(charIx) }.filter { it in 'A'..'Z' }) // Only use the valid characters
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
    private val commands = input.takeLastWhile { it != "" }.map(::parseCommand)

    init {
        println(stacks)
        println(commands)
    }

    private fun calc(execute: Command.(List<ArrayList<Char>>) -> Unit) =
        stacks.map { ArrayList(it) } // Make sure to make a copy before allowing changes
            .also { stacks -> commands.forEach { command -> execute(command, stacks) } } // Apply commands
            .map { it.last() }.joinToString(separator = "") // Map to characters back to a String

    override fun calcPart1() = calc { stacks ->
        repeat(count) { _ ->
            stacks[toStackIx].add(stacks[fromStackIx].removeLast())
        }
    }

    override fun calcPart2() = calc { stacks ->
        val insertIx = stacks[toStackIx].size
        repeat(count) { _ ->
            stacks[toStackIx].add(insertIx, stacks[fromStackIx].removeLast())
        }
    }
}
