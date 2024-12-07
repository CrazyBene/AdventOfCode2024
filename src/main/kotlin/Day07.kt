import kotlin.math.pow

fun main() = Day07.run(RunMode.BOTH)

object Day07 : BasicDay() {

    override val expectedTestValuePart1 = 3749L
    override val expectedTestValuePart2 = 11387L

    override val solvePart1: ((List<String>) -> Long) = { input ->
        val equations = parseInput(input)

        getSolvableEquations(
            equations,
            listOf(
                { acc, number -> acc + number },
                { acc, number -> acc * number }
            )
        ).sumOf { it.wantedResult }
    }

    override val solvePart2: ((List<String>) -> Long) = { input ->
        val equations = parseInput(input)

        getSolvableEquations(
            equations,
            listOf(
                { acc, number -> acc + number },
                { acc, number -> acc * number },
                { acc, number -> "$acc$number".toLong() }
            )
        ).sumOf { it.wantedResult }
    }

    private data class Equation(val wantedResult: Long, val numbers: List<Long>)

    private fun parseInput(input: List<String>): List<Equation> {
        return input.map { line ->
            line.split(": ").let { (testValueString, numberStrings) ->
                Equation(testValueString.toLong(), numberStrings.split(' ').map { it.toLong() })
            }
        }
    }

    private fun getSolvableEquations(
        equations: List<Equation>, operatorFunctions: List<(Long, Long) -> Long>
    ): List<Equation> {
        val numberOfOperators = operatorFunctions.size

        return equations.parallelStream().filter { equation ->
            for (operatorIndex in 0..<numberOfOperators.toDouble().pow(equation.numbers.size - 1).toLong()) {
                val result =
                    equation.numbers.drop(1).foldIndexed(equation.numbers.first()) { numberIndex, acc, number ->
                        val functionIndex = operatorIndex.toString(numberOfOperators)
                            .padStart(equation.numbers.size - 1, '0')[numberIndex].toString().toInt()
                        operatorFunctions[functionIndex](acc, number)
                    }

                if (result == equation.wantedResult) return@filter true
            }
            false
        }.toList()
    }

}
