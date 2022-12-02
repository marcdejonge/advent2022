package marcdejonge.advent2022

import kotlin.system.measureNanoTime

abstract class DaySolver(val day: Int) {
    private val url by lazy {
        val fileName = String.format("day%02d.txt", day)
        this::class.java.classLoader.getResource(fileName) ?: error("Could not load file $fileName")
    }

    val rawInput: String
        get() = url.openStream().bufferedReader().readText()

    val input: List<String>
        get() = rawInput.lines()

    val solutionPart1 by lazy { calcPart1() }
    val solutionPart2 by lazy { calcPart2() }

    open fun calcPart1(): Any? = null

    open fun calcPart2(): Any? = null

    fun printSolution() {
        println("$this:")
        val seconds = measureNanoTime {
            println("    Part 1: ${solutionPart1 ?: "no solution found"}")
            println("    Part 2: ${solutionPart2 ?: "no solution found"}")
        } / 1e9
        println(String.format("Calculated in %1.3f seconds", seconds))
    }

    override fun toString() = "Day $day"
}