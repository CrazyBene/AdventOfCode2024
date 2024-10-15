fun readInputFromResources(fileName: String): List<String>? {
    val fileUrl = object {}.javaClass.getResource(fileName)
    return fileUrl?.readText()?.split("\n")
}