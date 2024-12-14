import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.default.linalg.DefaultLinAlgEx.solve
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import kotlin.math.abs
import kotlin.math.round

fun main() = Day13.run(RunMode.BOTH)

object Day13 : BasicDay() {

    override val expectedTestValuePart1 = 480L
    override val expectedTestValuePart2 = 875318608908L

    private val buttonRegex = """Button [AB]: X\+(\d+), Y\+(\d+)""".toRegex()
    private val priceLocationRegex = """Prize: X=(\d+), Y=(\d+)""".toRegex()

    override val solvePart1: ((List<String>, Boolean) -> Long) = { input, _ ->
        val clawMachines = parseInput(input)

        clawMachines.sumOf { clawMachine ->
            val solution = solveClawMachine(clawMachine)

            val (aButtonPresses, bButtonPresses) = calculateButtonPresses(solution)

            aButtonPresses * clawMachine.buttonA.tokenCost + bButtonPresses * clawMachine.buttonB.tokenCost
        }
    }

    override val solvePart2: ((List<String>, Boolean) -> Long) = { input, _ ->
        val clawMachines = parseInput(input, 10000000000000)

        clawMachines.sumOf { clawMachine ->
            val solution = solveClawMachine(clawMachine)

            val (aButtonPresses, bButtonPresses) = calculateButtonPresses(solution)

            aButtonPresses * clawMachine.buttonA.tokenCost + bButtonPresses * clawMachine.buttonB.tokenCost
        }
    }

    private fun parseInput(input: List<String>, part2Extra: Long = 0) = input.windowed(3, 4).map {
        val buttonA = buttonRegex.find(it[0]).let { result ->
            if (result == null) error("Could not parse Button A.")

            Button(3, Vector2(result.groupValues[1].toLong(), result.groupValues[2].toLong()))
        }
        val buttonB = buttonRegex.find(it[1]).let { result ->
            if (result == null) error("Could not parse Button B.")

            Button(1, Vector2(result.groupValues[1].toLong(), result.groupValues[2].toLong()))
        }
        val priceLocation = priceLocationRegex.find(it[2]).let { result ->
            if (result == null) error("Could not parse price location.")

            Vector2(part2Extra + result.groupValues[1].toLong(), part2Extra + result.groupValues[2].toLong())
        }
        ClawMachine(buttonA, buttonB, priceLocation)
    }

    private fun solveClawMachine(clawMachine: ClawMachine): NDArray<Double, D1> {
        val vectorMatrix =
            mk.ndarray(mk[mk[clawMachine.buttonA.move.x, clawMachine.buttonB.move.x], mk[clawMachine.buttonA.move.y, clawMachine.buttonB.move.y]])
        val solutionMatrix = mk.ndarray(mk[clawMachine.prizeLocation.x, clawMachine.prizeLocation.y])

        return solve(vectorMatrix, solutionMatrix)
    }

    private fun calculateButtonPresses(solution: NDArray<Double, D1>): Pair<Long, Long> {
        val aButtonPressesDouble = solution[0]
        val bButtonPressesDouble = solution[1]

        if (!isInt(aButtonPressesDouble) || !isInt(bButtonPressesDouble)) {
            return 0 to 0
        }

        val aButtonPresses = round(aButtonPressesDouble).toLong()
        val bButtonPresses = round(bButtonPressesDouble).toLong()

        if (aButtonPresses < 0 || bButtonPresses < 0) {
            return 0 to 0
        }

        return aButtonPresses to bButtonPresses
    }

    private fun isInt(double: Double, uncertainty: Double = 0.0001) =
        abs(double - round(double).toLong()) < uncertainty

    private data class Vector2(val x: Long, val y: Long)

    private data class Button(val tokenCost: Long, val move: Vector2)

    private data class ClawMachine(val buttonA: Button, val buttonB: Button, val prizeLocation: Vector2)

}
