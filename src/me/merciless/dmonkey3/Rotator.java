/*

 */
package me.merciless.dmonkey3;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author kwando
 */
public class Rotator extends AbstractControl {

  @Override
  protected void controlUpdate(float tpf) {
    spatial.rotate(tpf / 13, tpf, tpf / 27 * 10);
  }

  @Override
  protected void controlRender(RenderManager rm, ViewPort vp) {
  }
}
