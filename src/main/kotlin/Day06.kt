import kotlin.collections.plusAssign

fun main() = Day06.run(RunMode.BOTH)

object Day06 : BasicDay() {

    override val expectedTestValuePart1 = 41
    override val expectedTestValuePart2 = 6

    override val solvePart1: ((List<String>) -> Int) = { input ->
        val width = input.first().length
        val height = input.size

        val (obstructions, startPosition) = parseInput(input)

        val placesVisited = walkThroughLab(startPosition, north, obstructions, width, height).first
        placesVisited.count()
    }

    override val solvePart2: ((List<String>) -> Int) = { input ->
        val width = input.first().length
        val height = input.size

        val (obstructions, startPosition) = parseInput(input)

        val placesVisited = walkThroughLab(startPosition, north, obstructions, width, height).first
        val possibleObstructions = placesVisited.toMutableSet().apply { remove(startPosition) }.toSet()

        possibleObstructions.parallelStream().filter { possibleObstruction ->
            val obstructionsToTest = obstructions.toMutableSet().apply { add(possibleObstruction) }.toSet()
            walkThroughLab(startPosition, north, obstructionsToTest, width, height).second
        }.count().toInt()
    }

    private fun parseInput(input: List<String>): Pair<Set<Position>, Position> {
        val obstructions = mutableSetOf<Position>()
        var startPosition: Position? = null

        input.forEachIndexed { row, line ->
            line.forEachIndexed { col, char ->
                when (char) {
                    '#' -> obstructions += Position(col, row)
                    '^' -> startPosition = Position(col, row)
                }
            }
        }

        if (startPosition == null)
            error("No startPosition found.")

        return obstructions to startPosition
    }

    private data class Position(val x: Int, val y: Int)
    private data class Direction(val x: Int, val y: Int)

    private val north = Direction(0, -1)
    private val east = Direction(1, 0)
    private val south = Direction(0, 1)
    private val west = Direction(-1, 0)

    private fun getNextPosition(currentPosition: Position, currentDirection: Direction) =
        Position(currentPosition.x + currentDirection.x, currentPosition.y + currentDirection.y)

    private fun getNextDirection(currentDirection: Direction): Direction {
        return when (currentDirection) {
            north -> east
            east -> south
            south -> west
            west -> north
            else -> error("Unknown direction.")
        }
    }

    private fun isInBounds(position: Position, width: Int, height: Int): Boolean {
        return position.x in 0..<width && position.y in 0..<height
    }

    private fun walkThroughLab(
        guardStartPosition: Position,
        guardStartDirection: Direction,
        obstructions: Set<Position>,
        width: Int,
        height: Int
    ): Pair<Collection<Position>, Boolean> {
        var guardPosition = guardStartPosition
        var guardDirection = guardStartDirection
        var placesVisited = mutableSetOf<Pair<Position, Direction>>()

        while (isInBounds(guardPosition, width, height)) {
            placesVisited += guardPosition to guardDirection

            var possibleNextPosition = getNextPosition(guardPosition, guardDirection)
            while (possibleNextPosition in obstructions) {
                guardDirection = getNextDirection(guardDirection)
                possibleNextPosition = getNextPosition(guardPosition, guardDirection)
            }
            guardPosition = possibleNextPosition

            if (guardPosition to guardDirection in placesVisited) {
                return placesVisited.map { it.first }.distinct() to true
            }
        }

        return placesVisited.map { it.first }.distinct() to false
    }

}
