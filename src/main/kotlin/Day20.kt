import java.util.PriorityQueue
import kotlin.math.abs

fun main() = Day20.run(RunMode.BOTH)

object Day20 : BasicDay() {

    override val expectedTestValuePart1 = 5
    override val expectedTestValuePart2 = 285

    override val solvePart1: ((List<String>, Boolean) -> Int) = { input, isTest ->
        val graph = parseInput(input)

        val (_, previous) = dijkstra(graph.getNodes(), graph.startNode) { currentNode ->
            currentNode == graph.endNode
        }

        val path = backtrackPath(graph.endNode, previous)
        val indexOfPosition = path.withIndex().associate { it.value to it.index }

        calculateTimeSaves(path, indexOfPosition, 2).filter { (if (isTest) 20 else 100) <= it }.count()
    }

    override val solvePart2: ((List<String>, Boolean) -> Int) = { input, isTest ->
        val graph = parseInput(input)

        val (_, previous) = dijkstra(graph.getNodes(), graph.startNode) { currentNode ->
            currentNode == graph.endNode
        }

        val path = backtrackPath(graph.endNode, previous)
        val indexOfPosition = path.withIndex().associate { it.value to it.index }

        calculateTimeSaves(path, indexOfPosition, 20).filter { (if (isTest) 50 else 100) <= it }.count()
    }

    private fun parseInput(input: List<String>): Graph {
        var startPosition: Vector2? = null
        var endPosition: Vector2? = null

        val nodeMap = input.mapIndexed { row, line ->
            line.mapIndexedNotNull { col, char ->
                when (char) {
                    '#' -> null
                    '.' -> Vector2(col, row)
                    'S' -> {
                        startPosition = Vector2(col, row)
                        Vector2(col, row)
                    }

                    'E' -> {
                        endPosition = Vector2(col, row)
                        Vector2(col, row)
                    }

                    else -> error("Could not parse maze. Unknown cell type $char.")
                }
            }.map { it to Node(it) }
        }.flatten().toMap()

        if (startPosition == null) error("Could not parse maze. No start position 'S' found.")
        if (endPosition == null) error("Could not parse maze. No end position 'E' found.")

        nodeMap.values.forEach { node ->
            listOf(
                Vector2(0, 1),
                Vector2(0, -1),
                Vector2(1, 0),
                Vector2(-1, 0)
            ).forEach { direction ->
                val neighborNode = nodeMap[node.position + direction]
                if (neighborNode != null) node.addNeighbor(neighborNode)
            }
        }

        val startNode = nodeMap[startPosition]!!
        val endNode = nodeMap[endPosition]!!

        return Graph(nodeMap, startNode, endNode)
    }

    private fun dijkstra(
        nodes: List<Node>,
        startNode: Node,
        earlyFinishCheck: ((Node) -> Boolean)? = null
    ): Pair<Map<Node, Int>, Map<Node, Node?>> {
        val distances = mutableMapOf(startNode to 0)
        val previous = nodes.associateWith { null as Node? }.toMutableMap()

        val queue = PriorityQueue<Pair<Node, Int>>(compareBy { it.second }).apply { add(startNode to 0) }

        while (queue.isNotEmpty()) {
            val (currentNode, currentDistance) = queue.poll()

            if (earlyFinishCheck?.invoke(currentNode) == true) break

            currentNode.getNeighbors().forEach { neighbor ->
                val newDistance = currentDistance + 1
                val oldDistance = distances.getOrElse(neighbor) { Int.MAX_VALUE }
                if (newDistance < oldDistance) {
                    distances[neighbor] = newDistance
                    previous[neighbor] = currentNode

                    queue.add(neighbor to newDistance)
                }
            }
        }

        return distances to previous
    }

    private fun backtrackPath(node: Node, previous: Map<Node, Node?>): List<Vector2> {
        val list = mutableListOf<Vector2>()

        var currentNode: Node? = node
        while (currentNode != null) {
            list += currentNode.position

            currentNode = previous[currentNode]
        }

        return list
    }

    private fun calculateTimeSaves(path: List<Vector2>, indexOfPosition: Map<Vector2, Int>, maxCheatTime: Int): List<Int> {
        val moveOptions = (-maxCheatTime..maxCheatTime).flatMap { y ->
            (-maxCheatTime..maxCheatTime).mapNotNull { x ->
                val cheatTime = abs(x) + abs(y)
                if (maxCheatTime < cheatTime || cheatTime == 0) null
                else Vector2(x, y) to cheatTime
            }
        }

        return path.flatMapIndexed { index, position ->
            moveOptions.map { it.first + position to it.second }
                .filter { it.first in indexOfPosition }
                .mapNotNull { (otherPosition, cheatTime) ->
                    val otherIndex = indexOfPosition[otherPosition] ?: return@mapNotNull null

                    if (otherIndex - cheatTime <= index) null
                    else otherIndex - index - cheatTime
                }
        }
    }

    private data class Vector2(val x: Int, val y: Int) {
        operator fun plus(other: Vector2) = Vector2(this.x + other.x, this.y + other.y)
    }

    private data class Node(val position: Vector2) {

        private val neighbors = mutableSetOf<Node>()

        fun addNeighbor(neighbor: Node) {
            neighbors.add(neighbor)
        }

        fun getNeighbors() = neighbors.toSet()

    }

    private data class Graph(private val nodeMap: Map<Vector2, Node>, val startNode: Node, val endNode: Node) {

        fun getNodes() = nodeMap.values.toList()

        operator fun get(position: Vector2) = nodeMap[position]

    }

}