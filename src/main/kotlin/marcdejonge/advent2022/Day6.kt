package marcdejonge.advent2022

fun main() = DaySolver.printSolutions(::Day6)

class Day6 : DaySolver(6) {
    private val chars = inputFullText.filter { it in 'a'..'z' }.map { it - 'a' }

    private fun findStart(checkSize: Int) = checkSize + chars.windowed(checkSize).indexOfFirst {
        it.fold(0) { acc, next ->
            acc or (1 shl next)
        }.countOneBits() == checkSize
    }

    override fun calcPart1() = findStart(4)
    override fun calcPart2() = findStart(14)
}
