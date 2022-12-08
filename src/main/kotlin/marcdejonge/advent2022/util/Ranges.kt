package marcdejonge.advent2022.util

inline fun IntProgression.bothWays(func: IntProgression.() -> Unit) {
    func(this)
    func(this.reversed())
}
