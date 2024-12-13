import Day12.Position

fun main() = Day12.run(RunMode.BOTH)

object Day12 : BasicDay() {

    override val expectedTestValuePart1 = 140
    override val expectedTestValuePart2 = 1206

    override val solvePart1: ((List<String>) -> Int) = { input ->
        val garden = Garden.parseInput(input)

        val gardenPlots = calculatePlots(garden)

        gardenPlots.sumOf { plot ->
            plot.calculateFencePrice()
        }
    }

    override val solvePart2: ((List<String>) -> Int) = { input ->
        val garden = Garden.parseInput(input)

        val gardenPlots = calculatePlots(garden)

        gardenPlots.sumOf { plot ->
            plot.calculateDiscountedFencePrice()
        }
    }

    // this does not look like a good algorithm, but for now its works and is fast enough
    private fun getSameNeighbors(
        position: Position,
        char: Char,
        garden: Garden,
        alreadyInSet: Set<Position>
    ): MutableSet<Position> {
        return listOf(
            position + Position(1, 0),
            position + Position(-1, 0),
            position + Position(0, 1),
            position + Position(0, -1)
        ).fold(mutableSetOf(position)) { acc, neighbor ->
            if (neighbor !in alreadyInSet && garden.getOrNull(neighbor) == char) {
                acc += getSameNeighbors(neighbor, char, garden, alreadyInSet + acc)
            }
            acc
        }
    }

    private fun calculatePlots(garden: Garden): MutableSet<GardenPlot> {
        val gardenPlots = mutableSetOf<GardenPlot>()
        val inNoPlot = List(garden.height) { y ->
            List(garden.width) { x ->
                Position(x, y)
            }
        }.flatten().toMutableList()

        while (inNoPlot.isNotEmpty()) {
            val startPos = inNoPlot.first()
            val plotChar = garden.getOrNull(startPos) ?: error("Something went wrong")

            val plot = GardenPlot(getSameNeighbors(startPos, plotChar, garden, emptySet<Position>()))
            gardenPlots.add(plot)

            plot.positions.forEach { p ->
                inNoPlot.remove(p)
            }
        }

        return gardenPlots
    }

    private data class Position(val x: Int, val y: Int) {
        operator fun plus(other: Position) = Position(this.x + other.x, this.y + other.y)
    }

    private data class Garden(private val map: List<List<Char>>) {

        val width = map.first().size
        val height = map.size

        fun getOrNull(position: Position) = map.getOrNull(position.y)?.getOrNull(position.x)

        companion object {
            fun parseInput(input: List<String>) = Garden(input.map { line -> line.map { it } })
        }

    }

    private data class GardenPlot(val positions: Set<Position>) {

        val size = positions.size

        fun calculatePerimeter(): Int {
            return positions.sumOf { position ->
                listOf(
                    position + Position(1, 0),
                    position + Position(-1, 0),
                    position + Position(0, 1),
                    position + Position(0, -1)
                ).filter { possibleNeighbor ->
                    possibleNeighbor !in positions
                }.count()
            }
        }

        fun calculateFencePrice() = this.size * this.calculatePerimeter()

        fun calculateBorderCount(): Int {
            var borderCount = 0


            listOf(
                Position(0, -1) to listOf(Position(-1, 0), Position(1, 0)),
                Position(0, 1) to listOf(Position(-1, 0), Position(1, 0)),
                Position(1, 0) to listOf(Position(0, -1), Position(0, 1)),
                Position(-1, 0) to listOf(Position(0, -1), Position(0, 1)),
            ).forEach { direction ->
                val seen = mutableSetOf<Position>()
                positions.filter { position ->
                    position + direction.first !in positions
                }.forEach { position ->
                    if (position in seen) return@forEach

                    seen += position
                    borderCount++

                    direction.second.forEach { testDirection ->
                        var newPosition = position + testDirection
                        while (newPosition in positions && newPosition + direction.first !in positions) {
                            seen += newPosition
                            newPosition += testDirection
                        }
                    }
                }
            }

            return borderCount
        }

        fun calculateDiscountedFencePrice() = this.size * this.calculateBorderCount()

    }

}
