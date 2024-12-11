/**
 * Splits a text at a specific index
 * @receiver The string to be split
 * @param index The index at what the string should be split (inclusive first part)
 * @return A pair of the spitted strings
 */
fun String.splitAtIndex(index: Int) = require(index in 0..length).let {
    take(index) to substring(index)
}