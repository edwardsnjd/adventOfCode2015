#! /usr/bin/env kotlin -J-ea

sealed interface Source
data class VALUE(val v: Int): Source
data class WIRE(val label: String): Source
data class NOT(val lhs: Source): Source
data class AND(val lhs: Source, val rhs: Source): Source
data class OR(val lhs: Source, val rhs: Source): Source
data class LSHIFT(val lhs: Source, val n: Int): Source
data class RSHIFT(val lhs: Source, val n: Int): Source

data class Assignment(val source: Source, val dest: String)

object Parsing {
  private val assignmentRe = Regex("""^(.*) -> (\w+)$""")
  fun parseAssignment(line: String) =
    assignmentRe.matchEntire(line)!!.destructured
      .let { (lhs, label) -> Assignment(parseSource(lhs), label) }

  fun parseSource(source: String): Source =
    // Chain of command (make sure patterns are mutually exclusive)
    parseValue(source)
    ?: parseWire(source)
    ?: parseNot(source)
    ?: parseAnd(source)
    ?: parseOr(source)
    ?: parseLshift(source)
    ?: parseRshift(source)
    ?: error("Unknown regex")

  private val valueRe = Regex("""^(\d+)$""")
  fun parseValue(source: String) = valueRe.matchEntire(source)?.destructured
    ?.let { (value) -> VALUE(value.toInt()) }

  private val wireRe = Regex("""^([a-z]+)$""")
  fun parseWire(source: String) = wireRe.matchEntire(source)?.destructured
    ?.let { (label) -> WIRE(label) }

  private val notRe = Regex("""^NOT (.*)$""")
  fun parseNot(source: String) = notRe.matchEntire(source)?.destructured
    ?.let { (notlhs) -> NOT(parseSource(notlhs)) }

  private val andRe = Regex("""^(.*) AND (.*)$""")
  fun parseAnd(source: String) = andRe.matchEntire(source)?.destructured
    ?.let { (andlhs, andrhs) -> AND(parseSource(andlhs), parseSource(andrhs)) }

  private val orRe = Regex("""^(.*) OR (.*)$""")
  fun parseOr(source: String) = orRe.matchEntire(source)?.destructured
    ?.let { (orlhs, orrhs) -> OR(parseSource(orlhs), parseSource(orrhs)) }

  private val lshiftRe = Regex("""^(.*) LSHIFT (.*)$""")
  fun parseLshift(source: String) = lshiftRe.matchEntire(source)?.destructured
    ?.let { (lshiftlhs, lshiftn) -> LSHIFT(parseSource(lshiftlhs), lshiftn.toInt()) }

  private val rshiftRe = Regex("""^(.*) RSHIFT (.*)$""")
  fun parseRshift(source: String) = rshiftRe.matchEntire(source)?.destructured
    ?.let { (rshiftlhs, rshiftn) -> RSHIFT(parseSource(rshiftlhs), rshiftn.toInt()) }
}

class Graph {
  var wires: MutableMap<String, Source> = mutableMapOf()
  fun update(asgn: Assignment) = apply { wires.put(asgn.dest, asgn.source) }
}

class Interpreter(val state: Graph) {
  var values: MutableMap<String, Int> = mutableMapOf()

  fun interpret(s: Source): Int = when (s) {
    is VALUE -> s.v
    is WIRE -> {
      if (!values.contains(s.label)) {
        values[s.label] = interpret(state.wires[s.label]!!)
      }
      values[s.label]!!
    }
    is NOT -> interpret(s.lhs).inv()
    is AND -> interpret(s.lhs) and interpret(s.rhs)
    is OR -> interpret(s.lhs) or interpret(s.rhs)
    is LSHIFT -> interpret(s.lhs) shl s.n
    is RSHIFT -> interpret(s.lhs) shr s.n
  }
}

generateSequence(::readlnOrNull)
  .plusElement("3176 -> b")
  .map(Parsing::parseAssignment)
  .fold(Graph()) { state, asgn -> state.update(asgn) }
  .let { Interpreter(it).interpret(WIRE("a")) }
