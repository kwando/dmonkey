/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.merciless.dmonkey;

import com.jme3.material.Material;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 *
 * @author kwando
 */
public class PointLightControl extends AbstractControl {
  private final Material material;

  public PointLightControl(Material material) {
    this.material = material;
  }

  @Override
  protected void controlUpdate(float tpf) {
    material.setVector3("LightPosition", this.spatial.getWorldTranslation());
  }

  @Override
  protected void controlRender(RenderManager rm, ViewPort vp) {
  }

  public Control cloneForSpatial(Spatial spatial) {
    Control control = new PointLightControl(((Geometry)spatial).getMaterial());
    control.setSpatial(spatial);
    return control;
  }
}
