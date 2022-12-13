package marcdejonge.advent2022.util

class Parser(private val input: String) {
    private var ix: Int = 0

    fun lookAhead() = input[ix]
    fun next() = input[ix++]
    fun testNext(char: Char) = (input[ix] == char).also { if (it) ix++ }

    fun readWhile(predicate: (Char) -> Boolean): Sequence<Char> = generateSequence {
        if (predicate(lookAhead())) next() else null
    }

    fun <T> readList(splitChar: Char, nextValue: Parser.() -> T) = sequence {
        do {
            yield(nextValue())
        } while (testNext(splitChar))
    }

    fun expect(char: Char) {
        if (next() != char) error("Expected '$char' at index $ix, but saw ${lookAhead()}")
    }
}