package view

import com.raquo.laminar.api.L._
import org.scalajs.dom
import state.AnimationState.engine
import state.AnimationState.running
import state.GraphState.edges
import state.GraphState.nodes
import view.components.AnimationControllerView
import view.components.EngineSettingsControllerView
import view.components.GridViewControllerView
import view.config.ViewConfig
import view.graph.scene.ThreeScene
import view.player.EngineController
import view.player.EngineLoopPlayer

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

@js.native
@JSGlobal("scastie.ClientMain")
object ScastieClientMain extends js.Object:
  def signal: js.Function3[js.Any, js.Any, js.Any, Unit] = js.native
  def signal_=(value: js.Function3[js.Any, js.Any, js.Any, Unit]): Unit =
    js.native

object MainView:
  def apply(config: ViewConfig): MainView = new MainView(config)

final class MainView(config: ViewConfig):
  private type ScastieSignal = js.Function3[js.Any, js.Any, js.Any, Unit]

  private val scene: ThreeScene = ThreeScene(config.sceneConfig)
  private val sceneController: GridViewControllerView =
    GridViewControllerView(scene)
  private val engineController: EngineController = EngineController()()()
  private val engineSettings: EngineSettingsControllerView =
    EngineSettingsControllerView(engineController)
  private val animationController: AnimationControllerView =
    AnimationControllerView()
  private val player: EngineLoopPlayer.type = EngineLoopPlayer
  private val scastieLoadingSignal: ScastieSignal =
    (result, attachedElements, scastieId) =>
      engineController.loadEngine()
      scene.centerView()

  private def initialize(): Unit =
    ScastieClientMain.signal = scastieLoadingSignal
  def render(): Unit =
    val rootElement = div(
      scene.renderScene("three_canvas"),
      sceneController.render,
      animationController.render,
      engineSettings.render,
      running --> (isRunning => if isRunning then player.start()),
      engine --> (maybeEngine =>
        maybeEngine.foreach(_ => player.loadNextFrame())
      ),
      edges.combineWith(nodes) --> { case (es, ns) =>
        scene.setNodes(ns)
        scene.setEdges(es)
      },
      onMountCallback(_ => initialize())
    )
    renderOnDomContentLoaded(
      dom.document.getElementById("app"),
      rootElement
    )
