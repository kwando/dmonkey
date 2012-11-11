/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.merciless.dmonkey;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.shader.VarType;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture2D;

/**
 *
 * @author kwando
 */
public class DeferredSceneProcessor implements SceneProcessor {

  private ViewPort vp;
  private RenderManager rm;
  private Renderer renderer;
  private ViewPort lightVp;
  private float tpf;
  private AssetManager assets;
  private Node lightNode;
  private GBuffer gbuffer;
  private boolean debugLights = false;
  private Geometry resolveQuad;
  private FrameBuffer lightBuffer;
  private Texture2D lightTexture;
  private AmbientQuad ambient;

  public DeferredSceneProcessor(Application app) {
    this.assets = app.getAssetManager();
    this.lightNode = new Node("BoundingVolumes");
  }

  public void initialize(RenderManager rm, ViewPort vp) {
    this.vp = vp;
    this.rm = rm;
    this.renderer = rm.getRenderer();
    Camera cam = vp.getCamera();
    lightVp = new ViewPort("Lights", cam);
    lightVp.attachScene(lightNode);
    lightVp.setClearFlags(true, false, false);

    reshape(vp, cam.getWidth(), cam.getHeight());
    setupLights();


    resolveQuad = new Geometry("ResolveQuad", new Quad(1, 1));
    Material resolveMat = new Material(assets, "DMonkey/Resolve.j3md");
    resolveQuad.setMaterial(resolveMat);
    resolveQuad.setCullHint(Spatial.CullHint.Never);
    resolveMat.getAdditionalRenderState().setDepthTest(false);
    resolveMat.getAdditionalRenderState().setDepthWrite(false);
    resolveMat.setTexture("NormalBuffer", gbuffer.normals);
    resolveMat.setTexture("DepthBuffer", gbuffer.Zbuffer);
    resolveMat.setTexture("DiffuseBuffer", gbuffer.diffuse);
    resolveMat.setTexture("LightBuffer", lightTexture);

    resolveQuad.setQueueBucket(RenderQueue.Bucket.Opaque);
  }

  public void reshape(ViewPort vp, int w, int h) {
    gbuffer = new GBuffer(w, h);
    lightBuffer = new FrameBuffer(w, h, 1);
    lightTexture = new Texture2D(w, h, Image.Format.RGBA8);
    lightBuffer.setColorTexture(lightTexture);
    lightBuffer.setDepthTexture(gbuffer.Zbuffer);
    lightVp.setOutputFrameBuffer(lightBuffer);
    lightVp.setBackgroundColor(ColorRGBA.BlackNoAlpha);
  }

  public boolean isInitialized() {
    return vp != null;
  }

  public void preFrame(float tpf) {
    this.tpf = tpf;
    lightNode.updateLogicalState(tpf);
  }

  public void postQueue(RenderQueue rq) {
    lightNode.updateGeometricState();

    FrameBuffer fb = vp.getOutputFrameBuffer();
    rm.setForcedTechnique("GBuffer");

    rm.getRenderer().setFrameBuffer(gbuffer.fbo);
    renderer.setBackgroundColor(ColorRGBA.BlackNoAlpha);
    renderer.clearBuffers(true, true, true);
    rm.renderViewPortQueues(vp, true);
    rm.getRenderer().setFrameBuffer(fb);
    rm.setForcedTechnique("ForwardPass");
  }

  public void postFrame(FrameBuffer out) {
    rm.setForcedTechnique(null);
    rm.renderViewPort(lightVp, 0);
    ambient.render(rm);
    if (debugLights) {
      rm.setForcedMaterial(assets.loadMaterial("DMonkey/DebugMaterial.j3m"));
      rm.renderViewPortRaw(lightVp);
      rm.setForcedMaterial(null);
    }
    renderer.setFrameBuffer(out);
    rm.renderGeometry(resolveQuad);
  }

  public void cleanup() {
    rm = null;
  }

  private void setupLights() {
    ambient = new AmbientQuad(assets, gbuffer);

    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 10; j++) {
        PointLight pl = new PointLight();
        pl.setPosition(Vector3f.UNIT_Y.add(i * 1.5f, 0, j * 1.5f));
        pl.setColor(ColorRGBA.randomColor().add(new ColorRGBA(0, 0, 0, 10 * 8.6f)));
        pl.setRadius(20);
        addPointLight(pl);
      }
    }


  }

  private void addPointLight(PointLight light) {
    final Spatial model = assets.loadModel("DMonkey/PointLight.j3o");
    Material material = new Material(assets, "DMonkey/PointLight.j3md");
    material.setTexture("DiffuseBuffer", gbuffer.diffuse);
    material.setTexture("DepthBuffer", gbuffer.Zbuffer);
    material.setTexture("NormalBuffer", gbuffer.normals);

    ColorRGBA color = ColorRGBA.randomColor();
    color.a = 10 * 8.6f;
    material.setVector3("LightPosition", model.getLocalTranslation());
    material.setColor("LightColor", color);
    material.setFloat("LightRadius", 1f/light.getRadius());
    material.setFloat("LightIntensity", 10f);
    material.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
    material.getAdditionalRenderState().setDepthTest(true);
    material.getAdditionalRenderState().setDepthWrite(false);
    material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Additive);
    material.setParam("LightPositions", VarType.Vector3Array, new Vector3f[]{model.getLocalTranslation()});

    model.addControl(new LightControl(light.getPosition()));
    model.addControl(new PointLightControl(material));
    model.addControl(new LightQualityControl(material, lightVp.getCamera()));


    model.setMaterial(material);
    model.setLocalScale(light.getRadius()/4);

    lightNode.attachChild(model);
  }
}
