/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.merciless.dmonkey;

import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.shader.VarType;

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
    
    // TODO, calculate the real range needed..
    this.spatial.setLocalScale(light.getRadius()/4);
  }

  @Override
  protected void controlRender(RenderManager rm, ViewPort vp) {
    Matrix4f viewMatrix = vp.getCamera().getViewMatrix();
    material.setVector3("LightPosition", viewMatrix.mult(light.getPosition()));
    material.setColor("LightColor", light.getColor());
    material.setFloat("LightRadius", 1f/light.getRadius());
    material.setFloat("LightIntensity", 10f); // unused
  }

  public Control cloneForSpatial(Spatial spatial) {
    throw new UnsupportedOperationException();
  }
}
