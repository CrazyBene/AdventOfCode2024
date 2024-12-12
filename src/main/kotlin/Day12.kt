import Day12.Position

fun main() = Day12.run(RunMode.PART2)

object Day12 : BasicDay() {

    override val expectedTestValuePart1 = 140
//    override val expectedTestValuePart2 = 1206

    override val solvePart1: ((List<String>) -> Int) = { input ->
        val garden = Garden.parseInput(input)

        val gardenPlots = calculatePlots(garden)

        gardenPlots.sumOf { plot ->
            plot.calculateFencePrice()
        }
    }

//    override val solvePart2: ((List<String>) -> Int) = { input ->
//        val garden = Garden.parseInput(input)
//
//        val gardenPlots = calculatePlots(garden)
//
//        gardenPlots.forEach { plot ->
//            println("...........")
//            println(garden.getOrNull(plot.positions.first()))
//            println(plot.outerCorners())
//            println(plot.innerCorners())
//        }
//
////        gardenPlots.sumOf { plot ->
////            (plot.outerCorners() + plot.innerCorners()) * plot.size
////        }
//        0
//    }

    // this does not look like a good algorithm, but for now its works and is fast enough
    private fun getSameNeighbors(
        position: Position,
        char: Char,
        garden: Garden,
        alreadyInSet: Set<Position>
    ): MutableSet<Position> {
        return listOf(
            position + Position(1, 0),
            position + Position(-1, 0),
            position + Position(0, 1),
            position + Position(0, -1)
        ).fold(mutableSetOf(position)) { acc, neighbor ->
            if (neighbor !in alreadyInSet && garden.getOrNull(neighbor) == char) {
                acc += getSameNeighbors(neighbor, char, garden, alreadyInSet + acc)
            }
            acc
        }
    }

    private fun calculatePlots(garden: Garden): MutableSet<GardenPlot> {
        val gardenPlots = mutableSetOf<GardenPlot>()
        val inNoPlot = List(garden.height) { y ->
            List(garden.width) { x ->
                Position(x, y)
            }
        }.flatten().toMutableList()

        while (inNoPlot.isNotEmpty()) {
            val startPos = inNoPlot.first()
            val plotChar = garden.getOrNull(startPos) ?: error("Something went wrong")

            val plot = GardenPlot(getSameNeighbors(startPos, plotChar, garden, emptySet<Position>()))
            gardenPlots.add(plot)

            plot.positions.forEach { p ->
                inNoPlot.remove(p)
            }
        }

        return gardenPlots
    }

    private data class Position(val x: Int, val y: Int) {
        operator fun plus(other: Position) = Position(this.x + other.x, this.y + other.y)
    }

    private data class Garden(private val map: List<List<Char>>) {

        val width = map.first().size
        val height = map.size

        fun getOrNull(position: Position) = map.getOrNull(position.y)?.getOrNull(position.x)

        companion object {
            fun parseInput(input: List<String>) = Garden(input.map { line -> line.map { it } })
        }

    }

    private data class GardenPlot(val positions: Set<Position>) {

        val size = positions.size

        fun calculatePerimeter(): Int {
            return positions.sumOf { position ->
                listOf(
                    position + Position(1, 0),
                    position + Position(-1, 0),
                    position + Position(0, 1),
                    position + Position(0, -1)
                ).filter { possibleNeighbor ->
                    possibleNeighbor !in positions
                }.count()
            }
        }

        fun calculateFencePrice() = this.size * this.calculatePerimeter()

//        fun outerCorners(): Int {
//            val outsideNeighbors = mutableMapOf<Position, MutableList<Position>>()
//
//            positions.forEach { position ->
//                listOf(
//                    position + Position(1, 0),
//                    position + Position(-1, 0),
//                    position + Position(0, 1),
//                    position + Position(0, -1)
//                ).filter { possibleNeighbor ->
//                    possibleNeighbor !in positions
//                }.forEach { outsideNeighbor ->
//                    if (!outsideNeighbors.containsKey(outsideNeighbor))
//                        outsideNeighbors[outsideNeighbor] = mutableListOf<Position>()
//                    outsideNeighbors[outsideNeighbor]!!.add(position)
//                }
//            }
//
//            val realOutsideN = mutableSetOf<Position>()
//            outsideNeighbors.forEach { outsideNeighbor ->
//                if (listOf(
//                        outsideNeighbor.key + Position(1, 0),
//                        outsideNeighbor.key + Position(0, 1),
//                    ).all { possibleNeighbor ->
//                        possibleNeighbor in outsideNeighbor.value
//                    }
//                ) realOutsideN += outsideNeighbor.key
//
//                if (listOf(
//                        outsideNeighbor.key + Position(0, 1),
//                        outsideNeighbor.key + Position(-1, 0),
//                    ).all { possibleNeighbor ->
//                        possibleNeighbor in outsideNeighbor.value
//                    }
//                ) realOutsideN += outsideNeighbor.key
//
//                if (listOf(
//                        outsideNeighbor.key + Position(-1, 0),
//                        outsideNeighbor.key + Position(0, -1),
//                    ).all { possibleNeighbor ->
//                        possibleNeighbor in outsideNeighbor.value
//                    }
//                ) realOutsideN += outsideNeighbor.key
//
//                if (listOf(
//                        outsideNeighbor.key + Position(0, -1),
//                        outsideNeighbor.key + Position(1, 0),
//                    ).all { possibleNeighbor ->
//                        possibleNeighbor in outsideNeighbor.value
//                    }
//                ) realOutsideN += outsideNeighbor.key
//            }
//
//            return positions.map { position ->
//                var cornerValue = 0
//                if (listOf(
//                        position + Position(1, 0),
//                        position + Position(0, 1),
//                    ).all { possibleNeighbor ->
//                        possibleNeighbor !in positions
//                    } && listOf(
//                        position + Position(1, 0),
//                        position + Position(0, 1),
//                    ).any { possibleNeighbor ->
//                        possibleNeighbor !in realOutsideN
//                    }
//                ) cornerValue++
//
//                if (listOf(
//                        position + Position(0, 1),
//                        position + Position(-1, 0),
//                    ).all { possibleNeighbor ->
//                        possibleNeighbor !in positions
//                    } && listOf(
//                        position + Position(0, 1),
//                        position + Position(-1, 0),
//                    ).any { possibleNeighbor ->
//                        possibleNeighbor !in realOutsideN
//                    }
//                ) cornerValue++
//
//                if (listOf(
//                        position + Position(-1, 0),
//                        position + Position(0, -1),
//                    ).all { possibleNeighbor ->
//                        possibleNeighbor !in positions
//                    } && listOf(
//                        position + Position(-1, 0),
//                        position + Position(0, -1),
//                    ).any { possibleNeighbor ->
//                        possibleNeighbor !in realOutsideN
//                    }
//                ) cornerValue++
//
//                if (listOf(
//                        position + Position(0, -1),
//                        position + Position(1, 0),
//                    ).all { possibleNeighbor ->
//                        possibleNeighbor !in positions
//                    } && listOf(
//                        position + Position(0, -1),
//                        position + Position(1, 0),
//                    ).any { possibleNeighbor ->
//                        possibleNeighbor !in realOutsideN
//                    }
//                ) cornerValue++
//
//                cornerValue
//            }.sum()
//        }
//
//        fun innerCorners(): Int {
//            val outsideNeighbors = mutableMapOf<Position, MutableList<Position>>()
//
//            positions.forEach { position ->
//                listOf(
//                    position + Position(1, 0),
//                    position + Position(-1, 0),
//                    position + Position(0, 1),
//                    position + Position(0, -1)
//                ).filter { possibleNeighbor ->
//                    possibleNeighbor !in positions
//                }.forEach { outsideNeighbor ->
//                    if (!outsideNeighbors.containsKey(outsideNeighbor))
//                        outsideNeighbors[outsideNeighbor] = mutableListOf<Position>()
//                    outsideNeighbors[outsideNeighbor]!!.add(position)
//                }
//            }
//
//            return outsideNeighbors.map { outsideNeighbor ->
//                var cornerValue = 0
//                if (listOf(
//                        outsideNeighbor.key + Position(1, 0),
//                        outsideNeighbor.key + Position(0, 1),
//                    ).all { possibleNeighbor ->
//                        possibleNeighbor in outsideNeighbor.value
//                    }
//                ) cornerValue++
//
//                if (listOf(
//                        outsideNeighbor.key + Position(0, 1),
//                        outsideNeighbor.key + Position(-1, 0),
//                    ).all { possibleNeighbor ->
//                        possibleNeighbor in outsideNeighbor.value
//                    }
//                ) cornerValue++
//
//                if (listOf(
//                        outsideNeighbor.key + Position(-1, 0),
//                        outsideNeighbor.key + Position(0, -1),
//                    ).all { possibleNeighbor ->
//                        possibleNeighbor in outsideNeighbor.value
//                    }
//                ) cornerValue++
//
//                if (listOf(
//                        outsideNeighbor.key + Position(0, -1),
//                        outsideNeighbor.key + Position(1, 0),
//                    ).all { possibleNeighbor ->
//                        possibleNeighbor in outsideNeighbor.value
//                    }
//                ) cornerValue++
//
//                cornerValue
//            }.sum()
//        }

    }

}
