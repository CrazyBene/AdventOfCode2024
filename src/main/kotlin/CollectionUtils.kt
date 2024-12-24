/**
 * Generates all combinations of the elements of tzhe given list for the requested size.
 * Note: Combinations do not include all their permutations!
 * Note2: Stolen from the official koltin aoc stream day 7, from Olaf Gottschalk (@coder_ogo)
 * @receiver The list to take elements from
 * @param size The size of the combinations to create
 * @return A sequence of all combinations
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

fun <T> Set<T>.combinations(size: Int): Sequence<List<T>> = this.toList().combinations(size)

/**
 * Same functionality as eachCount, but returns a map with the count value of type Long
 * @receiver The grouping to count
 * @return A map with the number of each element as value
 */
fun <T, K> Grouping<T, K>.eachCountLong(): Map<K, Long> {
    return this.eachCount().map { it.key to it.value.toLong() }.toMap()
}

/**
 * Flattens a list of maps which all represent some kind of counting
 * @receiver The list of maps, which should be combines/flatten
 * @return A map with the combine count of each element
 */
fun <T> List<Map<T, Long>>.flatten() = this.fold(mutableMapOf<T, Long>()) { acc, entries ->
    entries.forEach { entry ->
        acc[entry.key] = acc.getOrElse(entry.key) { 0L } + entry.value
    }
    acc
}.toMap()