import java.util.PriorityQueue

fun main() = Day16.run(RunMode.BOTH)

object Day16 : BasicDay() {

    override val expectedTestValuePart1 = 7036
    override val expectedTestValuePart2 = 45

    override val solvePart1: ((List<String>, Boolean) -> Int) = { input, _ ->
        val (nodes, startNode, endNodes) = parseInput(input)

        val (distances, _) = dijkstra(nodes, startNode) { currentNode ->
            currentNode in endNodes
        }

        endNodes.minOf { distances[it]!! }
    }

    override val solvePart2: ((List<String>, Boolean) -> Int) = { input, _ ->
        val (nodes, startNode, endNodes) = parseInput(input)

        val (distances, previous) = dijkstra(nodes, startNode)

        val shortestDistance = endNodes.minOfOrNull<Node, Int> { distances.getOrElse(it) { Int.MAX_VALUE } }

        endNodes.filter { distances[it] == shortestDistance }.flatMap {
            reversePathList(it, previous)
        }.toSet().count()
    }

    private fun parseInput(input: List<String>): Triple<List<Node>, Node, List<Node>> {
        var startPosition: Vector2? = null
        var endPosition: Vector2? = null

        val nodeMap = input.flatMapIndexed { row, line ->
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
            }
        }.flatMap { position ->
            Direction.entries.map { direction ->
                position to direction to Node(position, direction)
            }
        }.toMap()

        if (startPosition == null) error("Could not parse maze. No start position 'S' found.")
        if (endPosition == null) error("Could not parse maze. No end position 'E' found.")

        nodeMap.values.forEach { node ->
            val forwardNode = nodeMap[node.position + node.direction.value to node.direction]
            if (forwardNode != null)
                node.addNeighbor(forwardNode, 1)

            listOf(
                nodeMap[node.position + node.direction.turnRight().value to node.direction.turnRight()],
                nodeMap[node.position + node.direction.turnLeft().value to node.direction.turnLeft()]
            ).forEach { turnNode ->
                if (turnNode != null)
                    node.addNeighbor(turnNode, 1001)
            }
        }

        val startNode = nodeMap[startPosition to Direction.EAST] ?: error("Could not find start node from position.")
        val endNodes = nodeMap.filter { it.value.position == endPosition }.values.toList()

        return Triple(nodeMap.values.toList(), startNode, endNodes)
    }

    private fun dijkstra(
        nodes: List<Node>,
        startNode: Node,
        earlyFinishCheck: ((Node) -> Boolean)? = null
    ): Pair<Map<Node, Int>, Map<Node, Set<Node>>> {
        val distances = nodes.associateWith { Int.MAX_VALUE }.toMutableMap()
        val previous = nodes.associateWith { mutableSetOf<Node>() }.toMutableMap()
        distances[startNode] = 0

        val queue = PriorityQueue<Pair<Node, Int>>(compareBy { it.second }).apply { add(startNode to 0) }

        while (queue.isNotEmpty()) {
            val (currentNode, currentDistance) = queue.poll()

            if (earlyFinishCheck?.invoke(currentNode) == true) break

            currentNode.getNeighbors().forEach { neighbor, distance ->
                val newDistance = currentDistance + distance
                val oldDistance = distances[neighbor]!!
                when {
                    newDistance < oldDistance -> {
                        distances[neighbor] = newDistance

                        previous[neighbor]!!.clear()
                        previous[neighbor]!!.add(currentNode)

                        queue.add(neighbor to newDistance)
                    }

                    newDistance == oldDistance -> previous[neighbor]!!.add(currentNode)
                }
            }
        }

        return distances to previous
    }

    private fun reversePathList(node: Node, previous: Map<Node, Set<Node>>): Set<Vector2> = previous[node]!!.map { n ->
        reversePathList(n, previous)
    }.flatten().toSet() + node.position

    private data class Vector2(val x: Int, val y: Int) {
        operator fun plus(other: Vector2) = Vector2(this.x + other.x, this.y + other.y)
    }

    private enum class Direction(val value: Vector2) {
        NORTH(Vector2(0, -1)),
        EAST(Vector2(1, 0)),
        SOUTH(Vector2(0, 1)),
        WEST(Vector2(-1, 0));

        fun turnRight() = when (this) {
            NORTH -> EAST
            EAST -> SOUTH
            SOUTH -> WEST
            WEST -> NORTH
        }

        fun turnLeft() = when (this) {
            NORTH -> WEST
            WEST -> SOUTH
            SOUTH -> EAST
            EAST -> NORTH
        }
    }

    private data class Node(val position: Vector2, val direction: Direction) {

        private val neighborsWithDistance = mutableMapOf<Node, Int>()

        fun addNeighbor(neighbor: Node, distance: Int) {
            if (neighbor in neighborsWithDistance)
                error("Node $this already has node $neighbor as neighbor.")

            neighborsWithDistance[neighbor] = distance
        }

        fun getNeighbors() = neighborsWithDistance.toMap()

    }

}
