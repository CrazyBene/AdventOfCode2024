fun main() = Day19.run(RunMode.BOTH)

object Day19 : BasicDay() {

    override val expectedTestValuePart1 = 6
    override val expectedTestValuePart2 = 16L

    override val solvePart1: ((List<String>, Boolean) -> Int) = { input, _ ->
        val (availableTowelPatterns, wantedDesigns) = parseInput(input)

        wantedDesigns.filter { wantedDesign ->
            isDesignPossible(wantedDesign, availableTowelPatterns)
        }.count()
    }

    override val solvePart2: ((List<String>, Boolean) -> Long) = { input, _ ->
        val (availableTowelPatterns, wantedDesigns) = parseInput(input)

        wantedDesigns.sumOf { wantedDesign ->
            numberOfPossiblePatternsForDesignMemoized(wantedDesign, availableTowelPatterns)
        }
    }

    private fun parseInput(input: List<String>) = input.first().split(", ") to input.drop(2)

    private fun isDesignPossible(restPattern: String, availableTowelPatterns: List<String>): Boolean {
        if (restPattern == "") return true

        return availableTowelPatterns.any { pattern ->
            if (restPattern.startsWith(pattern))
                isDesignPossible(restPattern.substringAfter(pattern), availableTowelPatterns)
            else false
        }
    }

    private fun numberOfPossiblePatternsForDesignMemoized(
        restPattern: String,
        availableTowelPatterns: List<String>
    ): Long {
        val cache = mutableMapOf<String, Long>()

        fun inner(restPattern: String): Long {
            if (restPattern == "") return 1

            return availableTowelPatterns.sumOf { pattern ->
                if (restPattern.startsWith(pattern))
                    cache.getOrPut(restPattern.substringAfter(pattern)) {
                        inner(restPattern.substringAfter(pattern))
                    }
                else 0
            }
        }

        return inner(restPattern)
    }

}