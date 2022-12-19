package marcdejonge.advent2022

import marcdejonge.advent2022.util.GifSequenceWriter
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.stream.FileImageOutputStream

fun main() {
    val day = System.getenv("DAY")?.toIntOrNull() ?: return

    try {
        Class.forName("marcdejonge.advent2022.Day${day}Kt").getMethod("main").invoke(null)
    } catch (ex: ClassNotFoundException) {
        println("Day not found")
    }
}

abstract class DaySolver(private val day: Int, private val supportsAnimation: Boolean = false) {
    private val url by lazy {
        val filePostfix = System.getenv("FILE_POSTFIX") ?: ""
        val fileName = String.format("day%02d%s.txt", day, filePostfix)
        this::class.java.classLoader.getResource(fileName) ?: error("Could not load file $fileName")
    }

    val inputFullText: String
        get() = url.openStream().bufferedReader().readText()

    val input: Sequence<String>
        get() = url.openStream().bufferedReader().lineSequence()

    val solutionPart1 by lazy { calcPart1() }
    val solutionPart2 by lazy { calcPart2() }

    open fun calcPart1(): Any? = null

    open fun calcPart2(): Any? = null

    open fun generateAnimation(writer: GifSequenceWriter) = Unit

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

            if (solver.supportsAnimation && System.getenv("RENDER") != null) {
                printTiming("Animation generated") {
                    println()
                    println("Generating animation...")
                    val file = File(String.format("day%02d.gif", solver.day))
                    GifSequenceWriter(FileImageOutputStream(file), BufferedImage.TYPE_INT_RGB, 40, false).use { w ->
                        solver.generateAnimation(w)
                    }
                }
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
