/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.merciless.dmonkey;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 *
 * @author kwando
 */
public class LightControl extends AbstractControl {

  private float time;
  private float period = 10;
  private Vector3f origin;

  public LightControl(Vector3f origin) {
    this.origin = origin.clone();
  }

  @Override
  protected void controlUpdate(float tpf) {
    time += tpf;
    if (time > period) {
      time -= period;
    }
    float frac = time / period;
    this.spatial.setLocalTranslation(origin.add(-3, FastMath.sin(time / period * FastMath.TWO_PI) * 1 + 1.2f, 0));
  }

  @Override
  protected void controlRender(RenderManager rm, ViewPort vp) {
  }

  public Control cloneForSpatial(Spatial spatial) {
    Control control = new LightControl(origin);
    control.setSpatial(spatial);
    return control;
  }
}
