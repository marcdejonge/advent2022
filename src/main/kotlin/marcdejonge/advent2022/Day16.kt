package marcdejonge.advent2022

import marcdejonge.advent2022.util.breadFirstSearch
import marcdejonge.advent2022.util.depthFirstSearch
import kotlin.math.max

fun main() = DaySolver.printSolutions(::Day16)

class Day16 : DaySolver(16) {
    data class Valve(val ix: Int, val name: String, val rate: Int, val canReach: List<String>)

    @JvmInline
    value class ValveSet(private val value: Long = 0) {
        operator fun contains(valve: Valve) = value and (1L shl valve.ix) != 0L
        operator fun plus(valve: Valve) = ValveSet(value or (1L shl valve.ix))
    }

    private val lineFormat = Regex("Valve ([A-Z]+) has flow rate=(\\d+); tunnels? leads? to valves? ([A-Z, ]+)")
    private val valves: List<Valve> = input.mapIndexed { ix, line ->
        val (name, rate, canReach) = lineFormat.matchEntire(line)?.destructured ?: error("Invalid line: $line")
        Valve(ix, name, rate.toInt(), canReach.split(", "))
    }.toList()
    private val startValve = valves.single { it.name == "AA" }
    private val paths = valves.associateWith { startValve ->
        breadFirstSearch(startValve, 1, next = {
            canReach.asSequence().map { nextValveName -> valves.single { it.name == nextValveName } }
        }, visit = { it + 1 }) // Visit all valves and count the distance
            .filterKeys { it.rate > 0 } // Ignore any targets that won't change the flowrate
            .toSortedMap(compareBy { it.rate }) // pre-sort by rate for the DFS to be more effective
    }

    data class PersonalState(val place: Valve, val timeLeft: Int) {
        fun stepTo(valve: Valve, timeNeeded: Int) = PersonalState(valve, timeLeft - timeNeeded)
        override fun toString() = "${place.name} @ $timeLeft"
    }

    data class State(
        val me: PersonalState,
        val elephant: PersonalState,
        val openFlowRate: Int = 0,
        val openValves: ValveSet = ValveSet(),
        val totalFlow: Int = 0,
    ) {
        fun maxTotalFlow(totalRate: Int) = totalFlow +
                (totalRate - openFlowRate) * (max(me.timeLeft, elephant.timeLeft) - 2)

        fun iStepTo(valve: Valve, timeNeeded: Int): State = me.stepTo(valve, timeNeeded).let { next ->
            State(next, elephant, openFlowRate + valve.rate, openValves + valve, totalFlow + next.timeLeft * valve.rate)
        }

        fun elephantStepTo(valve: Valve, timeNeeded: Int): State = elephant.stepTo(valve, timeNeeded).let { next ->
            State(me, next, openFlowRate + valve.rate, openValves + valve, totalFlow + next.timeLeft * valve.rate)
        }
    }

    private val totalFlowRate = valves.sumOf { it.rate }
    private fun State.neighbors() =
        if (me.timeLeft >= elephant.timeLeft)
            paths[me.place]!!.asSequence().map { (nextValve, cost) -> iStepTo(nextValve, cost) }
                .filter { me.timeLeft > 0 && it.me.place !in openValves }
        else
            paths[elephant.place]!!.asSequence().map { (nextValve, cost) -> elephantStepTo(nextValve, cost) }
                .filter { elephant.timeLeft > 0 && it.elephant.place !in openValves }

    private inline fun calculate(startState: State, crossinline check: State.() -> Boolean): Int {
        var maxState = startState
        depthFirstSearch(startState, nextSequence = { neighbors() }, visitNext = {
            if (maxState.totalFlow < totalFlow) maxState = this
            check() && maxTotalFlow(totalFlowRate) > maxState.totalFlow
        })
        return maxState.totalFlow
    }

    override fun calcPart1() = calculate(
        State(PersonalState(startValve, 30), PersonalState(startValve, 0))
    ) {
        me.timeLeft > 1
    }

    override fun calcPart2() = calculate(
        State(PersonalState(startValve, 26), PersonalState(startValve, 26))
    ) {
        me.timeLeft > 1 && elephant.timeLeft > 1
    }
}
