package marcdejonge.advent2022.util

fun String.shouldStartWith(prefix: String) =
    also { if (!it.startsWith(prefix)) error("Expected string to start with \"$prefix\", but it was \"$it\"") }

fun String.trimExpectedStart(prefix: String) = shouldStartWith(prefix).drop(prefix.length)
