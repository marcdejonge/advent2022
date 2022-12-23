package marcdejonge.advent2022

import marcdejonge.advent2022.util.depthFirstSearch

fun main() = DaySolver.printSolutions(::Day19)

class Day19 : DaySolver(19) {
    data class Blueprint(
        val index: Int,
        val oreRobotCost: Materials,
        val clayRobotCost: Materials,
        val obsidianRobotCost: Materials,
        val geodeRobotCost: Materials
    ) {
        val max = Materials(
            maxOf(oreRobotCost.ore, clayRobotCost.ore, obsidianRobotCost.ore, geodeRobotCost.ore),
            maxOf(oreRobotCost.clay, clayRobotCost.clay, obsidianRobotCost.clay, geodeRobotCost.clay),
            maxOf(oreRobotCost.obsidian, clayRobotCost.obsidian, obsidianRobotCost.obsidian, geodeRobotCost.obsidian),
            Int.MAX_VALUE,
        )
    }

    data class Materials(val ore: Int = 0, val clay: Int = 0, val obsidian: Int = 0, val geode: Int = 0) {
        operator fun plus(other: Materials) =
            Materials(ore + other.ore, clay + other.clay, obsidian + other.obsidian, geode + other.geode)

        operator fun minus(other: Materials) =
            Materials(ore - other.ore, clay - other.clay, obsidian - other.obsidian, geode - other.geode)

        fun canPayFor(cost: Materials) =
            (ore >= cost.ore && clay >= cost.clay && obsidian >= cost.obsidian && geode >= cost.geode)

        override fun toString() = "$ore/$clay/$obsidian/$geode"
    }

    private val format =
        Regex("Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.")
    private val blueprints = input.map { line ->
        val (index, oRo, cRo, obRo, obRc, gRo, gRob) = format.matchEntire(line)?.destructured ?: error("Invalid format")
        Blueprint(
            index.toInt(),
            Materials(ore = oRo.toInt()),
            Materials(ore = cRo.toInt()),
            Materials(ore = obRo.toInt(), clay = obRc.toInt()),
            Materials(ore = gRo.toInt(), obsidian = gRob.toInt())
        )
    }.toList()

    data class State(
        val time: Int,
        val materials: Materials = Materials(),
        val robots: Materials = Materials(ore = 1),
        val boughtRobot: Robot? = null,
        val prevState: State? = null
    ) {
        fun neighbors(blueprint: Blueprint): Sequence<State> =
            if (time == 0) emptySequence()
            else (sequenceOf(
                copy(
                    time = time - 1,
                    materials = materials + robots,
                    boughtRobot = null,
                    prevState = this
                )
            ) + Robot.values().asSequence().map { robot ->
                val cost = robot.costFunction(blueprint)
                if (materials.canPayFor(robot.costFunction(blueprint)) &&
                    (robot.materialFunction(robots) < robot.materialFunction(blueprint.max)) &&
                    (boughtRobot != null || prevState?.materials?.canPayFor(cost) == false)
                ) copy(
                    time = time - 1,
                    materials = materials + robots - cost,
                    robots = robots + robot.robot,
                    boughtRobot = robot,
                    prevState = this
                ) else null
            }).filterNotNull()


        override fun hashCode() = materials.hashCode() * 31 + robots.hashCode() * 47
        override fun equals(other: Any?): Boolean =
            this === other || (other is State && materials == other.materials && robots == other.robots)

        fun calcMaxGeodes(blueprint: Blueprint): Int {
            val maxOre = materials.ore + time * robots.ore
            val maxClay = materials.clay +
                    time * robots.clay +
                    (time * time * (maxOre / blueprint.clayRobotCost.ore) + 1) / 4
            val maxObsidian = materials.obsidian +
                    time * robots.obsidian +
                    (time * time * (maxClay / blueprint.obsidianRobotCost.clay) + 1) / 4
            return materials.geode + time * robots.geode +
                    (time * time * (maxObsidian / blueprint.geodeRobotCost.obsidian) + 1) / 4
        }

        override fun toString(): String =
            "State ($time${if (boughtRobot != null) "/$boughtRobot" else ""}) mats=$materials robots=$robots"
    }

    private fun calculate(blueprint: Blueprint, depth: Int): State {
        //println("Calculating for $blueprint up to $depth minutes")
        var max = State(depth)
        depthFirstSearch(max, nextSequence = { neighbors(blueprint) }, visitNext = {
            if (materials.geode > max.materials.geode) max = this
            calcMaxGeodes(blueprint) > max.materials.geode
        })
        //println(max)
        //generateSequence(max, State::prevState).toList().reversed().forEach { println(it) }
        return max
    }

    override fun calcPart1() = blueprints.sumOf { blueprint ->
        blueprint.index * calculate(blueprint, 24).materials.geode
    }

    override fun calcPart2() = blueprints.take(3).fold(1) { acc, blueprint ->
        acc * calculate(blueprint, 32).materials.geode
    }

    enum class Robot(
        val robot: Materials,
        val costFunction: Blueprint.() -> Materials,
        val materialFunction: Materials.() -> Int
    ) {
        ORE(Materials(ore = 1), Blueprint::oreRobotCost, Materials::ore),
        CLAY(Materials(clay = 1), Blueprint::clayRobotCost, Materials::clay),
        OBSIDIAN(Materials(obsidian = 1), Blueprint::obsidianRobotCost, Materials::obsidian),
        GEODE(Materials(geode = 1), Blueprint::geodeRobotCost, Materials::geode),
    }
}
