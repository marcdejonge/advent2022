package marcdejonge.advent2022

fun main() = DaySolver.printSolutions { Day3() }

class Day3 : DaySolver(3) {
    private val rucksacks = input.map(::Rucksack).asSequence()

    override fun calcPart1() = rucksacks.sumOf {
        scoreOf(it.firstCompartment.intersect(it.secondCompartment).single())
    }

    override fun calcPart2() = rucksacks.map(Rucksack::allChars).chunked(3).sumOf { (first, second, third) ->
        scoreOf(first.intersect(second).intersect(third).single())
    }

    private fun scoreOf(char: Char): Int = when (char) {
        in 'a'..'z' -> char - 'a' + 1
        in 'A'..'Z' -> char - 'A' + 27
        else -> error("Unknown character")
    }
}

data class Rucksack(
    val firstCompartment: Set<Char>,
    val secondCompartment: Set<Char>,
) {
    constructor(line: String) : this(
        line.toCharArray(0, line.length / 2).toSet(),
        line.toCharArray(line.length / 2, line.length).toSet()
    )

    val allChars: Set<Char> = firstCompartment + secondCompartment
}