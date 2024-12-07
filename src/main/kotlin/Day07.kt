import kotlin.math.log10
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
                { acc, number -> concatNumbers(acc, number) }
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
            for (operatorCombination in 0..<numberOfOperators.toDouble().pow(equation.numbers.size - 1).toInt()) {
                run loop@{
                    val result =
                        equation.numbers.drop(1).foldIndexed(equation.numbers.first()) { numberIndex, acc, number ->
                            if (acc > equation.wantedResult) return@loop

                            val operatorIndex = getOperatorIndex(operatorCombination, numberIndex, numberOfOperators)
                            operatorFunctions[operatorIndex](acc, number)
                        }

                    if (result == equation.wantedResult) return@filter true
                }
            }
            false
        }.toList()
    }

    private fun concatNumbers(number1: Long, number2: Long): Long {
        val digits = log10(number2.toDouble()).toInt() + 1
        return (10.0.pow(digits)).toInt() * number1 + number2
    }

    private fun getOperatorIndex(operatorCombination: Int, numberIndex: Int, numberOfOperators: Int): Int {
        var operatorIndex = operatorCombination
        repeat(numberIndex) {
            operatorIndex /= numberOfOperators
        }
        return operatorIndex % numberOfOperators
    }

}
