inline fun <reified T> List<List<T>>.transpose(): List<List<T>> {
    val cols = this[0].size
    val rows = this.size
    return List(cols) { j ->
        List(rows) { i ->
            this[i][j]
        }
    }
}


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