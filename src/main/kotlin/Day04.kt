fun main() = Day04.run(RunMode.BOTH)

object Day04 : BasicDay() {

    override val expectedTestValuePart1: Int = 18
    override val expectedTestValuePart2: Int = 9

    override val solvePart1: ((List<String>, Boolean) -> Int) = { input, _ ->
        val inputTransposed = input.transpose()
        val inputRotated45 = input.rotate45Degrees()
        val inputRotated315 = input.map { it.reversed() }.rotate45Degrees()

        (input + inputTransposed + inputRotated45 + inputRotated315).sumOf { line ->
            """XMAS""".toRegex().findAll(line + line.reversed()).count()
        }
    }

    override val solvePart2: ((List<String>, Boolean) -> Int) = { input, _ ->
        input.withIndex().sumOf { (y, row) ->
            if (y == 0 || y == input.size - 1) return@sumOf 0

            row.withIndex().count { (x, char) ->
                if (x == 0 || x == row.length - 1) return@count false

                if (char == 'A') {
                    if (((input[y - 1][x - 1] == 'S' && input[y + 1][x + 1] == 'M') || (input[y - 1][x - 1] == 'M' && input[y + 1][x + 1] == 'S'))
                        && ((input[y + 1][x - 1] == 'S' && input[y - 1][x + 1] == 'M') || (input[y + 1][x - 1] == 'M' && input[y - 1][x + 1] == 'S'))) {
                        return@count true
                    }
                }
                return@count false
            }
        }
    }

    fun List<String>.transpose() = this.map { it.toList() }.transpose().map { it.joinToString("") }

    fun List<String>.rotate45Degrees() = this.map { it.toList() }.rotate45Degrees().map { it.joinToString("") }

}