package marcdejonge.advent2022

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.stream.Stream
import kotlin.test.assertEquals

class DaySolverTests {
    class ExpectedResults : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext): Stream<Arguments> = Stream.of(
            Arguments.of(Day1(), 24000, 45000),
            Arguments.of(Day2(), 15, 12),
            Arguments.of(Day3(), 157, 70),
            Arguments.of(Day4(), 2, 4),
            Arguments.of(Day5(), "CMZ", "MCD"),
            Arguments.of(Day6(), 11, 26),
            Arguments.of(Day7(), 95437, 24933642),
            Arguments.of(Day8(), 21, 8L),
            Arguments.of(Day9(), 88, 36),
            Arguments.of(Day10(), 13140, """
                
                ##..##..##..##..##..##..##..##..##..##..
                ###...###...###...###...###...###...###.
                ####....####....####....####....####....
                #####.....#####.....#####.....#####.....
                ######......######......######......####
                #######.......#######.......#######.....
            """.trimIndent()),
            Arguments.of(Day11(), 10605L, 2713310158L),
            Arguments.of(Day12(), 31, 29),
            Arguments.of(Day13(), 13, 140),
            Arguments.of(Day14(), 24, 93),
            Arguments.of(Day15(), 26L, 56000011L),
            Arguments.of(Day16(), 1651, 1707),
            Arguments.of(Day17(), 3068, 1_514_285_714_288L),
            Arguments.of(Day18(), 64, 58),
            //Arguments.of(Day19(), 33, 3472), // This is disabled by default, because it takes very long
            Arguments.of(Day20(), 3L, 1623178306L),
        )
    }

    @ParameterizedTest
    @ArgumentsSource(ExpectedResults::class)
    fun `verify day solver`(solver: DaySolver, expectedPart1: Any?, expectedPart2: Any?) {
        assertEquals(
            expectedPart1,
            solver.solutionPart1,
            "Solving $solver.solutionPart1 did not return the expected result"
        )
        assertEquals(
            expectedPart2,
            solver.solutionPart2,
            "Solving $solver.solutionPart2 did not return the expected result"
        )
    }
}