/**
 * Read file from resource directory and return its lines as text
 * @param fileName The name of the file to be read
 * @return A list of strings when the file is found and null, when not
 */
fun readInputFromResources(fileName: String): List<String>? {
    val fileUrl = object {}.javaClass.getResource(fileName)
    return fileUrl?.readText()?.split(System.lineSeparator())
}