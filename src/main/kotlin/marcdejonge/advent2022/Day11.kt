package marcdejonge.advent2022

import marcdejonge.advent2022.util.chunkBy
import marcdejonge.advent2022.util.trimExpectedStart

fun main() = DaySolver.printSolutions(::Day11)

class Day11 : DaySolver(11) {
    data class Monkey(
        val items: MutableList<Long> = ArrayList(50),
        val operationType: Char,
        val amount: Long, // Special case, 0 means the old value
        val testDivisibleBy: Long,
        val trueMonkey: Int,
        val falseMonkey: Int,
        var inspectedItems: Long = 0,
    )

    private fun Monkey.deepCopy() = copy(items = ArrayList(items))

    private val monkeys = input.chunkBy { it == "" }.mapIndexed { index, monkeyLines ->
        if (monkeyLines.size != 6) error("Excepted 6 lines describing the monkey")
        if (monkeyLines[0] != "Monkey $index:") error("Excepted the monkeys to appear in order")
        val operation = monkeyLines[2].trimExpectedStart("  Operation: new = old ")
        Monkey(
            items = monkeyLines[1].trimExpectedStart("  Starting items: ").splitToSequence(", ")
                .mapTo(ArrayList(50)) { it.toLong() },
            operationType = operation[0],
            amount = if (operation == "* old") 0 else operation.substring(2).toLong(),
            testDivisibleBy = monkeyLines[3].trimExpectedStart("  Test: divisible by ").toLong(),
            trueMonkey = monkeyLines[4].trimExpectedStart("    If true: throw to monkey ").toInt(),
            falseMonkey = monkeyLines[5].trimExpectedStart("    If false: throw to monkey ").toInt(),
        )
    }.toList()

    // We use modulus calculations to keep the numbers manageable, the multiple of all dividers should always work
    private val mod = monkeys.fold(1L) { acc, monkey -> acc * monkey.testDivisibleBy }

    private fun List<Monkey>.executeRound(div: Long) = forEach { monkey -> executeTurn(monkey, div) }

    private fun List<Monkey>.executeTurn(monkey: Monkey, div: Long) = monkey.apply {
        items.forEach { old ->
            val amount = if (amount == 0L) old else amount
            val new = (if (operationType == '+') old + amount else old * amount) % mod / div
            get(if (new % testDivisibleBy == 0L) trueMonkey else falseMonkey).items.add(new)
            inspectedItems++
        }
        items.clear()
    }

    private fun calculate(rounds: Int, div: Long) = with(monkeys.map { it.deepCopy() }) {
        repeat(rounds) { executeRound(div) }
        map { it.inspectedItems }.sortedDescending().let { (first, second) -> first * second }
    }

    override fun calcPart1() = calculate(20, 3)
    override fun calcPart2() = calculate(10_000, 1)
}
