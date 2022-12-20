package marcdejonge.advent2022

import marcdejonge.advent2022.util.breadFirstSearch
import marcdejonge.advent2022.util.depthFirstSearch
import marcdejonge.advent2022.util.findMaxNonOverlappingCombinations

fun main() = DaySolver.printSolutions(::Day16)

class Day16 : DaySolver(16) {
    data class Valve(val ix: Int, val name: String, val rate: Int, val lineNr: Int = ix) {
        var neighbors: Map<Valve, Int> = emptyMap()
        override fun toString() = "Valve $name ($ix), rate=$rate, neighbors = ${neighbors.keys.map { it.name }}"
    }

    @JvmInline
    value class ValveSet(val value: Long = 0) {
        operator fun contains(valve: Valve) = value and (1L shl valve.ix) != 0L
        operator fun plus(valve: Valve) = ValveSet(value or (1L shl valve.ix))
    }

    private val valves: Map<String, Valve>
    private val lineFormat = Regex("Valve ([A-Z]+) has flow rate=(\\d+); tunnels? leads? to valves? ([A-Z, ]+)")
    private val startValve: Valve
    private val totalFlowRate: Int

    init {
        val (rawValves, paths) = input.mapIndexed { ix, line ->
            val (name, rate, canReach) = lineFormat.matchEntire(line)?.destructured ?: error("Invalid line: $line")
            Valve(ix, name, rate.toInt()) to canReach.split(", ")
        }.unzip()
        valves = rawValves.filter { it.rate > 0 || it.name == "AA" }.mapIndexed { ix, valve ->
            valve.copy(ix = ix)
        }.associateBy { it.name }

        if (valves.size > 63) error("More than 63 active valves is not supported right now")

        valves.values.forEach { valve ->
            val neighbors = breadFirstSearch(valve, 1, next = {
                paths[lineNr].asSequence().map { nextValveName -> rawValves.single { it.name == nextValveName } }
            }, visit = { it + 1 }) // Visit all valves and count the distance
                .filterKeys { it.rate > 0 && it.name != valve.name } // Ignore any targets that won't change the flowrate
                .toSortedMap(compareBy { it.rate }) // pre-sort by rate for the DFS to be more effective
                .mapKeys { valves[it.key.name]!! }
            valves[valve.name]?.let { store -> store.neighbors = neighbors }
        }

        startValve = valves.values.single { it.name == "AA" }
        totalFlowRate = valves.values.sumOf { it.rate }
    }

    data class State(
        val place: Valve,
        val timeLeft: Int,
        val openFlowRate: Int = 0,
        val openValves: ValveSet = ValveSet(),
        val totalFlow: Int = 0,
        val prevState: State? = null,
    ) {
        fun neighbors() = place.neighbors.asSequence().mapNotNull { (nextValve, cost) ->
            val newTimeLeft = timeLeft - cost
            if (newTimeLeft <= 0 || nextValve in openValves) null
            else State(
                place = nextValve,
                timeLeft = newTimeLeft,
                openFlowRate = openFlowRate + nextValve.rate,
                openValves = openValves + nextValve,
                totalFlow = totalFlow + newTimeLeft * nextValve.rate,
                prevState = this
            )
        }

        override fun toString() = "State(${
            generateSequence(this, State::prevState).joinToString(" <- ") { it.place.name }
        } totalFlow = $totalFlow)"
    }

    override fun calcPart1(): Int {
        var maxScore = 0
        depthFirstSearch(State(startValve, 30), State::neighbors) {
            if (totalFlow > maxScore) maxScore = totalFlow
            timeLeft > 1 && totalFlow + (totalFlowRate - openFlowRate) * (timeLeft - 2) > maxScore
        }
        return maxScore
    }

    override fun calcPart2(): Int {
        val maxTotalFlows = HashMap<ValveSet, State>()
        depthFirstSearch(State(startValve, 26), State::neighbors) {
            if (totalFlow >= (maxTotalFlows[openValves]?.totalFlow ?: 0)) {
                maxTotalFlows[openValves] = this
                timeLeft > 1
            } else false
        }

        return findMaxNonOverlappingCombinations(
            maxTotalFlows.values,
            getBitMask = { openValves.value }, getScore = { totalFlow }
        ).let { (first, second) -> first.totalFlow + second.totalFlow }
    }
}
