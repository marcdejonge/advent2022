package marcdejonge.advent2022

import marcdejonge.advent2022.util.aStar

fun main() = DaySolver.printSolutions(::Day21)

class Day21 : DaySolver(21) {
    sealed interface Monkey {
        val name: String
        fun execute(): Long
    }

    data class ConstantMonkey(override val name: String, val number: Long) : Monkey {
        override fun execute() = number
    }

    abstract inner class DepMonkey(
        override val name: String,
        private val leftName: String,
        private val rightName: String
    ) : Monkey {
        val left: Monkey by lazy { monkeys[leftName]!! }
        val right: Monkey by lazy { monkeys[rightName]!! }
        abstract fun expectedLeft(answer: Long): Long
        abstract fun expectedRight(answer: Long): Long
    }

    inner class AddMonkey(name: String, left: String, right: String) : DepMonkey(name, left, right) {
        override fun execute() = left.execute() + right.execute()
        override fun expectedLeft(answer: Long) = answer - right.execute()
        override fun expectedRight(answer: Long) = answer - left.execute()
    }

    inner class MinusMonkey(name: String, left: String, right: String) : DepMonkey(name, left, right) {
        override fun execute() = left.execute() - right.execute()
        override fun expectedLeft(answer: Long) = answer + right.execute()
        override fun expectedRight(answer: Long) = left.execute() - answer
    }

    inner class TimesMonkey(name: String, left: String, right: String) : DepMonkey(name, left, right) {
        override fun execute() = left.execute() * right.execute()
        override fun expectedLeft(answer: Long) = answer / right.execute()
        override fun expectedRight(answer: Long) = answer / left.execute()
    }

    inner class DivMonkey(name: String, left: String, right: String) : DepMonkey(name, left, right) {
        override fun execute() = left.execute() / right.execute()
        override fun expectedLeft(answer: Long) = answer * right.execute()
        override fun expectedRight(answer: Long) = left.execute() / answer
    }

    private val lineFormat = Regex("(\\w+): (([0-9]+)|(\\w+) ([+\\-*/]) (\\w+))")
    private val monkeys: Map<String, Monkey> = input.map { line ->
        val (name, _, constant, left, op, right) =
            lineFormat.matchEntire(line)?.destructured ?: error("Invalid line: $line")
        name to when (op) {
            "" -> ConstantMonkey(name, constant.toLong())
            "+" -> AddMonkey(name, left, right)
            "-" -> MinusMonkey(name, left, right)
            "*" -> TimesMonkey(name, left, right)
            "/" -> DivMonkey(name, left, right)
            else -> error("Unreachable")
        }
    }.toMap()
    private val rootMonkey = monkeys["root"] as DepMonkey

    override fun calcPart1() = rootMonkey.execute()

    override fun calcPart2(): Long {
        val path = aStar<Monkey>(rootMonkey, { it.name == "humn" }) {
            if (it is DepMonkey) sequenceOf(it.left to 1, it.right to 1) else emptySequence()
        }.asSequence()
        val toMatch = if (path[1] == rootMonkey.left) rootMonkey.right.execute() else rootMonkey.left.execute()
        return path.asSequence().drop(1).zipWithNext().fold(toMatch) { answer, (curr, next) ->
            if (curr !is DepMonkey) error("Each intermediate node should be dependant on something else")
            if (curr.left == next) curr.expectedLeft(answer) else curr.expectedRight(answer)
        }
    }
}
