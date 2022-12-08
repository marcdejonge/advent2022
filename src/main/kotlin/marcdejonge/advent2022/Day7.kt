package marcdejonge.advent2022

fun main() = DaySolver.printSolutions(::Day7)

class Day7 : DaySolver(7) {
    data class Directory(val parent: Directory?, var totalSize: Int = 0) {
        fun goUp() = parent?.also { it.totalSize += this.totalSize }
        fun goToRoot() = generateSequence(this) { it.goUp() }.last()
    }

    private val root = Directory(null)
    private val directorySizes: Sequence<Int>

    init {
        val lineParser = Regex("(\\S+) (\\S+)( (\\S+))?")
        val allDirectories = mutableListOf(root)

        input.fold(root) { current, line ->
            val (type, name, _, dirName) = lineParser.matchEntire(line)!!.destructured
            if (type == "$" && name == "cd") {
                when (dirName) {
                    "/" -> current.goToRoot()
                    ".." -> current.goUp() ?: error("There is no up from the root")
                    else -> Directory(current).also { allDirectories.add(it) }
                }
            } else {
                current.apply {
                    val size = type.toIntOrNull()
                    if (size != null) totalSize += size // We only care about files with actual sizes, rest is ignored
                }
            }
        }.goToRoot()

        directorySizes = allDirectories.asSequence().map { it.totalSize }
    }

    override fun calcPart1() = directorySizes.filter { it <= 100000 }.sum()
    override fun calcPart2() = directorySizes.filter { it >= root.totalSize - 40_000_000 }.min()
}