#! /usr/bin/env kotlin -J-ea

val escape = Regex("""\\\\|\\\"|\\x[0-9a-f]{2}""")
fun findEscapes(line: String) = escape.findAll(line).map { it.value }.toList()

generateSequence(::readlnOrNull).fold(0) { state, line ->
  val escapes = findEscapes(line)
  val chars = line.length - 2 - escapes.map { it.length }.sum() + escapes.count()
  state + line.length - chars
}
