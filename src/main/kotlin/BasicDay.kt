import kotlin.time.measureTimedValue

enum class RunMode {
    PART1, PART2, BOTH
}

abstract class BasicDay(separateTestFiles: Boolean = false) {

    private val day = this.javaClass.name.substringAfter("Day")

    private val testInputFileNamePart1 =
        if (separateTestFiles) "Day${day}TestInputPart1.txt" else "Day${day}TestInput.txt"
    private val testInputFileNamePart2 =
        if (separateTestFiles) "Day${day}TestInputPart2.txt" else "Day${day}TestInput.txt"
    private val realInputFileName = "Day${day}.txt"

    private val testInputPart1 = readInputFromResources(testInputFileNamePart1)
    private val testInputPart2 = readInputFromResources(testInputFileNamePart2)
    private val realInput = readInputFromResources(realInputFileName)

    protected open val expectedTestValuePart1: Any? = null
    protected open val expectedTestValuePart2: Any? = null

    protected open val solvePart1: ((input: List<String>, isTest: Boolean) -> Any)? = null
    protected open val solvePart2: ((input: List<String>, isTest: Boolean) -> Any)? = null

    fun run(runMode: RunMode = RunMode.BOTH) {
        println("Advent of Code 2024 - Day $day")
        println("-".repeat(28))

        if (realInput == null) {
            println()
            println("⚠ Careful real data input file missing, skipping real data runs.", ConsoleColor.YELLOW)
            println()
            println("-".repeat(14))
        }

        if (runMode == RunMode.PART1 || runMode == RunMode.BOTH) {
            if (checkFunctionImplementation(1, solvePart1))
                if (checkTestInput(1, solvePart1!!, testInputPart1, expectedTestValuePart1))
                    runOnRealData(1, solvePart1!!, realInput)
        }

        println()
        println("-".repeat(14))

        if (runMode == RunMode.PART2 || runMode == RunMode.BOTH) {
            if (checkFunctionImplementation(2, solvePart2))
                if (checkTestInput(2, solvePart2!!, testInputPart2, expectedTestValuePart2))
                    runOnRealData(2, solvePart2!!, realInput)
        }
    }

    private fun checkFunctionImplementation(partNumber: Int, solveFunction: ((List<String>, Boolean) -> Any)?): Boolean {
        if (solveFunction != null) {
            return true
        } else {
            println()
            println("» Solve function $partNumber not implemented yet, skipping part.", ConsoleColor.CYAN)
            return false
        }
    }

    private fun checkTestInput(
        partNumber: Int, solveFunction: (List<String>, Boolean) -> Any, testInput: List<String>?, expectedValueTestPart: Any?
    ): Boolean {
        println()
        if (testInput == null) {
            println("» Missing test input file, skipping test.", ConsoleColor.CYAN)
            return true
        }

        if (expectedValueTestPart == null) {
            println("» Missing expected test value, skipping test.", ConsoleColor.CYAN)
            return true
        }

        println("Running part $partNumber on test data ...")
        val (testSolution, testDuration) = measureTimedValue { solveFunction(testInput, true) }
        if (testSolution == expectedValueTestPart) {
            println("✔ Passed part $partNumber test - took $testDuration", ConsoleColor.GREEN)
            return true
        }

        println(
            "✖ Failed part $partNumber test. Actual value $testSolution did not match expected value $expectedValueTestPart. - took $testDuration\"",
            ConsoleColor.RED
        )
        return false
    }

    private fun runOnRealData(partNumber: Int, solveFunction: (List<String>, Boolean) -> Any, realInput: List<String>?) {
        println()
        if (realInput == null) {
            println("» Missing real input file, skipping run.", ConsoleColor.CYAN)
            return
        }

        println("Running part $partNumber on real data ...")
        val (solution, duration) = measureTimedValue { solveFunction(realInput, false) }
        println("> Solution for part $partNumber: $solution - took $duration", ConsoleColor.PURPLE)
    }

}