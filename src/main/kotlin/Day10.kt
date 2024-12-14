fun main() = Day10.run(RunMode.BOTH)

object Day10 : BasicDay() {

    override val expectedTestValuePart1 = 36
    override val expectedTestValuePart2 = 81

    override val solvePart1: ((List<String>, Boolean) -> Int) = { input, _ ->
        val (topographicMap, trailheads) = parseInput(input)

        trailheads.sumOf {
            walkTrail(topographicMap, it).toSet().count()
        }
    }

    override val solvePart2: ((List<String>, Boolean) -> Int) = { input, _ ->
        val (topographicMap, trailheads) = parseInput(input)

        trailheads.sumOf {
            walkTrail(topographicMap, it).count()
        }
    }

    private fun parseInput(input: List<String>): Pair<List<List<Int>>, List<Position>> {
        val topographicMap = input.map { line ->
            line.map {
                if (it.isDigit()) it.digitToInt()
                else -1
            }
        }

        val trailheads = topographicMap.flatMapIndexed { row, line ->
            line.mapIndexedNotNull { col, height ->
                if (height == 0) Position(col, row)
                else null
            }
        }

        return topographicMap to trailheads
    }

    private data class Position(val x: Int, val y: Int) {
        operator fun plus(other: Position) = Position(this.x + other.x, this.y + other.y)
    }

    private fun List<List<Int>>.get(position: Position) = this.getOrNull(position.y)?.getOrNull(position.x)

    private fun walkTrail(map: List<List<Int>>, currentPosition: Position): List<Position> {
        val currentHeight = map.get(currentPosition)
        if (currentHeight == null) error("Fatal error, walked out of the map.")

        if (currentHeight == 9) return listOf(currentPosition)

        return listOf(
            currentPosition + Position(0, -1),
            currentPosition + Position(0, 1),
            currentPosition + Position(1, 0),
            currentPosition + Position(-1, 0)
        ).flatMap { position ->
            if (map.get(position) == currentHeight + 1) walkTrail(map, position)
            else emptyList()
        }
    }

}
