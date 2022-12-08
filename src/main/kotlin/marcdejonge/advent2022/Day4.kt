package marcdejonge.advent2022

fun main() = DaySolver.printSolutions { Day4() }

class Day4 : DaySolver(4) {
    private val regex = Regex("(\\d+)-(\\d+),(\\d+)-(\\d+)")

    private val assignmentPair = input.map { line ->
        val (a, b, c, d) = regex.matchEntire(line)!!.destructured
        a.toInt()..b.toInt() to c.toInt()..d.toInt()
    }.toList()


    override fun calcPart1() = assignmentPair.count { (firstRange, secondRange) ->
        firstRange.contains(secondRange) || secondRange.contains(firstRange)
    }

    override fun calcPart2() = assignmentPair.count { (firstRange, secondRange) ->
        firstRange.overlapsWith(secondRange)
    }

    private fun IntRange.contains(other: IntRange) =
        this.first <= other.first && this.last >= other.last

    private fun IntRange.overlapsWith(other: IntRange) =
        this.contains(other.first) || this.contains(other.last)
                || other.contains(this.first) || other.contains(this.last)
}
