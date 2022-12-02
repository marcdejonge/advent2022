package marcdejonge.advent2022

fun main() = Day2.printSolution()

object Day2 : DaySolver(2) {
    private val games = input.map {
        parseChoice(it[0]) to it[2]
    }

    override fun calcPart1() =
        games.sumOf { (opponent, me) -> calcScore(parseChoice(me), opponent) }

    override fun calcPart2() =
        games.sumOf { (opponent, me) -> calcScore(strategicChoice(me, opponent), opponent) }

    private fun parseChoice(char: Char) = when (char) {
        'A', 'X' -> Choice.Rock
        'B', 'Y' -> Choice.Paper
        'C', 'Z' -> Choice.Scissors
        else -> error("Unknown character")
    }

    private fun strategicChoice(me: Char, opponent: Choice) = when (me) {
        'X' -> opponent.losingChoice
        'Y' -> opponent
        'Z' -> opponent.winningChoice
        else -> error("Unknown character")
    }

    private fun calcScore(me: Choice, opponent: Choice) = me.score +
            when (me) {
                opponent -> 3
                opponent.winningChoice -> 6
                else -> 0
            }
}

enum class Choice(val score: Int) {
    Rock(1),
    Paper(2),
    Scissors(3);

    val losingChoice
        get() = when (this) {
            Rock -> Scissors
            Paper -> Rock
            Scissors -> Paper
        }

    val winningChoice
        get() = when (this) {
            Rock -> Paper
            Paper -> Scissors
            Scissors -> Rock
        }
}
