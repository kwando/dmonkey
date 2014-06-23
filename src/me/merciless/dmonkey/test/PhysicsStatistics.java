package me.merciless.dmonkey.test;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.scene.Node;
import com.sun.medialib.mlib.mediaLibException;
import java.util.concurrent.atomic.AtomicLong;
import me.merciless.utils.Timer;

/**
 *
 * @author kwando
 */
public class PhysicsStatistics extends AbstractAppState {

  private Application app;
  private final Node guiNode;
  private AtomicLong collisionCount = new AtomicLong();
  private BitmapText txt;
  private PhysicsCollisionListener listener;
  private Timer timer;

  public PhysicsStatistics(Node guiNode) {
    this.guiNode = guiNode;
    this.timer = new Timer(1f / 5);
  }

  @Override
  public void initialize(AppStateManager stateManager, Application app) {
    super.initialize(stateManager, app);
    this.app = app;

    BitmapFont font = app.getAssetManager().loadFont("Interface/Fonts/Futura.fnt");
    txt = new BitmapText(font);
    txt.setLocalTranslation(10, app.getCamera().getHeight() - 10, 0);
    
    updateLabel(collisionCount.get());
    
    guiNode.attachChild(txt);
    listener = new CollisionCounter(collisionCount);
    getPhysicsSpace().addCollisionListener(listener);
  }

  @Override
  public void update(float tpf) {
    super.update(tpf);
    if (timer.update(tpf)) {
      updateLabel(collisionCount.get());
    }
  }

  @Override
  public void cleanup() {
    getPhysicsSpace().removeCollisionListener(listener);
    txt.removeFromParent();
    super.cleanup();
  }

  private void updateLabel(long count) {
    String label = String.format("%dK collisions", collisionCount.get() / 1000);
    txt.setText(label);
  }

  private PhysicsSpace getPhysicsSpace() {
    return app.getStateManager().getState(BulletAppState.class).getPhysicsSpace();
  }

  private static class CollisionCounter implements PhysicsCollisionListener {

    private AtomicLong counter;

    private CollisionCounter(AtomicLong counter) {
      this.counter = counter;
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
      counter.incrementAndGet();
    }
  }
}
