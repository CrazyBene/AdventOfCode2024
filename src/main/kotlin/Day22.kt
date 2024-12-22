fun main() = Day22.run(RunMode.BOTH)

object Day22 : BasicDay(separateTestFiles = true) {

    override val expectedTestValuePart1 = 37327623L
    override val expectedTestValuePart2 = 23L

    override val solvePart1: ((List<String>, Boolean) -> Long) = { input, _ ->
        val startNumbers = input.map { it.toLong() }

        startNumbers.map { startNumber ->
            generateSequence(startNumber) {
                nextPRN(it)
            }.take(2000 + 1).last()
        }.sum()
    }

    override val solvePart2: ((List<String>, Boolean) -> Long) = { input, _ ->
        val startNumbers = input.map { it.toLong() }

        startNumbers.map { startNumber ->
            generateSequence(startNumber) {
                nextPRN(it)
            }.take(2000 + 1)
                .toList()
                .map { it % 10 }
                .zipWithNext()
                .map { it.second to it.second - it.first }
                .windowed(4)
                .reversed()
                .map {
                    it.map { it.second } to it.last().first
                }.toMap()
        }.fold(mutableMapOf<List<Long>, Long>()) { acc, cur ->
            cur.forEach { (key, value) ->
                acc.merge(key, value) { a, b -> b + a }
            }
            acc
        }.maxOf { it.value }
    }

    private infix fun Long.mix(number: Long) = this xor number
    private fun Long.prune() = this.mod(16777216).toLong()

    fun nextPRN(currentPRN: Long): Long {
        return currentPRN.let {
            (it mix (it shl 6)).prune()
        }.let {
            (it mix (it shr 5)).prune()
        }.let {
            (it mix (it shl 11)).prune()
        }
    }

}