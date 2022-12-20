package marcdejonge.advent2022

import marcdejonge.advent2022.util.breadFirstSearch
import marcdejonge.advent2022.util.depthFirstSearch
import marcdejonge.advent2022.util.findMaxNonOverlappingCombinations

fun main() = DaySolver.printSolutions(::Day16)

class Day16 : DaySolver(16) {
    data class Valve(val ix: Int, val name: String, val rate: Int, val lineNr: Int = ix) {
        lateinit var neighbors: List<Pair<Valve, Int>>
        override fun toString() = "Valve $name ($ix), rate=$rate, neighbors = ${neighbors.map { it.first.name }}"
    }

    @JvmInline
    value class ValveSet(val value: Long = 0) {
        operator fun contains(valve: Valve) = value and (1L shl valve.ix) != 0L
        operator fun plus(valve: Valve) = ValveSet(value or (1L shl valve.ix))
    }

    private val lineFormat = Regex("Valve ([A-Z]+) has flow rate=(\\d+); tunnels? leads? to valves? ([A-Z, ]+)")
    private val startValve: Valve
    private val totalFlowRate: Int

    init {
        val (rawValves, paths) = input.mapIndexed { ix, line ->
            val (name, rate, canReach) = lineFormat.matchEntire(line)?.destructured ?: error("Invalid line: $line")
            Valve(ix, name, rate.toInt()) to canReach.split(", ")
        }.unzip()

        val relevantValves = rawValves.filter { it.rate > 0 || it.name == "AA" }.mapIndexed { ix, valve ->
            valve.copy(ix = ix)
        }.also { if (it.size > 63) error("More than 63 active valves is not supported right now") }
        startValve = relevantValves.single { it.name == "AA" }
        totalFlowRate = relevantValves.sumOf { it.rate }

        relevantValves.forEach { valve ->
            valve.neighbors = breadFirstSearch(valve, 1, next = {
                paths[lineNr].asSequence().map { nextValveName -> rawValves.single { it.name == nextValveName } }
            }, visit = { it + 1 }) // Visit all valves and count the distance
                .mapNotNull { (key, cost) -> relevantValves.singleOrNull { it.name == key.name }?.let { it to cost } }
                .filter { (target, _) -> target != startValve || startValve.rate > 0 }
                .sortedBy { it.first.rate }  // pre-sort by rate for the DFS to be more effective
        }
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
            generateSequence(this, State::prevState).toList().reversed().joinToString(" -> ") { it.place.name }
        } totalFlow = $totalFlow)"
    }

    override fun calcPart1(): Int {
        var maxState = State(startValve, 30)
        depthFirstSearch(maxState, State::neighbors) {
            if (totalFlow > maxState.totalFlow) maxState = this
            timeLeft > 1 && totalFlow + (totalFlowRate - openFlowRate) * (timeLeft - 2) > maxState.totalFlow
        }
        return maxState.totalFlow
    }

    override fun calcPart2(): Int {
        val maxTotalFlows = HashMap<ValveSet, State>()
        var maxState = State(startValve, 26)
        depthFirstSearch(maxState, State::neighbors) {
            if (totalFlow > maxState.totalFlow) maxState = this
            if (totalFlow >= (maxTotalFlows[openValves]?.totalFlow ?: 0)) {
                maxTotalFlows[openValves] = this
                timeLeft > 1
            } else {
                timeLeft > 1 && totalFlow + (totalFlowRate - openFlowRate) * (timeLeft - 2) > maxState.totalFlow
            }
        }

        return findMaxNonOverlappingCombinations(
            maxTotalFlows.values,
            getBitMask = { openValves.value }, getScore = { totalFlow }
        ).let { (first, second) -> first.totalFlow + second.totalFlow }
    }
}
