import kotlin.collections.takeWhile
import kotlin.math.floor
import kotlin.math.pow

fun main() = Day17.run(RunMode.BOTH)

object Day17 : BasicDay(separateTestFiles = true) {

    override val expectedTestValuePart1 = "4,6,3,5,6,3,5,2,1,0"
    override val expectedTestValuePart2 = 117440L

    private val registerRegex = """Register [ABC]: (\d+)""".toRegex()

    private val instructions = mapOf(
        0 to ADV(),
        1 to BXL(),
        2 to BST(),
        3 to JNZ(),
        4 to BXC(),
        5 to OUT(),
        6 to BDV(),
        7 to CDV()
    )

    override val solvePart1: ((List<String>, Boolean) -> String) = { input, _ ->
        val (registerA, registerB, registerC) = parseRegisterInput(input.takeWhile { it.isNotEmpty() })
        val program = parseInstructionInput(input.takeLastWhile { it.isNotEmpty() }.first())

        var output = runProgram(program, registerA, registerB, registerC)
        output.joinToString(",")
    }

    override val solvePart2: ((List<String>, Boolean) -> Long) = { input, _ ->
        val program = parseInstructionInput(input.takeLastWhile { it.isNotEmpty() }.first())

        findLastDigitsOfA(program, 0) ?: error("Could find no solution")
    }

    private fun parseRegisterInput(input: List<String>) = input.map { line ->
        registerRegex.find(line)?.groupValues[1]?.toLong() ?: error("Could not parse register string '$line'.")
    }.let {
        listOf(Register('A', it[0]), Register('B', it[1]), Register('C', it[2]))
    }

    private fun parseInstructionInput(input: String) = input.split(": ")[1].split(",").map { it.toInt() }

    private fun Int.comboValue(registerA: Register, registerB: Register, registerC: Register) =
        when (this) {
            in 0..3 -> this.toLong()
            4 -> registerA.value
            5 -> registerB.value
            6 -> registerC.value
            else -> error("Unknown operand $this, something went wrong.")
        }

    private fun runProgram(program: List<Int>, registerA: Register, registerB: Register, registerC: Register): List<Int> {
        var pointer = Pointer(0)
        var output = mutableListOf<Int>()

        while (pointer.value < program.size) {
            val opcode = program[pointer.value]
            val operand = program[pointer.value + 1]

            val instruction = instructions[opcode] ?: error("Unknown operator $opcode, something went wrong.")
            val didPointerMove = instruction(operand, pointer, registerA, registerB, registerC, output)

            if (!didPointerMove) pointer.value += 2
        }

        return output
    }

    private fun findLastDigitsOfA(program: List<Int>, currentA: Long): Long? {
        return (currentA..currentA+8).firstNotNullOfOrNull { a ->
            val registerA = Register('A', a)
            val registerB = Register('B', 0)
            val registerC = Register('C', 0)

            val output = runProgram(program, registerA, registerB, registerC)
            if(output == program.takeLast(output.size)) {
                if(output == program) a.toLong()
                else findLastDigitsOfA(program, maxOf(a shl 3, 8))
            } else {
                null
            }
        }
    }

    private data class Pointer(var value: Int)

    private data class Register(val name: Char, var value: Long)

    private abstract class Instruction() : (Int, Pointer, Register, Register, Register, MutableList<Int>) -> Boolean

    private class ADV() : Instruction() {
        override fun invoke(
            operand: Int,
            pointer: Pointer,
            registerA: Register,
            registerB: Register,
            registerC: Register,
            output: MutableList<Int>
        ): Boolean {
            val operandValue = operand.comboValue(registerA, registerB, registerC)
            registerA.value = floor(registerA.value / 2.0.pow(operandValue.toInt())).toLong()

            return false
        }
    }

    private class BXL() : Instruction() {
        override fun invoke(
            operand: Int,
            pointer: Pointer,
            registerA: Register,
            registerB: Register,
            registerC: Register,
            output: MutableList<Int>
        ): Boolean {
            registerB.value = operand.toLong() xor registerB.value

            return false
        }
    }

    private class BST() : Instruction() {
        override fun invoke(
            operand: Int,
            pointer: Pointer,
            registerA: Register,
            registerB: Register,
            registerC: Register,
            output: MutableList<Int>
        ): Boolean {
            val operandValue = operand.comboValue(registerA, registerB, registerC)
            registerB.value = operandValue % 8

            return false
        }
    }

    private class JNZ() : Instruction() {
        override fun invoke(
            operand: Int,
            pointer: Pointer,
            registerA: Register,
            registerB: Register,
            registerC: Register,
            output: MutableList<Int>
        ): Boolean {
            if (registerA.value == 0L) return false

            pointer.value = operand.toInt()
            return true
        }
    }

    private class BXC() : Instruction() {
        override fun invoke(
            operand: Int,
            pointer: Pointer,
            registerA: Register,
            registerB: Register,
            registerC: Register,
            output: MutableList<Int>
        ): Boolean {
            registerB.value = registerB.value xor registerC.value

            return false
        }
    }

    private class OUT() : Instruction() {
        override fun invoke(
            operand: Int,
            pointer: Pointer,
            registerA: Register,
            registerB: Register,
            registerC: Register,
            output: MutableList<Int>
        ): Boolean {
            val operandValue = operand.comboValue(registerA, registerB, registerC)
            output += operandValue.mod(8)

            return false
        }
    }

    private class BDV() : Instruction() {
        override fun invoke(
            operand: Int,
            pointer: Pointer,
            registerA: Register,
            registerB: Register,
            registerC: Register,
            output: MutableList<Int>
        ): Boolean {
            val operandValue = operand.comboValue(registerA, registerB, registerC)
            registerB.value = floor(registerA.value / 2.0.pow(operandValue.toInt())).toLong()

            return false
        }
    }

    private class CDV() : Instruction() {
        override fun invoke(
            operand: Int,
            pointer: Pointer,
            registerA: Register,
            registerB: Register,
            registerC: Register,
            output: MutableList<Int>
        ): Boolean {
            val operandValue = operand.comboValue(registerA, registerB, registerC)
            registerC.value = floor(registerA.value / 2.0.pow(operandValue.toInt())).toLong()

            return false
        }
    }

}