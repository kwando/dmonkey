package me.merciless.dmonkey3;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.FrameBuffer;

/**
 *
 * @author kwando
 */
public class LightRenderer implements GBufferListener, SceneProcessor {

  private Geometry fullscreenQuad;
  private GBuffer gbuffer;
  private RenderManager renderManager;
  private Material material;
  private boolean gbufferChanged = false;

  private LightRenderer(AssetManager assets) {
    this.fullscreenQuad = new Geometry("LightRenderQuad", new Quad(1, 1));
    material = new Material(assets, "DMonkey/AmbientLight.j3md");
    material.setColor("Color", ColorRGBA.Blue);
    fullscreenQuad.setMaterial(material);
    fullscreenQuad.setCullHint(Spatial.CullHint.Never);
  }

  public static LightRenderer initialize(AssetManager assets) {
    return new LightRenderer(assets);
  }

  @Override
  public void onReshape(GBuffer gbuffer) {
    this.gbuffer = gbuffer;
    gbufferChanged = true;
  }

  private boolean updateMaterial() {
    if (gbufferChanged && material != null) {
      material.setTexture("DiffuseBuffer", gbuffer.diffuse);
      material.setTexture("NormalBuffer", gbuffer.normals);
      material.setTexture("DepthBuffer", gbuffer.depth);
      material.getAdditionalRenderState().setDepthTest(false);
      material.getAdditionalRenderState().setDepthWrite(false);
      gbufferChanged = false;
      return true;
    }
    return false;
  }

  @Override
  public void initialize(RenderManager rm, ViewPort vp) {
    renderManager = rm;
  }

  @Override
  public void reshape(ViewPort vp, int w, int h) {
  }

  @Override
  public boolean isInitialized() {
    return renderManager != null;
  }

  @Override
  public void preFrame(float tpf) {
  }

  @Override
  public void postQueue(RenderQueue rq) {
  }

  @Override
  public void postFrame(FrameBuffer out) {
    updateMaterial();
    
    renderManager.getRenderer().setFrameBuffer(null);
    renderManager.getRenderer().clearBuffers(true, true, true);
    renderManager.renderGeometry(fullscreenQuad);
    renderManager.getRenderer().setFrameBuffer(out);
  }

  @Override
  public void cleanup() {
    renderManager = null;
  }
}
