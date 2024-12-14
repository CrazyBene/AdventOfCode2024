import kotlin.collections.groupingBy

fun main() = Day11.run(RunMode.BOTH)

object Day11 : BasicDay() {

    override val expectedTestValuePart1 = 55312

    override val solvePart1: ((List<String>, Boolean) -> Int) = { input, _ ->
        var stones = input.first().split(' ').map { it.toLong() }

        repeat(25) { stones = blinkList(stones) }

        stones.count()
    }

    override val solvePart2: ((List<String>, Boolean) -> Long) = { input, _ ->
        var stoneGroups = parseToGroups(input)

        repeat(75) { stoneGroups = blinkGroups(stoneGroups) }

        stoneGroups.map { it.value }.sum()
    }

    private fun parseToGroups(input: List<String>) =
        input.first().split(' ').map { it.toLong() }.groupingBy { it }.eachCountLong()

    private fun blinkSingle(stone: Long): List<Long> = when {
        stone == 0L -> listOf(1)
        stone.toString().length % 2 == 0 -> stone.toString().splitAtIndex(stone.toString().length / 2)
            .let { listOf(it.first.toLong(), it.second.toLong()) }

        else -> listOf(stone * 2024)
    }

    private fun blinkGroup(stoneGroup: Map.Entry<Long, Long>) =
        blinkSingle(stoneGroup.key).groupingBy { it }.eachCount().map { it.key to it.value * stoneGroup.value }.toMap()

    private fun blinkList(stones: List<Long>): List<Long> = stones.flatMap { stone -> blinkSingle(stone) }

    private fun blinkGroups(stoneGroups: Map<Long, Long>) = stoneGroups.map { stoneGroup ->
        blinkGroup(stoneGroup)
    }.flatten()

}
