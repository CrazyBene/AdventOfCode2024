import java.util.PriorityQueue

fun main() = Day18.run(RunMode.BOTH)

object Day18 : BasicDay() {

    override val expectedTestValuePart1 = 22
    override val expectedTestValuePart2 = "6,1"

    override val solvePart1: ((List<String>, Boolean) -> Int) = { input, isTest ->
        val width = if (isTest) 7 else 71
        val height = if (isTest) 7 else 71

        val graph = createGraph(width, height)
        val startNode = graph[Vector2(0, 0)] ?: error("Could not find start node at 0, 0.")
        val endNode =
            graph[Vector2(width - 1, height - 1)] ?: error("Could not find end node at ${width - 1}, ${height - 1}.")

        val corruptedPositions = parseInput(input)
        corruptedPositions.take(if (isTest) 12 else 1024).forEach { corruptedPosition ->
            graph.removePosition(corruptedPosition)
        }

        val (distances, _) = dijkstra(graph.getNodes(), startNode) { currentNode ->
            currentNode == endNode
        }

        distances[endNode]!!
    }

    override val solvePart2: ((List<String>, Boolean) -> String) = { input, isTest ->
        val width = if (isTest) 7 else 71
        val height = if (isTest) 7 else 71

        val graph = createGraph(width, height)
        val startNode = graph[Vector2(0, 0)] ?: error("Could not find start node at 0, 0.")
        val endNode =
            graph[Vector2(width - 1, height - 1)] ?: error("Could not find end node at ${width - 1}, ${height - 1}.")

        val corruptedPositions = parseInput(input)

        val (_, previous) = dijkstra(graph.getNodes(), startNode) { currentNode ->
            currentNode == endNode
        }

        var currentPath = getPath(endNode, previous)

        corruptedPositions.firstNotNullOfOrNull { corruptedPosition ->
            graph.removePosition(corruptedPosition)

            if (corruptedPosition !in currentPath) return@firstNotNullOfOrNull null

            val (_, previous) = dijkstra(graph.getNodes(), startNode) { currentNode ->
                currentNode == endNode
            }

            if (previous[endNode] == null) return@firstNotNullOfOrNull corruptedPosition
            currentPath = getPath(endNode, previous)

            null
        }?.let { "${it.x},${it.y}" }
            ?: error("Could not find a solution, even with all corrupted blocks, the finish could still be reached.")
    }

    private fun parseInput(input: List<String>) = input.map { line ->
        line.split(',').let {
            Vector2(it[0].toInt(), it[1].toInt())
        }
    }

    private fun createGraph(width: Int, height: Int): Graph {
        val nodeMap = List(height) { row ->
            List(width) { col ->
                Vector2(col, row) to Node(Vector2(col, row))
            }
        }.flatten().toMap()

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

        return Graph(nodeMap.toMutableMap())
    }

    private fun dijkstra(
        nodes: List<Node>,
        startNode: Node,
        earlyFinishCheck: ((Node) -> Boolean)? = null
    ): Pair<Map<Node, Int>, Map<Node, Node?>> {
        val distances = nodes.associateWith { Int.MAX_VALUE }.toMutableMap()
        val previous = nodes.associateWith { null as Node? }.toMutableMap()
        distances[startNode] = 0

        val queue = PriorityQueue<Pair<Node, Int>>(compareBy { it.second }).apply { add(startNode to 0) }

        while (queue.isNotEmpty()) {
            val (currentNode, currentDistance) = queue.poll()

            if (earlyFinishCheck?.invoke(currentNode) == true) break

            currentNode.getNeighbors().forEach { neighbor ->
                val newDistance = currentDistance + 1
                val oldDistance = distances[neighbor]!!
                if (newDistance < oldDistance) {
                    distances[neighbor] = newDistance
                    previous[neighbor] = currentNode

                    queue.add(neighbor to newDistance)
                }
            }
        }

        return distances to previous
    }

    private fun getPath(node: Node, previous: Map<Node, Node?>): List<Vector2> {
        if (previous[node] == null)
            return listOf(node.position)

        return getPath(previous[node]!!, previous) + node.position
    }

    private data class Vector2(val x: Int, val y: Int) {
        operator fun plus(other: Vector2) = Vector2(this.x + other.x, this.y + other.y)
    }

    private data class Node(val position: Vector2) {

        private val neighbors = mutableSetOf<Node>()

        fun addNeighbor(neighbor: Node) {
            if (neighbor in neighbors)
                error("Node $this already has node $neighbor as neighbor.")

            neighbors += neighbor
        }

        fun removeNeighbor(neighbor: Node) {
            neighbors -= neighbor
        }

        fun getNeighbors() = neighbors.toSet()

    }

    private data class Graph(private val nodeMap: MutableMap<Vector2, Node>) {

        fun getNodes() = nodeMap.values.toList()

        operator fun get(position: Vector2) = nodeMap[position]

        fun removePosition(position: Vector2) {
            val node = nodeMap[position]
            if (node == null) return

            node.getNeighbors().forEach { neighbor ->
                neighbor.removeNeighbor(node)
            }

            nodeMap.remove(position)
        }

    }

}