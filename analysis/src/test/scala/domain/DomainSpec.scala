package domain

import domain.GraphDomain.GraphEdge
import domain.GraphDomain.GraphNode
import domain.GraphDomain.Position
import munit.FunSuite
import munit.ScalaCheckSuite
import org.scalacheck.Prop.forAll

class DomainSpec extends FunSuite with ScalaCheckSuite:
  test("edge from x to y is the same of y to x") {
    forAll {
      (
          id1: Int,
          label1: String,
          color1: Int,
          x1: Double,
          y1: Double,
          z1: Double,
          id2: Int,
          label2: String,
          color2: Int,
          x2: Double,
          y2: Double,
          z2: Double
      ) =>
        val node1 = GraphNode(id1, Position(x1, y1, z1), label1, color1)
        val node2 = GraphNode(id2, Position(x2, y2, z2), label2, color2)
        val edge1 = GraphEdge((node1, node2))
        val edge2 = GraphEdge((node2, node1))
        edge1 == edge2
    }
  }

  test("edges with different nodes is not equal") {
    forAll {
      (
          id1: Int,
          label1: String,
          color1: Int,
          x1: Double,
          y1: Double,
          z1: Double,
          id2: Int,
          label2: String,
          color2: Int,
          x2: Double,
          y2: Double,
          z2: Double,
          id3: Int,
          label3: String,
          color3: Int,
          x3: Double,
          y3: Double,
          z3: Double
      ) =>
        val node1 = GraphNode(id1, Position(x1, y1, z1), label1, color1)
        val node2 = GraphNode(id2, Position(x2, y2, z2), label2, color2)
        val node3 = GraphNode(id3, Position(x3, y3, z3), label3, color3)
        val edge1 = GraphEdge((node1, node2))
        val edge2 = GraphEdge((node2, node3))
        edge1 != edge2
    }
  }
