fun main() = Day15.run(RunMode.BOTH)

object Day15 : BasicDay() {

    override val expectedTestValuePart1 = 10092
    override val expectedTestValuePart2 = 9021

    override val solvePart1: ((List<String>, Boolean) -> Int) = { input, _ ->
        val warehouseMap = WarehouseMap(input.takeWhile { it.isNotEmpty() })
        val moves = parseMoves(input.takeLastWhile { it.isNotBlank() })

        moves.forEach { move ->
            warehouseMap.tryMoveRobot(move)
        }

        warehouseMap.calculateGPS()
    }

    override val solvePart2: ((List<String>, Boolean) -> Int) = { input, _ ->
        val warehouseMap = WideWarehouseMap(input.takeWhile { it.isNotEmpty() })
        val moves = parseMoves(input.takeLastWhile { it.isNotBlank() })

        moves.forEach { move ->
            warehouseMap.tryMoveRobot(move)
        }

        warehouseMap.calculateGPS()
    }

    private fun parseMoves(input: List<String>) = input.joinToString("").map { char ->
        when (char) {
            '^' -> Move.UP
            '>' -> Move.RIGHT
            'v' -> Move.DOWN
            '<' -> Move.LEFT
            else -> error("Could not parse move command $char.")
        }
    }

    private data class Vector2(val x: Int, val y: Int) {
        operator fun plus(other: Vector2) = Vector2(this.x + other.x, this.y + other.y)
    }

    private enum class MapObject {
        WALL,
        BOX
    }

    private class WarehouseMap(input: List<String>) {
        val width = input.first().length
        val height = input.size

        val map: MutableMap<Vector2, MapObject>
        var robotPosition: Vector2 = Vector2(0, 0)

        init {
            map = input.flatMapIndexed { row, line ->
                line.mapIndexedNotNull { col, char ->
                    when (char) {
                        '#' -> Vector2(col, row) to MapObject.WALL
                        'O' -> Vector2(col, row) to MapObject.BOX
                        '@' -> {
                            robotPosition = Vector2(col, row)
                            null
                        }

                        '.' -> null
                        else -> error("Could not parse map object $char.")
                    }
                }
            }.toMap().toMutableMap()
        }

        fun tryMoveRobot(move: Move) {
            val newPosition = robotPosition + move.direction
            when (map[newPosition]) {
                MapObject.WALL -> return
                MapObject.BOX -> if (!tryMoveBox(newPosition, move)) return
                else -> {}
            }
            robotPosition = newPosition
        }

        private fun tryMoveBox(from: Vector2, move: Move): Boolean {
            val newPosition = from + move.direction
            when (map[newPosition]) {
                MapObject.WALL -> return false
                MapObject.BOX -> if (!tryMoveBox(newPosition, move)) return false
                else -> {}
            }

            map.remove(from)
            map[newPosition] = MapObject.BOX
            return true
        }

        fun calculateGPS(): Int {
            return map.filter { it.value == MapObject.BOX }.map { it.key.x + it.key.y * 100 }.sum()
        }

        fun print() {
            (0..<height).forEach { row ->
                (0..<width).forEach { col ->
                    val pos = Vector2(col, row)
                    if (pos == robotPosition)
                        print("@")
                    else when (map[pos]) {
                        MapObject.WALL -> print("#")
                        MapObject.BOX -> print("O")
                        null -> print(".")
                    }
                }
                println()
            }
        }
    }

    private class WideWarehouseMap(input: List<String>) {
        val width = input.first().length * 2
        val height = input.size

        val map: MutableMap<Vector2, MapObject>
        var robotPosition: Vector2 = Vector2(0, 0)

        init {
            map = input.flatMapIndexed { row, line ->
                line.mapIndexedNotNull { col, char ->
                    when (char) {
                        '#' -> Vector2(col * 2, row) to MapObject.WALL
                        'O' -> Vector2(col * 2, row) to MapObject.BOX
                        '@' -> {
                            robotPosition = Vector2(col * 2, row)
                            null
                        }

                        '.' -> null
                        else -> error("Could not parse map object $char.")
                    }
                }
            }.toMap().toMutableMap()
        }

        fun tryMoveRobot(move: Move) {
            val newPosition = robotPosition + move.direction
            if (!canMove(newPosition, move) || !canMove(newPosition + Vector2(-1, 0), move))
                return

            move(newPosition, move)
            move(newPosition + Vector2(-1, 0), move)
            robotPosition = newPosition
        }

        private fun canMove(from: Vector2, move: Move): Boolean {
            if (map[from] == null)
                return true

            if (map[from] == MapObject.WALL)
                return false

            val newPosition = from + move.direction
            if (map[newPosition] == MapObject.WALL
                || map[newPosition + Vector2(-1, 0)] == MapObject.WALL
                || map[newPosition + Vector2(1, 0)] == MapObject.WALL
            )
                return false

            return when (move) {
                Move.UP, Move.DOWN -> {
                    canMove(newPosition + Vector2(-1, 0), move)
                            && canMove(newPosition, move)
                            && canMove(newPosition + Vector2(1, 0), move)
                }

                Move.RIGHT -> {
                    canMove(newPosition + Vector2(1, 0), move)
                }

                Move.LEFT -> {
                    canMove(newPosition + Vector2(-1, 0), move)
                }
            }
        }

        private fun move(from: Vector2, move: Move) {
            if (map[from] == null)
                return

            if (map[from] == MapObject.WALL)
                error("We should never try to move a wall.")

            val newPosition = from + move.direction

            when (move) {
                Move.UP, Move.DOWN -> {
                    move(newPosition + Vector2(-1, 0), move)
                    move(newPosition, move)
                    move(newPosition + Vector2(1, 0), move)
                }

                Move.RIGHT -> {
                    move(newPosition + Vector2(1, 0), move)
                }

                Move.LEFT -> {
                    move(newPosition + Vector2(-1, 0), move)
                }
            }

            map.remove(from)
            map[newPosition] = MapObject.BOX
        }

        fun calculateGPS(): Int {
            return map.filter { it.value == MapObject.BOX }.map { it.key.x + it.key.y * 100 }.sum()
        }

        fun print() {
            (0..<height).forEach { row ->
                (0..<width).forEach { col ->
                    val pos = Vector2(col, row)
                    if (pos == robotPosition)
                        print("@")
                    else if (map[pos + Vector2(-1, 0)] != null)
                        return@forEach
                    else when (map[pos]) {
                        MapObject.WALL -> print("##")
                        MapObject.BOX -> print("[]")
                        null -> print(".")
                    }
                }
                println()
            }
        }

    }

    private enum class Move(val direction: Vector2) {
        UP(Vector2(0, -1)),
        RIGHT(Vector2(1, 0)),
        DOWN(Vector2(0, 1)),
        LEFT(Vector2(-1, 0))
    }

}
