package marcdejonge.advent2022

import marcdejonge.advent2022.util.chunkBy

fun main() = DaySolver.printSolutions { Day1() }

class Day1 : DaySolver(1) {
    private val sums = input.chunkBy { it == "" }.map { elf -> elf.sumOf { it.toInt() } }
        .toSortedSet(Comparator.reverseOrder())

    override fun calcPart1(): Int = sums.first()

    override fun calcPart2() = sums.take(3).sum()
}
