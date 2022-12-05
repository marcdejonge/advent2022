package marcdejonge.advent2022

fun main() {
    val day = System.getenv("DAY")?.toIntOrNull() ?: return

    try {
        Class.forName("marcdejonge.advent2022.Day${day}Kt").getMethod("main").invoke(null)
    } catch (ex: ClassNotFoundException) {
        println("Day not found")
    }
}

abstract class DaySolver(private val day: Int, private val bigVersion: String = "") {
    private val url by lazy {
        val fileName = String.format("day%02d%s.txt", day, bigVersion)
        this::class.java.classLoader.getResource(fileName) ?: error("Could not load file $fileName")
    }

    val rawInput: String
        get() = url.openStream().bufferedReader().readText().trimEnd()

    val input: List<String>
        get() = rawInput.lines()

    val solutionPart1 by lazy { calcPart1() }
    val solutionPart2 by lazy { calcPart2() }

    open fun calcPart1(): Any? = null

    open fun calcPart2(): Any? = null

    override fun toString() = "Day $day"

    companion object {
        fun printSolutions(solverFactory: () -> DaySolver) {
            val solver = printTiming("Loaded") {
                solverFactory()
            }
            println("$solver:")
            printTiming("Calculated") {
                println("    Part 1: ${solver.solutionPart1 ?: "no solution found"}")
                println("    Part 2: ${solver.solutionPart2 ?: "no solution found"}")
            }
        }

        private fun <T> printTiming(what: String, function: () -> T): T {
            val start = System.nanoTime()
            val result = function()
            val totalTime = (System.nanoTime() - start) / 1e9
            println(String.format("%s in %1.3f seconds", what, totalTime))
            return result
        }
    }
}
