fun main() = Day21.run(RunMode.BOTH)

object Day21 : BasicDay() {

    override val expectedTestValuePart1 = 126384L
    override val expectedTestValuePart2 = 154115708116294L

    private val numericalKeypad = listOf(
        listOf('7', '8', '9'),
        listOf('4', '5', '6'),
        listOf('1', '2', '3'),
        listOf(null, '0', 'A')
    ).toKeypad()

    private val directionalKeypad = listOf(
        listOf(null, '^', 'A'),
        listOf('<', 'v', '>'),
    ).toKeypad()

    override val solvePart1: ((List<String>, Boolean) -> Long) = { input, _ ->
        val codes = input.toList()

        codes.sumOf { code ->
            solve(code, numericalKeypad).minOf {
                calculateLength(it, 2)
            } * code.toNumericValue()
        }
    }

    override val solvePart2: ((List<String>, Boolean) -> Long) = { input, _ ->
        val codes = input.toList()

        codes.sumOf { code ->
            solve(code, numericalKeypad).minOf {
                calculateLength(it, 25)
            } * code.toNumericValue()
        }
    }

    private fun List<List<Char?>>.toKeypad(): MutableMap<Pair<Char, Char>, List<String>> {
        val keypadMap = this.flatMapIndexed { row, line ->
            line.mapIndexedNotNull { col, char ->
                if (char == null) null
                else char to Vector2(col, row)
            }
        }.toMap()


        val possibleMoves = mutableMapOf<Pair<Char, Char>, List<String>>()
        keypadMap.entries.forEach { keyA ->
            keypadMap.entries.forEach { keyB ->
                if (keyA == keyB) {
                    possibleMoves[keyA.key to keyB.key] = listOf("A")
                    return@forEach
                }
                val possibilities = mutableListOf<String>()
                var minLengthToKeyB = Int.MAX_VALUE
                val queue = ArrayDeque<Pair<Vector2, String>>().apply { add(keyA.value to "") }

                run queueLoop@{
                    while (queue.isNotEmpty()) {
                        val (position, moves) = queue.removeFirst()
                        listOf(
                            position + Vector2(0, -1) to "^",
                            position + Vector2(0, 1) to "v",
                            position + Vector2(-1, 0) to "<",
                            position + Vector2(1, 0) to ">",
                        ).forEach { (newPosition, move) ->
                            if (newPosition !in keypadMap.values) return@forEach

                            if (keyB.value == newPosition) {
                                if (minLengthToKeyB < moves.length + 1) return@queueLoop
                                minLengthToKeyB = moves.length + 1
                                possibilities += moves + move + "A"
                            } else {
                                queue.add(newPosition to moves + move)
                            }
                        }
                    }
                }

                possibleMoves[keyA.key to keyB.key] = possibilities
            }
        }

        return possibleMoves
    }

    private fun solve(target: String, keypad: MutableMap<Pair<Char, Char>, List<String>>): List<String> {
        val options = ("A$target").map { it.toChar() }.zipWithNext().map { it -> keypad[it.first to it.second]!! }
        return cartesianProduct(*options.toTypedArray()).map { it.joinToString("") }
    }

    private fun calculateLength(target: String, depth: Int): Long {
        val cache = mutableMapOf<Pair<String, Int>, Long>()
        fun inner(target: String, depth: Int): Long {
            if (depth == 1) return "A$target".map { it.toChar() }.zipWithNext()
                .sumOf { directionalKeypad[it.first to it.second]!![0].length.toLong() }

            return cache.getOrPut(target to depth) {
                ("A$target").map { it.toChar() }.zipWithNext().sumOf {
                    directionalKeypad[it.first to it.second]!!.minOf {
                        inner(it, depth - 1)
                    }
                }
            }
        }

        return inner(target, depth)
    }

    private fun String.toNumericValue() = this.dropLast(1).toInt()

    private data class Vector2(val x: Int, val y: Int) {
        operator fun plus(other: Vector2) = Vector2(this.x + other.x, this.y + other.y)
    }

}