fun main() = Day08.run(RunMode.BOTH)

object Day08 : BasicDay() {

    override val expectedTestValuePart1: Int = 14
    override val expectedTestValuePart2: Int = 34

    override val solvePart1: ((List<String>) -> Int) = { input ->
        val width = input.first().length
        val height = input.size

        val antennaGroups = parseInput(input)

        val positions = antennaGroups.flatMap { group ->
            group.value.combinations(2).flatMap { (first, second) ->
                val diff = first.position - second.position

                listOf(
                    first.position + diff,
                    second.position - diff
                )
            }.filter { it.inBounds(0..<width, 0..<height) }.toList()
        }

        positions.toSet().count()
    }

    override val solvePart2: ((List<String>) -> Int) = { input ->
        val width = input.first().length
        val height = input.size

        val antennaGroups = parseInput(input)

        val positions = antennaGroups.flatMap { group ->
            group.value.combinations(2).flatMap { (first, second) ->
                val diff = first.position - second.position

                val sequence1 = generateSequence(first.position) {
                    it + diff
                }.takeWhile { it.inBounds(0..<width, 0..<height) }
                val sequence2 = generateSequence(second.position) {
                        it - diff
                }.takeWhile { it.inBounds(0..<width, 0..<height) }

                sequence1 + sequence2
            }
        }

        positions.toSet().count()
    }

    private data class Position(val x: Int, val y: Int) {

        operator fun plus(other: Position) = Position(this.x + other.x, this.y + other.y)
        operator fun minus(other: Position) = Position(this.x - other.x, this.y - other.y)

        fun inBounds(width: IntRange, height: IntRange) = this.x in width && this.y in height

    }

    private data class Antenna(val frequency: Char, val position: Position)

    private fun parseInput(input: List<String>) =
        input.flatMapIndexed { row, line ->
            line.mapIndexedNotNull { col, char ->
                if (char == '.') null
                else Antenna(char, Position(col, row))
            }
        }.groupBy { it.frequency }

}
