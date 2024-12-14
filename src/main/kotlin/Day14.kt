fun main() = Day14.run(RunMode.BOTH)

object Day14 : BasicDay() {

    override val expectedTestValuePart1 = 12

    private val robotRegex = """p=(-?\d+),(-?\d+) v=(-?\d+),(-?\d+)""".toRegex()

    override val solvePart1: ((List<String>, Boolean) -> Int) = { input, isTest ->
        val width = if (isTest) 11 else 101
        val height = if (isTest) 7 else 103
        var robots = parseInput(input)

        robots = robots.map { robot ->
            moveRobot(robot, width, height, 100)
        }

        calculateSafetyFactor(robots, width, height)
    }

    override val solvePart2: ((List<String>, Boolean) -> Int) = { input, isTest ->
        val width = if (isTest) 11 else 101
        val height = if (isTest) 7 else 103
        var robots = parseInput(input)

        val startConfiguration = robots.toList()

        var moves = 0
        var minSafetyFactor = calculateSafetyFactor(robots, width, height)
        var movesMinSafetyFactor = 0

        do {
            robots = robots.map { robot ->
                moveRobot(robot, width, height)
            }
            moves++

            val safetyFactor = calculateSafetyFactor(robots, width, height)
            if (safetyFactor < minSafetyFactor) {
                minSafetyFactor = safetyFactor
                movesMinSafetyFactor = moves
            }
        } while (robots != startConfiguration)

        movesMinSafetyFactor
    }

    val solvePart2Visually: ((List<String>, Boolean) -> Int) = { input, isTest ->
        val width = if (isTest) 11 else 101
        val height = if (isTest) 7 else 103
        var robots = parseInput(input)

        var moves = 0
        var minSafetyFactor = calculateSafetyFactor(robots, width, height)

        outer@ while (true) {
            input@ while (true) {
                val input = readln()

                when (input) {
                    "q" -> break@outer
                    "n" -> break@input
                    else -> println("Unknown command, enter 'n' for next lowest safety score image or 'q' to quit")
                }
            }

            println("Move robots...")

            while (true) {
                robots = robots.map { robot ->
                    moveRobot(robot, width, height, 100)
                }
                moves++

                val safetyFactor = calculateSafetyFactor(robots, width, height)
                if (safetyFactor < minSafetyFactor) {
                    minSafetyFactor = safetyFactor
                    println("-".repeat(width))
                    printFloor(robots, width, height)
                    println(moves)
                    println("-".repeat(width))
                    break
                }
            }
        }


        moves
    }

    private fun parseInput(input: List<String>) = input.map { line ->
        robotRegex.find(line).let { result ->
            if (result == null) error("Something went wrong")

            Robot(
                Vector2(result.groupValues[1].toInt(), result.groupValues[2].toInt()),
                Vector2(result.groupValues[3].toInt(), result.groupValues[4].toInt())
            )
        }
    }

    private fun moveRobot(robot: Robot, maxX: Int, maxY: Int, seconds: Int = 1): Robot {
        var newPosition = robot.position + robot.velocity * seconds
        newPosition = Vector2(newPosition.x.mod(maxX), newPosition.y.mod(maxY))
        return Robot(newPosition, robot.velocity)
    }

    private fun calculateSafetyFactor(robots: List<Robot>, width: Int, height: Int): Int {
        var topLeftCount = 0
        var topRightCount = 0
        var botLeftCount = 0
        var botRightCount = 0
        robots.forEach { robot ->
            if (robot.position.x == width / 2 || robot.position.y == height / 2)
                return@forEach

            when ((robot.position.x < width / 2) to (robot.position.y < height / 2)) {
                true to true -> topLeftCount++
                false to true -> topRightCount++
                true to false -> botLeftCount++
                false to false -> botRightCount++
            }
        }

        return topLeftCount * topRightCount * botLeftCount * botRightCount
    }

    private fun printFloor(robots: List<Robot>, width: Int, height: Int) {
        (0..<height).forEach { y ->
            (0..<width).forEach { x ->
                print(if (robots.any { it.position == Vector2(x, y) }) "x" else " ")
            }
            println()
        }
    }

    private data class Vector2(val x: Int, val y: Int) {
        operator fun plus(other: Vector2) = Vector2(this.x + other.x, this.y + other.y)
        operator fun times(scalar: Int) = Vector2(this.x * scalar, this.y * scalar)
    }

    private data class Robot(var position: Vector2, val velocity: Vector2)

}
