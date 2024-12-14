import kotlin.collections.mapIndexed
import kotlin.collections.plusAssign

fun main() = Day09.run(RunMode.BOTH)

object Day09 : BasicDay() {

    override val expectedTestValuePart1 = 1928L
    override val expectedTestValuePart2 = 2858L

    override val solvePart1: ((List<String>, Boolean) -> Long) = { input, _ ->
        var fullInput = input.first().flatMapIndexed { index, char ->
            if (index % 2 == 0)
                List(char.digitToInt()) { index / 2 }
            else
                List(char.digitToInt()) { null }
        }

        var output = mutableListOf<Int>()

        var leftIndex = 0
        var rightIndex = fullInput.size - 1

        while (leftIndex < rightIndex + 1) {
            if (fullInput[leftIndex] != null) {
                output += fullInput[leftIndex]!!
                leftIndex++
                continue
            }
            if (fullInput[rightIndex] == null) {
                rightIndex--
                continue
            }

            output += fullInput[rightIndex]!!
            rightIndex--
            leftIndex++
        }

        output.calculateChecksum()
    }

    override val solvePart2: ((List<String>, Boolean) -> Long) = { input, _ ->
        var blocks = input.first().mapIndexed { index, char ->
            if (index % 2 == 0) Block(char.digitToInt(), index / 2)
            else Block(char.digitToInt(), null)
        }

        var outputBlocks = blocks.toMutableList()
        var blockIndex = outputBlocks.size - 1

        val alreadyMovedBlocks = mutableSetOf<Block>()

        while (blockIndex > 0) {
            var block = outputBlocks[blockIndex]

            if (block.id == null || block in alreadyMovedBlocks) {
                blockIndex--
                continue
            }

            val freeBlocks = outputBlocks.subList(0, blockIndex).withIndex().filter { it.value.id == null }
            for (freeBlock in freeBlocks) {
                if (block.size <= freeBlock.value.size) {
                    outputBlocks[blockIndex] = Block(block.size, null)

                    outputBlocks[freeBlock.index] = block
                    if (freeBlock.value.size - block.size > 0)
                        outputBlocks.add(freeBlock.index + 1, Block(freeBlock.value.size - block.size, null))
                    break
                }
            }

            blockIndex--
            alreadyMovedBlocks += block
        }

        outputBlocks.flatMap { block ->
            List(block.size) { block.id }
        }.calculateChecksum()
    }

    private data class Block(val size: Int, val id: Int?)

    private fun List<Int?>.calculateChecksum() = this.mapIndexed { index, number ->
        if (number == null) 0
        else (index * number).toLong()
    }.sum()

    private fun List<Block>.print() {
        this.flatMap { block ->
            if (block.id == null)
                List(block.size) { '.' }
            else
                List(block.size) { block.id.toString() }
        }.joinToString("").let { println(it) }
    }

}
