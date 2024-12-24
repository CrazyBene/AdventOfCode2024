fun main() = Day23.run(RunMode.BOTH)

object Day23 : BasicDay() {

    override val expectedTestValuePart1 = 7
    override val expectedTestValuePart2 = "co,de,ka,ta"

    override val solvePart1: ((List<String>, Boolean) -> Int) = { input, _ ->
        val connections = parseInput(input)

        val tripleConnectedComputers = connections.mapNotNull { (computer1, connectedComputers) ->
            connectedComputers.combinations(2).mapNotNull { (computer2, computer3) ->
                if (computer3 !in connections[computer2]!!) null
                else setOf(computer1, computer2, computer3)
            }.toList()
        }.flatten().toSet()

        tripleConnectedComputers.filter {
            it.any { it.startsWith('t') }
        }.count()
    }

    override val solvePart2: ((List<String>, Boolean) -> String) = { input, _ ->
        val connections = parseInput(input)

        val interConnectedComputers = searchConnections(connections)
        interConnectedComputers.maxBy { it.size }.sorted().joinToString(",")
    }

    private fun parseInput(input: List<String>): Map<String, Set<String>> {
        val connections = mutableMapOf<String, Set<String>>()
        input.forEach { line ->
            val (computer1, computer2) = line.split("-")

            connections[computer1] = connections.getOrElse(computer1) { emptySet() } + computer2
            connections[computer2] = connections.getOrElse(computer2) { emptySet() } + computer1
        }
        return connections
    }

    private fun searchConnections(connections: Map<String, Set<String>>): Set<Set<String>> {
        val interConnectedComputers = mutableSetOf<Set<String>>()

        fun inner(currentComputer: String, requiredConnections: Set<String>) {
            if (requiredConnections in interConnectedComputers) return
            interConnectedComputers.add(requiredConnections)

            connections[currentComputer]!!.forEach { neighbor ->
                if (neighbor in requiredConnections) return@forEach
                if (!connections[neighbor]!!.containsAll(requiredConnections)) return@forEach

                inner(neighbor, requiredConnections.toMutableSet().apply { add(neighbor) })
            }
        }

        connections.keys.forEach { inner(it, setOf(it)) }
        return interConnectedComputers
    }

}