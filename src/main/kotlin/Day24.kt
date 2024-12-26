import kotlin.collections.set

fun main() = Day24.run(RunMode.BOTH)

object Day24 : BasicDay(separateTestFiles = true) {

    override val expectedTestValuePart1 = 4L

    private val logicGateRegex = """(.{3}) (AND|XOR|OR) (.{3}) -> (.{3})""".toRegex()

    override val solvePart1: ((List<String>, Boolean) -> Long) = { input, _ ->
        val startWires = parseInputWires(input.takeWhile { it.isNotEmpty() })
        val logicGates = parseInputLogicGates(input.takeLastWhile { it.isNotEmpty() })

        val wires = calculateEverything(startWires, logicGates)

        wires.filter { it.key.startsWith('z') }.toSortedMap().values.reversed().joinToString("").toLong(2)
    }

    override val solvePart2: ((List<String>, Boolean) -> String) = { input, _ ->
        val logicGateMap =
            parseInputLogicGates(input.takeLastWhile { it.isNotEmpty() }).associateBy { it.outputWire }.toMutableMap()

        val swaps = (0..<4).flatMap {
            val baseline = progress(logicGateMap)
            logicGateMap.entries.forEach outer@{ x ->
                logicGateMap.entries.forEach inner@{ y ->
                    if (x == y) return@inner
                    val temp = x.value
                    logicGateMap[x.key] = y.value
                    logicGateMap[y.key] = temp
                    if (progress(logicGateMap) > baseline) return@flatMap listOf(x.key, y.key)
                    val temp2 = x.value
                    logicGateMap[x.key] = y.value
                    logicGateMap[y.key] = temp2
                }
            }
            error("Could not find swap")
        }

        swaps.sorted().joinToString(",")
    }

    private fun parseInputWires(input: List<String>) = input.map { line ->
        line.split(": ").let { it[0] to it[1].toInt() }
    }.toMap()

    private fun parseInputLogicGates(input: List<String>) = input.map { line ->
        logicGateRegex.find(line).let { result ->
            if (result == null) error("Could not parse line $line.")
            val gateType = when (result.groupValues[2]) {
                "AND" -> AND()
                "OR" -> OR()
                "XOR" -> XOR()
                else -> error("Could not parse gate type ${result.groupValues[2]}")
            }
            LogicGate(result.groupValues[1], result.groupValues[3], gateType, result.groupValues[4])
        }
    }

    private fun calculateEverything(startWires: Map<String, Int>, logicGates: List<LogicGate>): Map<String, Int> {
        val wires = startWires.toMutableMap()
        val logicGateQueue = logicGates.toMutableList()

        while (logicGateQueue.isNotEmpty()) {
            val logicGate = logicGateQueue.removeFirst()

            if (logicGate.inputWire1 !in wires || logicGate.inputWire2 !in wires) {
                logicGateQueue.add(logicGate)
                continue
            }

            val input1 = wires[logicGate.inputWire1]!!
            val input2 = wires[logicGate.inputWire2]!!
            val output = logicGate.type(input1, input2)

            wires[logicGate.outputWire] = output
        }
        return wires
    }

    private fun prettyPrint(wire: String, logicGateMap: Map<String, LogicGate>, depth: Int = 0): String {
        if (wire[0] in "xy") return "  ".repeat(depth) + wire
        val logicGate = logicGateMap[wire]!!
        return "${"  ".repeat(depth)}${logicGate.type.javaClass.name} ($wire)\n${
            prettyPrint(
                logicGate.inputWire1, logicGateMap, depth + 1
            )
        }\n${prettyPrint(logicGate.inputWire2, logicGateMap, depth + 1)}"
    }

    private fun padWire(char: Char, num: Int) = "$char${num.toString().padStart(2, '0')}"

    private fun verify(num: Int, logicGateMap: Map<String, LogicGate>): Boolean {
        return verifyZ(padWire('z', num), num, logicGateMap)
    }

    private fun verifyZ(wire: String, num: Int, logicGateMap: Map<String, LogicGate>): Boolean {
//        println("vz $wire $num")
        if (wire !in logicGateMap.keys) return false
        val (input1, input2, type) = logicGateMap[wire]!!
        if (type !is XOR) return false
        if (num == 0) return listOf(input1, input2).sorted() == listOf("x00", "y00")
        return (verifyIntermediateXOR(input1, num, logicGateMap) && verifyCarryBit(
            input2, num, logicGateMap
        )) || (verifyIntermediateXOR(input2, num, logicGateMap) && verifyCarryBit(input1, num, logicGateMap))
    }

    private fun verifyIntermediateXOR(wire: String, num: Int, logicGateMap: Map<String, LogicGate>): Boolean {
//        println("vx $wire $num")
        if (wire !in logicGateMap.keys) return false
        val (input1, input2, type) = logicGateMap[wire]!!
        if (type !is XOR) return false
        return listOf(input1, input2).sorted() == listOf(padWire('x', num), padWire('y', num))
    }

    private fun verifyCarryBit(wire: String, num: Int, logicGateMap: Map<String, LogicGate>): Boolean {
//        println("vc $wire $num")
        if (wire !in logicGateMap.keys) return false
        val (input1, input2, type) = logicGateMap[wire]!!
        if (num == 1) {
            if (type !is AND) return false
            return listOf(input1, input2).sorted() == listOf("x00", "y00")
        }
        if (type !is OR) return false
        return (verifyDirectCarry(input1, num - 1, logicGateMap) && verifyRecarry(
            input2, num - 1, logicGateMap
        )) || (verifyDirectCarry(input2, num - 1, logicGateMap) && verifyRecarry(input1, num - 1, logicGateMap))
    }

    private fun verifyDirectCarry(wire: String, num: Int, logicGateMap: Map<String, LogicGate>): Boolean {
//        println("vd $wire $num")
        if (wire !in logicGateMap.keys) return false
        val (input1, input2, type) = logicGateMap[wire]!!
        if (type !is AND) return false
        return listOf(input1, input2).sorted() == listOf(padWire('x', num), padWire('y', num))
    }

    private fun verifyRecarry(wire: String, num: Int, logicGateMap: Map<String, LogicGate>): Boolean {
//        println("vr $wire $num")
        if (wire !in logicGateMap.keys) return false
        val (input1, input2, type) = logicGateMap[wire]!!
        if (type !is AND) return false
        return (verifyIntermediateXOR(input1, num, logicGateMap) && verifyCarryBit(
            input2,
            num,
            logicGateMap
        )) || (verifyIntermediateXOR(input2, num, logicGateMap) && verifyCarryBit(input1, num, logicGateMap))
    }

    private fun progress(logicGateMap: Map<String, LogicGate>): Int {
        var i = 0
        while (true) {
            if (!verify(i, logicGateMap)) break
            i++
        }
        return i
    }

}

private abstract class GateType() : (Int, Int) -> Int

private class AND() : GateType() {
    override fun invoke(input1: Int, input2: Int) = input1 and input2
}

private class OR() : GateType() {
    override fun invoke(input1: Int, input2: Int) = input1 or input2
}

private class XOR() : GateType() {
    override fun invoke(input1: Int, input2: Int) = input1 xor input2
}

private data class LogicGate(
    val inputWire1: String, val inputWire2: String, val type: GateType, var outputWire: String
)