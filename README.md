Advent of Code 2022
===

These are my solutions for https://adventofcode.com/2022 written in Kotlin.

The goal is readable code that is still pretty quick.

Execution from CLI
---

There is a gradle task to calculate any day from the CLI, which is just called day. You can provide the day property to
change the day you want to calculate. For example:

```bash
~: ./gradlew day -Pday=2

> Task :day
Loaded in ?,??? seconds
Day 2:
    Part 1: ???
    Part 2: ???
Calculated in ?,??? seconds

BUILD SUCCESSFUL in ???ms
8 actionable tasks: 1 executed, 7 up-to-date
```

There is also the `file_postfix` property if you want to execute one of the big files for execution. Normally
the `src/main/kotlin/dayXX.txt` are used as the input for normal execution, but when this property is provided the
file `src/main/kotlin/dayXX${postfix}.txt` will be read instead. This can be useful to test other (or way bigger) files. 
