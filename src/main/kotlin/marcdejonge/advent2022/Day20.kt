package marcdejonge.advent2022

import marcdejonge.advent2022.util.every

fun main() = DaySolver.printSolutions(::Day20)

class Day20 : DaySolver(20) {
    data class NumberNode(val number: Long, val shift: Int) {
        lateinit var prev: NumberNode
        lateinit var next: NumberNode

        fun applyShift() {
            if (shift != 0) {
                removeFromChain()

                var afterNode = this
                if (shift > 0) {
                    repeat(shift) { afterNode = afterNode.next }
                } else {
                    repeat(-shift + 1) { afterNode = afterNode.prev }
                }

                addAfter(afterNode)
            }
        }

        fun removeFromChain() {
            prev.next = next
            next.prev = prev
        }

        fun addAfter(other: NumberNode) {
            prev = other
            next = other.next
            other.next = this
            next.prev = this
        }

        fun asSequence() = generateSequence(this, NumberNode::next)
    }

    private val startingNumbers = input.map { it.toLong() }.toList()

    private fun List<Long>.setupDoubleLinkedList() = map { number ->
        val size = startingNumbers.size - 1
        var shift = number % size
        if (shift > size / 2) shift -= size
        if (shift < -size / 2) shift += size
        NumberNode(number, shift.toInt())
    }.apply {
        forEachIndexed { ix, a ->
            val b = this[(ix + 1) % size]
            a.next = b
            b.prev = a
        }
    }

    private fun calculate(multiply: Int, repeat: Int): Long {
        val nodes = startingNumbers.map { it * multiply }.setupDoubleLinkedList()
        val zeroNode = nodes.single { it.number == 0L }
        repeat(repeat) { nodes.forEach(NumberNode::applyShift) }
        return zeroNode.asSequence().every(1000 % nodes.size).map(NumberNode::number).take(4).fold(0, Long::plus)
    }

    override fun calcPart1() = calculate(1, 1)
    override fun calcPart2() = calculate(811589153, 10)
}
