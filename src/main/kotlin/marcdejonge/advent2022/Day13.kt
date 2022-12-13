package marcdejonge.advent2022

import marcdejonge.advent2022.util.Parser

fun main() = DaySolver.printSolutions(::Day13)

class Day13 : DaySolver(13) {
    sealed interface ListOrNumber : Comparable<ListOrNumber>

    class NumberList(val items: List<ListOrNumber>) : ListOrNumber {
        constructor(single: ListOrNumber) : this(listOf(single))

        override fun compareTo(other: ListOrNumber): Int {
            when (other) {
                is Number -> return compareTo(NumberList(other))
                is NumberList -> {
                    val leftIt = this.items.iterator()
                    val rightIt = other.items.iterator()
                    while (leftIt.hasNext() && rightIt.hasNext()) {
                        val compare = leftIt.next().compareTo(rightIt.next())
                        if (compare != 0) return compare
                    }
                    return when {
                        leftIt.hasNext() -> 1
                        rightIt.hasNext() -> -1
                        else -> 0
                    }
                }
            }
        }
    }

    class Number(val number: Int) : ListOrNumber {
        override fun compareTo(other: ListOrNumber): Int = when (other) {
            is Number -> this.number.compareTo(other.number)
            is NumberList -> NumberList(this).compareTo(other)
        }
    }

    private fun Parser.parseList(): NumberList {
        expect('[')
        val items = readList(',') { if (lookAhead() == '[') parseList() else parseNumber() }.toList()
        expect(']')
        return NumberList(items)
    }

    private fun Parser.parseNumber() = Number(readWhile { it in '0'..'9' }.fold(0) { a, c -> a * 10 + (c - '0') })

    private val packets = input.filter { it != "" }.map { Parser(it).parseList() }.toList()

    override fun calcPart1() = packets.windowed(2, 2).mapIndexed { ix, (first, second) ->
        if (first < second) ix + 1 else 0
    }.sum()

    private val startPacket = NumberList(NumberList(Number(2)))
    private val endPacket = NumberList(NumberList(Number(6)))

    override fun calcPart2() = (packets.count { it < startPacket } + 1) * (packets.count { it < endPacket } + 2)
}
