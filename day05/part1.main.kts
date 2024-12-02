#! /usr/bin/env kotlin -J-ea

val bannedRe = Regex("""ab|cd|pq|xy""")
val threeVowelsRe = Regex("""[aeiou].*[aeiou].*[aeiou]""")
val repeatedLetterRe = Regex("""(.)\1""")

generateSequence(::readLine)
  .filter { !bannedRe.containsMatchIn(it) }
  .filter { threeVowelsRe.containsMatchIn(it) }
  .filter { repeatedLetterRe.containsMatchIn(it) }
  .count()
