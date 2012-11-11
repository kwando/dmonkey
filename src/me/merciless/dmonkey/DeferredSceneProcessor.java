/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.merciless.dmonkey;

import java.util.logging.Logger;

import me.merciless.dmonkey.lights.Ambient;
import me.merciless.dmonkey.lights.DLight;
import me.merciless.dmonkey.lights.DPointLight;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.Light;
import com.jme3.light.LightList;
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

/**
 * 
 * @author kwando
 */
public final class DeferredSceneProcessor implements SceneProcessor {

	private static final Logger _log = Logger.getLogger(DeferredSceneProcessor.class.getName());

	public static boolean debugLights = false;

	private ViewPort vp;
	private RenderManager rm;
	private Renderer renderer;
	private ViewPort lightVp;
	private float tpf;
	private AssetManager assets;
	private GBuffer gbuffer;
	private Geometry resolveQuad;

	private FrameBuffer lightBuffer;
	private Texture2D lightTexture;
	private Ambient ambient;
	
	// It has two functions now, so it should only be accessed fomr within this class
	private Node lightNode;

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

		// Start it
		for(Spatial slight : lightNode.getChildren()) {
			((DLight) slight).initiate(gbuffer, assets, vp);
		}

		if(ambient != null) {
			ambient.initiate(gbuffer, assets, vp);
		}
		
		// XXX needed?
		setupLights();
		
		// Update to be safe
		updateLights();
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
		
		// Render the Ambient, if we have one
		if(ambient != null)
			rm.renderGeometry(ambient);
		
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
		for(Spatial light : lightNode.getChildren()) {
			light.addControl(new LightControl(light.getLocalTranslation()));
		}
	}

	/**
	 * @see DeferredSceneProcessor#addLight(Light)
	 */
	public void scanLightNode(Node node, boolean local) {
		LightList val = local ? node.getLocalLightList() : node.getWorldLightList();
		
		for (int i = 0; i < val.size(); i++) {
			
			Light light = val.get(i);
			
			DLight dLight = getDLight(light);
			
			if(dLight != null) {
				dLight.clean();
			}
			
			// Shouln't i remove the light from it's previous node?
			lightNode.addLight(light);
		}

		LightList lightList = lightNode.getLocalLightList(); // Uh feels dirty..
		
		int size = lightList.size();
		
		for (int i = 0; i < size; i++) {
			Light light = lightList.get(i);

			addLight(light, null, false);
		}
	}

	/**
	 * Will update light material for every volume light.
	 */
	public void updateLights() {
		for(Spatial light : lightNode.getChildren()) {
			((DLight) light).updateAndGetMaterial();
		}
		
		if(ambient != null)
			ambient.updateAndGetMaterial();
	}
	
	public void removeLight(Light light) {

		lightNode.removeLight(light);
		
		// Look for another one, if its the first one, else leave it be, the ambient class will handle the colors.
		if(light instanceof AmbientLight && (ambient != null && light == ambient.getLight())) {

			ambient.clean();
			ambient = null;

			LightList lightList = lightNode.getLocalLightList(); // Uh feels dirty..
			
			int size = lightList.size();
			
			for (int i = 0; i < size; i++) {
				light = lightList.get(i);
				if(light instanceof AmbientLight) {
					ambient = new Ambient(light, lightList);
					break;
				}
			}
			
			return;
		}

		DLight dl = getDLight(light);
		if(dl != null) {
			dl.clean();
		}
	}
	
	/**
	 * Will add the light to the light node, how it should be.
	 * @param light
	 */
	public <T extends DLight> T addLight(Light light) {
		return addLight(light, null, true);
	}
	
	public <T extends DLight> T addLight(Light light, Class<T> type) {
		return addLight(light, type, true);
	}
	
	@SuppressWarnings("unchecked")
	private <T extends DLight> T addLight(Light light, Class<T> type, boolean add) {
		
		if(add)
			lightNode.addLight(light);
		
		switch (light.getType()) {
			case Point: {
				DPointLight l = new DPointLight(light);
				
				if(isInitialized()) {
					l.initiate(gbuffer, assets, vp);
				}
				lightNode.attachChild(l);

				return (T) l;
			}
			case Spot: {
				break;
			}
			case Ambient:
				if(ambient == null) {
					ambient = new Ambient(light, getLightList());
					if(isInitialized()) {
						ambient.initiate(gbuffer, assets, vp);
					}
				}
				return (T) ambient;
			case Directional:  // Elsewhere
				break;
			default:
				_log.warning("Light type not implemented: "+light.getType());
		}
		return null;
	}

	public void updateForLight(Light light) {
		DLight dl = getDLight(light);

		if(dl != null)
			dl.updateAndGetMaterial();
	}
	
	public <T extends DLight> T getDLight(Light light) {
		return getDLight(light, null);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends DLight> T getDLight(Light light, Class<T> type) {
		for(Spatial l : lightNode.getChildren()) {
			DLight dl = (DLight)l;
			if(dl.getLight() == light)
				return (T) l;
		}
		return null;
	}

	public LightList getLightList() {
		return lightNode.getLocalLightList();
	}
}
