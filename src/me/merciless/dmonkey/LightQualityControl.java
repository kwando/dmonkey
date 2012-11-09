/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.merciless.dmonkey;

import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 *
 * @author kwando
 */
public class LightQualityControl extends AbstractControl {

  private Material material;
  private Camera cam;
  private boolean inside = true;

  public LightQualityControl(Material material, Camera cam) {
    this.material = material;
    this.cam = cam;
  }

  @Override
  protected void controlUpdate(float tpf) {
    Vector3f pos = this.spatial.getWorldTranslation();
    float d = cam.getLocation().distance(pos);
    float lightRadius = spatial.getLocalScale().x / 2f;
    if (inside && d > lightRadius + cam.getFrustumNear()) {
      inside = false;
      material.getAdditionalRenderState().setDepthTest(true);
      material.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);
      material.setBoolean("specular", false);
      return;
    }
    if (!inside && d < lightRadius + cam.getFrustumNear()) {
      inside = true;
      material.setBoolean("specular", true);
      material.getAdditionalRenderState().setDepthTest(false);
      material.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Front);
      return;
    }
  }

  @Override
  protected void controlRender(RenderManager rm, ViewPort vp) {
  }

  public Control cloneForSpatial(Spatial spatial) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
