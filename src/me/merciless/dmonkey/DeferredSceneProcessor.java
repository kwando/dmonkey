/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.merciless.dmonkey;

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
import java.util.HashMap;
import me.merciless.dmonkey.lights.Ambient;
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
  public AssetManager assets;
  private Node lightNode;
  public GBuffer gbuffer;
  private boolean debugLights = false;
  private Geometry resolveQuad;
  private FrameBuffer lightBuffer;
  private Texture2D lightTexture;
  
  private HashMap<Light, DLight>lights;
  private final Ambient ambient;
  private FrameBuffer outputBuffer;
  
  public DeferredSceneProcessor(Application app) {
    this.assets = app.getAssetManager();
    this.lightNode = new Node("BoundingVolumes");
    this.lights = new HashMap<Light, DLight>();
    ambient = new Ambient(this);
  }

  public void initialize(RenderManager rm, ViewPort vp) {
    this.vp = vp;
    this.rm = rm;
    this.renderer = rm.getRenderer();
    Camera cam = vp.getCamera();
    lightVp = new ViewPort("Lights", cam);
    lightVp.attachScene(lightNode);
    //lightVp.setClearFlags(true, false, false);

    reshape(vp, cam.getWidth(), cam.getHeight());

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
  
  public void removeLight(Light light) {
		DLight dlight = lights.remove(light);
    if(dlight != null){
      dlight.removeFromParent();
    }
  }

  public DLight addLight(Light light, boolean check) {
    
    DeferredSceneProcessor dsp = this;
		if (check) {
			DLight l = lights.get(light);
      
			if (l != null)
				return l;
		}

		switch (light.getType()) {
			case Ambient:
			case Directional:
        ambient.addLight(light);
				return ambient;
			case Point: {
      DPointLight l = new DPointLight((PointLight)light, this);
        lights.put(light, l);
				dsp.lightNode.attachChild(l);
				return l;
			}
			case Spot: {
				DSpotLight l = new DSpotLight((SpotLight)light, this);
        lights.put(light, l);
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
    return lights.get(light);
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
    ambient.updateLogicalState(tpf);
  }

  public void postQueue(RenderQueue rq) {
    lightNode.updateGeometricState();

    rm.setForcedTechnique("GBuffer");

    rm.getRenderer().setFrameBuffer(gbuffer.fbo);
    renderer.setBackgroundColor(ColorRGBA.BlackNoAlpha);
    renderer.clearBuffers(true, true, true);
    rm.renderViewPortQueues(vp, false);
    rm.setForcedTechnique("ForwardPass");
    renderer.setFrameBuffer(lightBuffer);
    renderer.setBackgroundColor(ColorRGBA.BlackNoAlpha);
    renderer.clearBuffers(true, false, false);
  }

  public void postFrame(FrameBuffer out) {
    rm.setForcedTechnique(null);
    rm.renderViewPort(lightVp, tpf);

    ambient.updateGeometricState();
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

  public Camera getCamera() {
    return lightVp.getCamera();
  }
}
