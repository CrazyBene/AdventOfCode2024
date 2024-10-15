enum class ConsoleColor(val colorASCII: String) {
    RESET("\u001B[0m"),

    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m")
}

fun println(message: String, consoleColor: ConsoleColor) {
    println("${consoleColor.colorASCII}$message${ConsoleColor.RESET.colorASCII}")
}