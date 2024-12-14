fun main() = Day05.run(RunMode.BOTH)

object Day05 : BasicDay() {

    override val expectedTestValuePart1 = 143
    override val expectedTestValuePart2 = 123

    override val solvePart1: ((List<String>, Boolean) -> Int) = { input, _ ->
        val (pageOrderingRules, updates) = parseInput(input)

        updates.filter { update ->
            checkOrder(update, pageOrderingRules)
        }.sumOf { it.getMiddlePageNumber() }
    }

    override val solvePart2: ((List<String>, Boolean) -> Int) = { input, _ ->
        val (pageOrderingRules, updates) = parseInput(input)

        updates.filterNot { update ->
            checkOrder(update, pageOrderingRules)
        }.map { update ->
            update.sortedWith { a, b ->
                if (pageOrderingRules.any { it.first == a && it.second == b })
                    return@sortedWith -1
                else if (pageOrderingRules.any { it.first == b && it.second == a })
                    return@sortedWith 1

                return@sortedWith 0
            }
        }.sumOf { it.getMiddlePageNumber() }
    }

    fun parseInput(input: List<String>): Pair<List<Pair<Int, Int>>, List<List<Int>>> {
        val pageOrderingRules = input.takeWhile { it.isNotBlank() }
            .map { line ->
                line.split('|').let { it[0].toInt() to it[1].toInt() }
            }

        val updates = input.takeLastWhile { it.isNotBlank() }
            .map { line ->
                line.split(',').map { it.toInt() }
            }

        return pageOrderingRules to updates
    }

    fun checkOrder(update: List<Int>, pageOrderingRules: List<Pair<Int, Int>>): Boolean {
        return pageOrderingRules.all { rule ->
            val indexFirstPage = update.indexOfFirst { it == rule.first }
            val indexSecondPage = update.indexOfFirst { it == rule.second }

            indexFirstPage == -1 || indexSecondPage == -1 || indexFirstPage < indexSecondPage
        }
    }

    inline fun <reified T> List<T>.getMiddlePageNumber(): T = this[(this.size - 1) / 2]

}
