fun main() = Day25.run(RunMode.BOTH)

object Day25 : BasicDay() {

    override val expectedTestValuePart1 = 3

    override val solvePart1: ((List<String>, Boolean) -> Int) = { input, _ ->
        val locks = mutableListOf<List<Int>>()
        val keys = mutableListOf<List<Int>>()
        input.windowed(7, 8) { lines ->
            val transposed = lines.map { it.map { it.toChar() } }.transpose()
            val heights = transposed.map { it.count { it == '#' } - 1 }

            if (lines[0].all { it == '#' }) locks.add(heights)
            else keys.add(heights)
        }

        locks.sumOf { lock ->
            keys.map { key ->
                if(key.zip(lock).all { it.first + it.second <= 5 }) 1
                else 0
            }.sum()
        }
    }

}