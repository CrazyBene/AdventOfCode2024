import kotlin.math.abs

fun main() = Day01.run(RunMode.BOTH)

object Day01 : BasicDay() {

    override val expectedTestValuePart1 = 11
    override val expectedTestValuePart2 = 31

    override val solvePart1: ((List<String>) -> Int)? = { input ->
        val (leftList, rightList) = splitIntoLists(input)

        val (leftListSorted, rightListSorted) = leftList.sorted() to rightList.sorted()

        leftListSorted.zip(rightListSorted).map { (leftValue, rightValue) ->
            abs(leftValue - rightValue)
        }.sum()
    }

    override val solvePart2: ((List<String>) -> Int)? = { input ->
        val (leftList, rightList) = splitIntoLists(input)

        leftList.sumOf { number ->
            number * rightList.count { it == number }
        }
    }

}

fun splitIntoLists(input: List<String>): Pair<List<Int>, List<Int>> {
    return input.map { line ->
        line
            .split("\\s+".toRegex())
            .take(2)
            .map { it.toInt() }
            .zipWithNext()
            .first()
    }.fold(mutableListOf<Int>() to mutableListOf<Int>()) { (leftList, rightList), (leftValue, rightValue) ->
        leftList.add(leftValue)
        rightList.add(rightValue)
        leftList to rightList
    }
}