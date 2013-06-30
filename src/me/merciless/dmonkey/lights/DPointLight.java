package me.merciless.dmonkey.lights;

import com.jme3.asset.AssetManager;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Matrix4f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.shape.Sphere;
import me.merciless.dmonkey.DeferredSceneProcessor;
import me.merciless.dmonkey.GBuffer;
import me.merciless.dmonkey.LightQualityControl;

/**
 * @author Seth
 */
public final class DPointLight extends DLight<PointLight> {

  public DPointLight(PointLight light, DeferredSceneProcessor dsp) {
    super(light, dsp);

    // Better option? lol
    setName("DPS-" + light.getType() + "-[" + light.getName() + "/" + light.hashCode() + "]");

    mesh = new Sphere(8, 12, 1f);
    mesh.setStatic();
    setBoundRefresh();
  }

  @Override
  public void initialize(DeferredSceneProcessor dsp, GBuffer buff, AssetManager assetManager) {
    material = new Material(assetManager, "DMonkey/PointLight.j3md");
    material.setTexture("DiffuseBuffer", buff.getDiffuse());
    material.setTexture("DepthBuffer", buff.getDepth());
    material.setTexture("NormalBuffer", buff.getNormals());

    material.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
    material.getAdditionalRenderState().setDepthTest(true);
    material.getAdditionalRenderState().setDepthWrite(false);
    material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Additive);
    addControl(new LightQualityControl(material, dsp.getCamera()));
  }

  @Override
  public void render(RenderManager rm, ViewPort vp) {
    PointLight light = getLight();

    Matrix4f viewMatrix = vp.getCamera().getViewMatrix();
    material.setVector3("LightPosition", viewMatrix.mult(light.getPosition()));
    material.setColor("LightColor", light.getColor());
    material.setFloat("LightRadius", light.getRadius());
    material.setFloat("LightIntensity", 10f); // unused
  }

  @Override
  public void update(float tpf) {
    PointLight light = getLight();

    setLocalTranslation(light.getPosition());

    // TODO, calculate the real range needed..
    setLocalScale(light.getRadius());
  }

  @Override
  public void clean() {
    removeFromParent();
  }
}
