package marcdejonge.advent2022

import marcdejonge.advent2022.util.toInt

fun main() = Day4.printSolution()

object Day4 : DaySolver(4) {
    private val regex = Regex("(\\d+)-(\\d+),(\\d+)-(\\d+)")

    private val assignmentPair: List<Pair<IntRange, IntRange>> = input.map { line ->
        regex.matchEntire(line)?.groups.let { group ->
            checkNotNull(group)
            group[1].toInt()..group[2].toInt() to group[3].toInt()..group[4].toInt()
        }
    }


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
