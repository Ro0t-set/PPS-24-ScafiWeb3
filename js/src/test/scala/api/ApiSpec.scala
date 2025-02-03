package api

import domain.GraphDomain.GraphNode
import domain.GraphDomain.Position
import munit.FunSuite
import munit.ScalaCheckSuite
import org.scalacheck.Gen
import org.scalacheck.Prop
import org.scalacheck.Prop.forAll
import state.GraphState

class ApiSpec extends FunSuite with ScalaCheckSuite:

  def validNodeJsonGen: Gen[(String, GraphNode)] =
    for
      id    <- Gen.choose(1, 1000)
      label <- Gen.alphaStr
      color <- Gen.choose(0x000000, 0xffffff)
      x     <- Gen.choose(-1000.0, 1000.0)
      y     <- Gen.choose(-1000.0, 1000.0)
      z     <- Gen.choose(-1000.0, 1000.0)
    yield (
      s"""[{
      "id": $id,
      "label": "$label",
      "color": $color,
      "position": {
        "x": $x,
        "y": $y,
        "z": $z
      }
    }]""",
      GraphNode(id, Position(x, y, z), label, color)
    )

  def validEdgeJsonGen: Gen[String] =
    for
      source <- Gen.choose(1, 1000)
      target <- Gen.choose(1, 1000)
    yield s"""[{
      "source": $source,
      "target": $target
      }]"""

  test("Add nodes from JSON") {
    forAll(validNodeJsonGen) {
      case (jsonString, validJsonNodeFromParams) =>
        GraphAPI.addNodesFromJson(
          jsonString
        ).isEmpty || (GraphState.nodes.now() contains validJsonNodeFromParams)
    }
  }

  test("Add edges from JSON") {
    forAll(validEdgeJsonGen) { jsonString =>
      GraphAPI.addEdgesFromJson(
        jsonString
      ).isEmpty
    }
  }
