fun main() = Day03.run(RunMode.BOTH)

object Day03 : BasicDay(separateTestFiles = true) {

    private val mulRegex = """mul\((\d{1,3}),(\d{1,3})\)""".toRegex()

    private val doRegex = """do\(\)""".toRegex()
    private val dontRegex = """don't\(\)""".toRegex()

    override val expectedTestValuePart1: Int = 161
    override val expectedTestValuePart2: Int = 48

    override val solvePart1: ((List<String>) -> Int) = { input ->
        input.sumOf { line ->
            val matches = mulRegex.findAll(line)

            matches.sumOf { match ->
                val firstValue = match.groupValues[1].toInt()
                val secondValue = match.groupValues[2].toInt()

                firstValue * secondValue
            }
        }
    }

    override val solvePart2: ((List<String>) -> Int) = { input ->
        var mulActivated = true

        input.sumOf { line ->
            val mulInstructions = mulRegex.findAll(line).map { Instruction(InstructionType.MUL, it) }
            val doInstructions = doRegex.findAll(line).map { Instruction(InstructionType.DO, it) }
            val dontInstructions = dontRegex.findAll(line).map { Instruction(InstructionType.DONT, it) }

            val allInstructions = mulInstructions + doInstructions + dontInstructions
            val sortedInstructions = allInstructions.sortedBy { it.matchResult.range.first }

            sortedInstructions.sumOf { match ->
                when (match.type) {
                    InstructionType.MUL -> {
                        if (mulActivated) match.matchResult.groupValues[1].toInt() * match.matchResult.groupValues[2].toInt()
                        else 0
                    }

                    InstructionType.DO -> {
                        mulActivated = true
                        0
                    }

                    InstructionType.DONT -> {
                        mulActivated = false
                        0
                    }
                }
            }
        }
    }

    enum class InstructionType {
        MUL, DO, DONT
    }

    data class Instruction(val type: InstructionType, val matchResult: MatchResult)

}