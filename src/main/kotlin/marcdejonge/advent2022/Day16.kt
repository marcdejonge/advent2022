package marcdejonge.advent2022

import marcdejonge.advent2022.util.breadFirstSearch
import marcdejonge.advent2022.util.depthFirstSearch

fun main() = DaySolver.printSolutions(::Day16)

class Day16 : DaySolver(16) {
    data class Valve(val ix: Int, val name: String, val rate: Int, val lineNr: Int = ix) {
        var neighbors: Map<Valve, Int> = emptyMap()
        override fun toString() = "Valve $name ($ix), rate=$rate, neighbors = ${neighbors.keys.map { it.name }}"
    }

    @JvmInline
    value class ValveSet(private val value: Long = 0) {
        operator fun contains(valve: Valve) = value and (1L shl valve.ix) != 0L
        operator fun plus(valve: Valve) = ValveSet(value or (1L shl valve.ix))
        fun overlaps(other: ValveSet) = this.value and other.value != 0L
    }

    private val lineFormat = Regex("Valve ([A-Z]+) has flow rate=(\\d+); tunnels? leads? to valves? ([A-Z, ]+)")
    private val startValve: Valve
    private val totalFlowRate: Int

    init {
        val (rawValves, paths) = input.mapIndexed { ix, line ->
            val (name, rate, canReach) = lineFormat.matchEntire(line)?.destructured ?: error("Invalid line: $line")
            Valve(ix, name, rate.toInt()) to canReach.split(", ")
        }.unzip()
        val valves = rawValves.filter { it.rate > 0 || it.name == "AA" }.mapIndexed { ix, valve ->
            valve.copy(ix = ix)
        }.associateBy { it.name }
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
                nextValve,
                newTimeLeft,
                openFlowRate + nextValve.rate,
                openValves + nextValve,
                totalFlow + newTimeLeft * nextValve.rate,
                this
            )
        }

        override fun toString() = "State(${
            generateSequence(this, State::prevState).joinToString(" <- ") { it.place.name }
        } totalFlow = $totalFlow)"
    }

    private fun calculateTotalFlows(startState: State): HashMap<ValveSet, State> {
        val maxTotalFlows = HashMap<ValveSet, State>()
        depthFirstSearch(startState, State::neighbors) {
            if ((maxTotalFlows[openValves]?.totalFlow ?: 0) < totalFlow) {
                maxTotalFlows[openValves] = this
                timeLeft > 1
            } else false
        }
        return maxTotalFlows
    }

    override fun calcPart1(): Int = calculateTotalFlows(State(startValve, 30)).values.maxOf { it.totalFlow }

    override fun calcPart2(): Int {
        val maxTotalFlows = calculateTotalFlows(State(startValve, 26))
        val searchArea = maxTotalFlows.toList().sortedByDescending { it.second.totalFlow }

        var maxTotalFlow = 0
        for (myPathIx in (0 until searchArea.lastIndex).reversed()) {
            val (myPath, myState) = searchArea[myPathIx]
            for (elephantPathIx in (myPathIx + 1)..searchArea.lastIndex) {
                val (elephantPath, elephantState) = searchArea[elephantPathIx]
                if (!myPath.overlaps(elephantPath)) {
                    if (myState.totalFlow + elephantState.totalFlow > maxTotalFlow) {
                        maxTotalFlow = myState.totalFlow + elephantState.totalFlow
                    }
                    break
                }
            }
        }

        return maxTotalFlow
    }
}
