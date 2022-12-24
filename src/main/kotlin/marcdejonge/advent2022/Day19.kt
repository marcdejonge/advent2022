package marcdejonge.advent2022

import marcdejonge.advent2022.util.depthFirstSearch

fun main() = DaySolver.printSolutions(::Day19)

class Day19 : DaySolver(19) {
    data class Materials(val ore: Int = 0, val clay: Int = 0, val obsidian: Int = 0, val geode: Int = 0) {
        operator fun plus(o: Materials) = Materials(ore + o.ore, clay + o.clay, obsidian + o.obsidian, geode + o.geode)
        operator fun minus(o: Materials) = Materials(ore - o.ore, clay - o.clay, obsidian - o.obsidian, geode - o.geode)
        fun canPay(m: Materials) = ore >= m.ore && clay >= m.clay && obsidian >= m.obsidian && geode >= m.geode
    }

    data class Blueprint(
        val ix: Int, val oreBot: Materials, val clayBot: Materials, val obsidianBot: Materials, val geodeBot: Materials
    ) {
        val maxRobots = Materials(
            maxOf(oreBot.ore, clayBot.ore, obsidianBot.ore, geodeBot.ore),
            maxOf(oreBot.clay, clayBot.clay, obsidianBot.clay, geodeBot.clay),
            maxOf(oreBot.obsidian, clayBot.obsidian, obsidianBot.obsidian, geodeBot.obsidian),
            Int.MAX_VALUE,
        )
    }

    enum class Robot(
        val robot: Materials, val getCost: Blueprint.() -> Materials, val getType: Materials.() -> Int
    ) {
        ORE(Materials(ore = 1), Blueprint::oreBot, Materials::ore),
        CLAY(Materials(clay = 1), Blueprint::clayBot, Materials::clay),
        OBSIDIAN(Materials(obsidian = 1), Blueprint::obsidianBot, Materials::obsidian),
        GEODE(Materials(geode = 1), Blueprint::geodeBot, Materials::geode),
    }

    private val format =
        Regex("Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.")
    private val blueprints = input.map { line ->
        with(format.matchEntire(line) ?: error("Invalid format")) {
            Blueprint(
                groupValues[1].toInt(),
                Materials(ore = groupValues[2].toInt()),
                Materials(ore = groupValues[3].toInt()),
                Materials(ore = groupValues[4].toInt(), clay = groupValues[5].toInt()),
                Materials(ore = groupValues[6].toInt(), obsidian = groupValues[7].toInt())
            )
        }
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
                copy(time = time - 1, materials = materials + robots, boughtRobot = null, prevState = this)
            ) + Robot.values().asSequence().map { robot ->
                val cost = robot.getCost(blueprint)
                if (materials.canPay(cost) && // We should be able to pay for it
                    (robot.getType(robots) < robot.getType(blueprint.maxRobots)) && // Don't allow more bots than we can consume
                    (boughtRobot != null || prevState?.materials?.canPay(cost) == false) // Don't allow buying a bot we could have bought the previous round
                ) copy(
                    time = time - 1,
                    materials = materials + robots - cost,
                    robots = robots + robot.robot,
                    boughtRobot = robot, prevState = this
                ) else null
            }).filterNotNull()


        fun calcMaxGeodes(blueprint: Blueprint): Int {
            val maxOre = materials.ore + time * robots.ore
            val maxClay = materials.clay + time * robots.clay +
                    (time * time * (maxOre / blueprint.clayBot.ore) + 1) / 5
            val maxObsidian = materials.obsidian + time * robots.obsidian +
                    (time * time * (maxClay / blueprint.obsidianBot.clay) + 1) / 5
            return materials.geode + time * robots.geode +
                    (time * time * (maxObsidian / blueprint.geodeBot.obsidian) + 1) / 5
        }
    }

    private fun calculate(blueprint: Blueprint, depth: Int): State {
        var max = State(depth)
        depthFirstSearch(max, nextSequence = { neighbors(blueprint) }, visitNext = {
            if (materials.geode > max.materials.geode) max = this
            calcMaxGeodes(blueprint) > max.materials.geode
        })
        return max
    }

    override fun calcPart1() = blueprints.sumOf { blueprint ->
        blueprint.ix * calculate(blueprint, 24).materials.geode
    }

    override fun calcPart2() = blueprints.take(3).fold(1) { acc, blueprint ->
        acc * calculate(blueprint, 32).materials.geode
    }
}
