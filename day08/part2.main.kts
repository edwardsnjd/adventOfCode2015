#! /usr/bin/env kotlin -J-ea

fun escape(line: String) =
  line.replace("\\", "\\\\").replace("\"", "\\\"").let { "\"$it\"" }

generateSequence(::readlnOrNull).fold(0) { total, line ->
  total + escape(line).length - line.length
}
