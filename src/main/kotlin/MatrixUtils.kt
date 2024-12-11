/**
 * Transpose a matrix (list of list)
 * @receiver The matrix (list of list) to be transposed
 * @return The transpose matrix (list of list)
 */
inline fun <reified T> List<List<T>>.transpose(): List<List<T>> {
    val cols = this[0].size
    val rows = this.size
    return List(cols) { j ->
        List(rows) { i ->
            this[i][j]
        }
    }
}

/**
 * Roatate a matrix (list of list) 45 degrees
 * Note: This will not return a rectangular matrix, but instead
 * stretch it into y direction and shrink it in x direction
 * @receiver The matrix (list of list) to be rotated
 * @return The rotated matrix (list of list)
 */
inline fun <reified T> List<List<T>>.rotate45Degrees(): List<List<T>> {
    val result = List(this.size + this.first().size) {
        mutableListOf<T>()
    }

    this.forEachIndexed { y, row ->
        row.forEachIndexed { x, value ->
            result[y + x] += value
        }
    }

    return result.map { it.reversed() }
}