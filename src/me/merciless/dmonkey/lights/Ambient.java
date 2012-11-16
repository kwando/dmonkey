package me.merciless.dmonkey.lights;

import me.merciless.dmonkey.DeferredSceneProcessor;
import me.merciless.dmonkey.GBuffer;

import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.shape.Quad;
import com.jme3.util.SafeArrayList;

/**
 * @author Seth
 */
public final class Ambient extends DLight {

	// The reason this is here is to get the colors
	private final SafeArrayList<AmbientLight> lights = new SafeArrayList<AmbientLight>(AmbientLight.class);
	
	private final ColorRGBA ambientColor = new ColorRGBA(0,0,0,0);
	
	public Ambient() {
		super(null); // Nothing that needs to be keept track of

		mesh = new Quad(1, 1);
		mesh.setStatic();
		setBoundRefresh();
	}

	@Override
	public void initialize(DeferredSceneProcessor dsp, GBuffer buff, AssetManager assetManager) {
		material = new Material(assetManager, "DMonkey/AmbientLight.j3md");
		
	    material.setTexture("DiffuseBuffer", buff.getDiffuse());
	    material.setTexture("DepthBuffer", buff.getDepth());
	    material.setTexture("NormalBuffer", buff.getNormals());
	    
	    RenderState rs = material.getAdditionalRenderState();
	    rs.setBlendMode(RenderState.BlendMode.Additive);
	    rs.setDepthTest(false);
	    rs.setDepthWrite(false);
	}

	@Override
	public void render(RenderManager rm, ViewPort vp) {
		ambientColor.set(0, 0, 0, 0);

		for (AmbientLight light : lights) {
			ambientColor.addLocal(light.getColor());
		}
		
		if(ambientColor.a > 1.0f)
			ambientColor.a = 1.0f;
		
		material.setColor("Color", ambientColor);
	}
	
	@Override
	public void update(float tpf) {
	}

	public void addAmbientLight(Light light) {
		if(!lights.contains(light))
			lights.add((AmbientLight) light);
	}

	public void removeAmbientLight(Light light) {
		lights.remove(light);
	}

	@Override
	public void clean() { /* always here */ }
	
}
