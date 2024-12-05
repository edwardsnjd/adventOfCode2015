#! /usr/bin/env kotlin -J-ea

val lineRe = Regex("""(.+) to (.+) = (\d+)""")
fun parse(line: String) =
  lineRe.matchEntire(line)!!.destructured
    .let { (from, to, dist) -> Entry(from, to, dist.toInt()) }

data class Entry(val from: String, val to: String, val dist: Int)
fun Graph.add(entry: Entry) = add(entry.from, entry.to, entry.dist)

class Graph {
  val nodes: MutableSet<String> = mutableSetOf()
  val adjacency: MutableMap<String, MutableMap<String, Int>> = mutableMapOf()

  fun distance(from: String, to: String) = adjacency.get(from)?.get(to)

  fun add(from: String, to: String, dist: Int) = apply {
    nodes.add(from)
    nodes.add(to)
    adjacency.getOrPut(from, { mutableMapOf() }).put(to, dist)
    adjacency.getOrPut(to, { mutableMapOf() }).put(from, dist)
  }
}

private fun <E> permutations(source: List<E>): List<List<E>> =
  if (source.count() == 1) listOf(source)
  else source.flatMapIndexed { ind, first ->
    val rest = source.take(ind) + source.drop(ind+1)
    permutations(rest).map {
      buildList {
        add(first)
        addAll(it)
      }
    }
  }


generateSequence(::readlnOrNull)
  .map(::parse)
  .fold(Graph(), Graph::add)
  .let { graph ->
    permutations(graph.nodes.toList())
      .map { it.windowed(2).map { (f, t) -> graph.distance(f, t)!! }.sum() }
      .max()
  }
