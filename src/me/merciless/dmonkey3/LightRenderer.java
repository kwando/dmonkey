package me.merciless.dmonkey3;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.FrameBuffer;
import me.merciless.utils.color.CSSColor;

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
  private Camera camera;
  
  private Vector3f vsLightDir = new Vector3f();
  
  private DirectionalLight dl = new DirectionalLight();

  private LightRenderer(AssetManager assets) {
    this.fullscreenQuad = new Geometry("LightRenderQuad", new Quad(1, 1));
    material = new Material(assets, "DMonkey/AmbientLight.j3md");
    material.setColor("Color", dl.getColor());
    dl.setColor(new ColorRGBA(CSSColor.LightSkyBlue).interpolateLocal(ColorRGBA.White, 0.5f));
    fullscreenQuad.setMaterial(material);
    fullscreenQuad.setCullHint(Spatial.CullHint.Never);
    
    dl.setDirection(Vector3f.UNIT_XYZ);
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
    camera = vp.getCamera();
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
    vsLightDir.set(camera.getViewMatrix().mult(dl.getDirection().normalizeLocal()).normalize());
    
    Vector3f vec3 = dl.getDirection();
    Vector4f vec4 = new Vector4f(vec3.x, vec3.y, vec3.z, 0);
    camera.getViewMatrix().mult(vec4, vec4);
    
    vec3 = new Vector3f(vec4.x, vec4.y, vec4.z);
    material.setVector3("ViewLightDir", vec3);
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
