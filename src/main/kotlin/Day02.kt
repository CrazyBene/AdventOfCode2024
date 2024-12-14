fun main() = Day02.run(RunMode.BOTH)

object Day02 : BasicDay() {

    override val expectedTestValuePart1: Int = 2
    override val expectedTestValuePart2: Int = 4

    override val solvePart1: ((List<String>, Boolean) -> Int)? = { input, _ ->
        input.filter { report ->
            val levels = report.split(' ').map { it.toInt() }

            checkIfReportIsSave(levels)
        }.count()
    }

    override val solvePart2: ((List<String>, Boolean) -> Int)? = { input, _ ->
        input.filter { report ->
            val levels = report.split(' ').map { it.toInt() }

            if (checkIfReportIsSave(levels))
                return@filter true

            levels.forEachIndexed { index, _ ->
                val levelsToCheck = levels.filterIndexed { i, _ -> i != index }
                if (checkIfReportIsSave(levelsToCheck))
                    return@filter true
            }

            false
        }.count()
    }

    fun checkIfReportIsSave(levels: List<Int>): Boolean {
        val differences = levels.zipWithNext().map { (first, second) ->
            first - second
        }

        return if (differences[0] > 0) {
            differences.all { it in 1..3 }
        } else {
            differences.all { it in -3..-1 }
        }
    }

}