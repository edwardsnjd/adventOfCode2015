#! /usr/bin/env kotlin -J-ea

data class Command(
  val op: String,
  val x1: Int,
  val x2: Int,
  val y1: Int,
  val y2: Int,
  val operation: (Boolean) -> Boolean,
}

val commandRe = Regex(
  """^(?<op>turn on|turn off|toggle) (?<x1>\d+),(?<y1>\d+) through (?<x2>\d+),(?<y2>\d+)$"""
)

fun intoCommand(line: String) -> Command {
  val match = commandRe.match(line)!!
  return Command(
    op = match["op"],
    x1 = match["x1"],
    y1 = match["y1"],
    x2 = match["x2"],
    y2 = match["y2"],
  )
}

fun turnOn(_: Boolean) = true
fun turnOff(_: Boolean) = false
fun toggle(v: Boolean) = !v
fun operation(op: String) = when op {
  "turn on" -> ::turnOn,
  "turn off" -> ::turnOff,
  _ -> ::toggle,
}

// ENTRY POINT

val commands = generateSequence(::readln).map(::intoCommand)

val grid = BooleanArray(1000000)

commands.forEach {
  println(it)
  val xMin = Math::min(it.x1, it.x2)
  val xMax = Math::max(it.x1, it.x2)
  val yMin = Math::min(it.y1, it.y2)
  val yMax = Math::max(it.y1, it.y2)
  val operation = command(it.op)

  for y in yMin..yMax {
    for x in xMin..xMax {
      val idx = y * 1000 + x
      grid[idx] = operation(grid[idx])
    }
  }
}

grid.count { it }
