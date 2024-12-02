#! /usr/bin/env kotlin -J-ea

val r1 = Regex("""(..).*\1""")
val r2 = Regex("""(.).\1""")

generateSequence(::readLine)
  .filter { r1.containsMatchIn(it) }
  .filter { r2.containsMatchIn(it) }
  .count()
