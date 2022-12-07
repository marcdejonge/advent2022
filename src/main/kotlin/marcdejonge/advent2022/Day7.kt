package marcdejonge.advent2022

fun main() = DaySolver.printSolutions(::Day7)

class Day7 : DaySolver(7) {
    interface FSItem {
        val name: String
        val parent: Directory?
        val size: Int

        fun addToParent() = parent?.also { it.items[name] = this } ?: error("Missing parent")
    }

    data class Directory(
        override val name: String,
        override val parent: Directory? = null,
        val items: MutableMap<String, FSItem> = LinkedHashMap()
    ) : FSItem {
        override val size: Int get() = items.values.sumOf { it.size }
        fun listDirs(): Sequence<Directory> = sequenceOf(this) + items.values.asSequence()
            .filterIsInstance<Directory>().flatMap { it.listDirs() }
    }

    data class File(override val name: String, override val parent: Directory, override val size: Int) : FSItem

    private val topDirectory = Directory(name = "/")

    init {
        input.fold(topDirectory) { current, line ->
            val (type, name) = line.split(" ", limit = 2)
            when (type) {
                "$" -> if (name == "cd") {
                    when (val dirName = line.drop(5)) {
                        "/" -> topDirectory
                        ".." -> current.parent ?: error("Already at the top directory")
                        else -> current.items[dirName] as? Directory ?: error("Unknown directory $dirName")
                    }
                } else current // Ignore any other commands
                "dir" -> Directory(line.drop(4), current).addToParent()
                else -> File(name, current, type.toInt()).addToParent() // The type is actually the size
            }
        }
    }

    override fun calcPart1() =
        topDirectory.listDirs().filter { it.size <= 100000 }.sumOf { it.size }

    override fun calcPart2() =
        topDirectory.listDirs().filter { it.size >= topDirectory.size - 40_000_000 }.minOf { it.size }
}