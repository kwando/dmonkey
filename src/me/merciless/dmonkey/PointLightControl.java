/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.merciless.dmonkey;

import com.jme3.light.PointLight;
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
  private final PointLight light;

  PointLightControl(Material material, PointLight light) {
    this.material = material;
    this.light = light;
  }

  @Override
  protected void controlUpdate(float tpf) {
    this.spatial.setLocalTranslation(light.getPosition());
    material.setVector3("LightPosition", light.getPosition());
  }

  @Override
  protected void controlRender(RenderManager rm, ViewPort vp) {
  }

  public Control cloneForSpatial(Spatial spatial) {
    throw new UnsupportedOperationException();
  }
}
