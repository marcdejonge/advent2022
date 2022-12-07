package marcdejonge.advent2022

fun main() = DaySolver.printSolutions(::Day7)

class Day7 : DaySolver(7) {
    interface FSItem {
        val name: String
        val parent: Directory?
        val size: Int

        fun addToParent() = parent?.items?.set(name, this)
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

    data class File(
        override val name: String,
        override val parent: Directory,
        override val size: Int
    ) : FSItem

    private val topDirectory = Directory(name = "/")

    init {
        input.fold(topDirectory) { current, line ->
            if (line.startsWith("$ cd ")) {
                when (val name = line.drop(5)) {
                    "/" -> topDirectory
                    ".." -> current.parent ?: topDirectory
                    else -> current.items[name] as? Directory ?: error("Unknown directory $name")
                }
            } else {
                if (line.startsWith("$")) {
                    // Ignore other commands
                } else if (line.startsWith("dir ")) {
                    Directory(line.drop(4), current).addToParent()
                } else {
                    val (size, name) = line.split(" ")
                    File(name, current, size.toInt()).addToParent()
                }
                current
            }
        }
    }

    override fun calcPart1() = topDirectory.listDirs().filter { it.size <= 100000 }.sumOf { it.size }
    override fun calcPart2(): Int {
        val spaceNeeded = 30_000_000 - (70_000_000 - topDirectory.size)
        return topDirectory.listDirs().filter { it.size >= spaceNeeded }.minOf { it.size }
    }
}