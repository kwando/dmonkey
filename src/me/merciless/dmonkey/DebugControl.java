package me.merciless.dmonkey;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;

/**
 *
 * @author kwando
 */
public class DebugControl extends AbstractAppState {

  private DeferredSceneProcessor dsp;

  public DebugControl(DeferredSceneProcessor dsp) {
    this.dsp = dsp;
  }

  @Override
  public void initialize(AppStateManager stateManager, Application app) {
    app.getInputManager().addRawInputListener(new RawInputListener() {
      @Override
      public void onTouchEvent(TouchEvent evt) {
      }

      @Override
      public void onMouseMotionEvent(MouseMotionEvent evt) {
      }

      @Override
      public void onMouseButtonEvent(MouseButtonEvent evt) {
      }

      @Override
      public void onKeyEvent(KeyInputEvent evt) {
        boolean isPressed = evt.isPressed();

        if (!isPressed) {
          switch (evt.getKeyCode()) {
            case KeyInput.KEY_I: {
              dsp.toggleDebug();
              return;
            }
          }
        }
      }

      @Override
      public void onJoyButtonEvent(JoyButtonEvent evt) {
      }

      @Override
      public void onJoyAxisEvent(JoyAxisEvent evt) {
      }

      @Override
      public void endInput() {
      }

      @Override
      public void beginInput() {
      }
    });
  }

  @Override
  public void cleanup() {
  }
}
