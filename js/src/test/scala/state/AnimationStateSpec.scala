package state

import domain.AnimationDomain.AnimationBatch
import domain.AnimationDomain.NextTick
import domain.AnimationDomain.PauseAnimation
import domain.AnimationDomain.Reset
import domain.AnimationDomain.SetEngine
import domain.AnimationDomain.StartAnimation
import domain.AnimationDomain.SwitchMode
import domain.AnimationDomain.ViewMode
import munit.FunSuite
import munit.ScalaCheckSuite
import org.scalacheck.Gen
import org.scalacheck.Prop
import org.scalacheck.Prop.forAll

import scala.scalajs.js

class AnimationStateSpec extends FunSuite with ScalaCheckSuite:

  override def beforeEach(context: BeforeEach): Unit =
    AnimationState.animationObserver.onNext(Reset())
    while AnimationState.mode.now() != ViewMode.Mode3D do
      AnimationState.animationObserver.onNext(SwitchMode())

  test("initial state validates default values") {
    Prop.all(
      AnimationState.batch.now() == 1,
      AnimationState.currentTick.now() == 0,
      AnimationState.engine.now().isEmpty,
      AnimationState.mode.now() == ViewMode.Mode3D
    )
  }

  test("updates engine and resets state") {
    forAll(Gen.const(js.Dynamic.literal())) { mockEngine =>
      AnimationState.animationObserver.onNext(SetEngine(mockEngine))
      Prop.all(
        AnimationState.engine.now().contains(mockEngine),
        !AnimationState.running.now(),
        AnimationState.currentTick.now() == 0
      )
    }
  }

  test("start animation") {

    AnimationState.animationObserver.onNext(StartAnimation())
    AnimationState.running.now()

  }

  test("stops animation") {

    AnimationState.animationObserver.onNext(StartAnimation())
    AnimationState.animationObserver.onNext(PauseAnimation())
    !AnimationState.running.now()

  }

  test("increments current tick") {

    val initialTick = AnimationState.currentTick.now()
    AnimationState.animationObserver.onNext(NextTick())
    AnimationState.currentTick.now() == initialTick + 1

  }

  test("updates batch value") {
    forAll(Gen.choose(1, 10)) { batchValue =>
      AnimationState.animationObserver.onNext(AnimationBatch(batchValue))
      AnimationState.batch.now() == batchValue
    }
  }

  test("restores initial state") {

    AnimationState.animationObserver.onNext(StartAnimation())
    AnimationState.animationObserver.onNext(NextTick())
    AnimationState.animationObserver.onNext(Reset())
    Prop.all(
      !AnimationState.running.now(),
      AnimationState.currentTick.now() == 0
    )

  }

  test("Switch mode between view modes") {

    val initialMode = AnimationState.mode.now()
    AnimationState.animationObserver.onNext(SwitchMode())

    AnimationState.mode.now() ==
      (if initialMode == ViewMode.Mode3D then ViewMode.Mode2D
       else ViewMode.Mode3D)

  }
