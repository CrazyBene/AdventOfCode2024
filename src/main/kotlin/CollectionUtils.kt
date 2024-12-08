/**
 * Generates all combinations of the elements of tzhe given list for the requested size.
 * Note: combinations do not include all their permutations!
 * Note2: Stolen from the official koltin aoc stream day 7, from Olaf Gottschalk (@coder_ogo)
 * @receiver the list to take elements from
 * @param size the size of the combinations to create
 * @return a sequence of all combinations
 */
fun <T> List<T>.combinations(size: Int): Sequence<List<T>> =
    when (size) {
        0 -> emptySequence()
        1 -> asSequence().map { listOf(it) }
        else -> sequence {
            this@combinations.forEachIndexed { index, element ->
                val head = listOf(element)
                val tail = this@combinations.subList(index + 1, this@combinations.size)
                tail.combinations(size - 1).forEach { tailCombination ->
                    yield(head + tailCombination)
                }
            }
        }
    }