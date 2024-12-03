#! /usr/bin/env kotlin -J-ea

data class Command(
  val op: String,
  val x1: Int,
  val x2: Int,
  val y1: Int,
  val y2: Int,
) {
  companion object {
    private val commandRe = Regex(
      """(turn on|turn off|toggle) (\d+),(\d+) through (\d+),(\d+)"""
    )

    fun parse(line: String): Command {
      val (op, x1, y1, x2, y2) = commandRe.matchEntire(line)!!.destructured
      return Command(
        op = op,
        x1 = x1.toInt(),
        y1 = y1.toInt(),
        x2 = x2.toInt(),
        y2 = y2.toInt(),
      )
    }
  }
}

fun turnOn(v: Int) = v + 1
fun turnOff(v: Int) = kotlin.math.max(v - 1, 0)
fun toggle(v: Int) = v + 2
fun operation(op: String) = when (op) {
  "turn on" -> ::turnOn
  "turn off" -> ::turnOff
  else -> ::toggle
}

// ENTRY POINT

val commands = generateSequence(::readlnOrNull).map(Command::parse)

IntArray(1000000)
  .also { grid ->
    commands.forEach {
      val xMin = kotlin.math.min(it.x1, it.x2)
      val xMax = kotlin.math.max(it.x1, it.x2)
      val yMin = kotlin.math.min(it.y1, it.y2)
      val yMax = kotlin.math.max(it.y1, it.y2)
      val op = operation(it.op)

      for (y in yMin..yMax) {
        for (x in xMin..xMax) {
          val idx = y * 1000 + x
          grid[idx] = op(grid[idx])
        }
      }
    }
  }
  .sum()
