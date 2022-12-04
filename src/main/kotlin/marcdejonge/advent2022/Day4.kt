package marcdejonge.advent2022

fun main() = Day4.printSolution()

object Day4 : DaySolver(4) {
    private val regex = Regex("(\\d+)-(\\d+),(\\d+)-(\\d+)")
    private fun MatchGroup?.toInt() = checkNotNull(this).value.toInt()

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
        firstRange.overlaps(secondRange)
    }

    private fun IntRange.contains(range: IntRange) = first <= range.first && last >= range.last
    private fun IntRange.overlaps(range: IntRange) =
        contains(range.first) || contains(range.last) || range.contains(first) || range.contains(last)
}
