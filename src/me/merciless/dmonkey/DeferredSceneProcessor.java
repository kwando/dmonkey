/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.merciless.dmonkey;

import me.merciless.dmonkey.lights.Ambient;
import me.merciless.dmonkey.lights.DLight;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
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
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import me.merciless.dmonkey.lights.DPointLight;
import me.merciless.dmonkey.lights.DSpotLight;

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
  private final Ambient ambient;
  
  public DeferredSceneProcessor(Application app) {
    this.assets = app.getAssetManager();
    this.lightNode = new Node("BoundingVolumes");
    ambient = new Ambient();
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
    
    ambient.initialize(this, gbuffer, assets);
    scanRootNode();

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
    lightNode.updateGeometricState();
  }

  public void scanRootNode() {
    // It's always the rootNode right?
    Node rootNode = (Node) vp.getScenes().get(0);
    DeferredShadingUtils.scanNode(this, rootNode);
  }
  
  public void removeLight(Light light) {
		switch (light.getType()) {
			case Ambient:
			case Directional:
				ambient.removeLight(light);
				break;
			default:
				DLight l = this.getLight(light);
	
				if (l != null)
					l.clean();
		}
  }

  public DLight addLight(Light light, boolean check) {
    DeferredSceneProcessor dsp = this;
		if (check) {
			DLight l = getLight(light);
			if (l != null)
				return l;
		}

		switch (light.getType()) {
			case Ambient:
			case Directional:
				ambient.addLight(light);
				return ambient;
			case Point: {
      DPointLight l = new DPointLight((PointLight)light);
				l.initialize(dsp, gbuffer, assets);
				l.addControl(new LightQualityControl(l.getMaterial(), lightVp.getCamera()));
				dsp.lightNode.attachChild(l);
				return l;
			}
			case Spot: {
				DSpotLight l = new DSpotLight((SpotLight)light);
				l.initialize(dsp, gbuffer, assets);
				dsp.lightNode.attachChild(l);
				return l;
			}
			default:
				System.out.println("Unsuported light type: " + light.getType() + " - " + light.getName());
				break;
		}

		return null;
	}
  public DLight getLight(Light light) {
		String id = "DPS-" + light.getType() + "-[" + light.getName() + "/" + light.hashCode() + "]";
		return (DLight) lightNode.getChild(id);
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
    rm.renderViewPort(lightVp, tpf);
    
    ambient.render(rm, vp);
    
    if (debugLights) {
   	  lightNode.updateGeometricState();
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
}
