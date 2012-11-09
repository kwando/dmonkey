/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.merciless.dmonkey;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;

/**
 *
 * @author kwando
 */
public class AmbientQuad{

  private Geometry quad;

  public AmbientQuad(AssetManager assets, GBuffer gbuffer) {
    this.quad = new Geometry("DistantLight", new Quad(1, 1));
    Material material = new Material(assets, "DMonkey/AmbientLight.j3md");
    material.setTexture("DiffuseBuffer", gbuffer.diffuse);
    material.setTexture("DepthBuffer", gbuffer.Zbuffer);
    material.setTexture("NormalBuffer", gbuffer.normals);
    RenderState rs = material.getAdditionalRenderState();
    rs.setBlendMode(RenderState.BlendMode.Additive);
    rs.setDepthTest(false);
    rs.setDepthWrite(false);
    this.quad.setMaterial(material);
  }
  public void render(RenderManager rm){
    rm.renderGeometry(quad);
  }
}
