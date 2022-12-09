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