package marcdejonge.advent2022

fun main() = Day1.printSolution()

object Day1 : DaySolver(1) {
    private val sums by lazy {
        rawInput.splitToSequence("\n\n")
            .map { elf ->
                elf.splitToSequence("\n").sumOf { it.toInt() }
            }.sortedDescending().toList()
    }

    override fun calcPart1() = sums.first()

    override fun calcPart2() = sums.take(3).sum()
}
