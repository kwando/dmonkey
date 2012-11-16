package me.merciless.dmonkey.lights;

import me.merciless.dmonkey.DeferredSceneProcessor;
import me.merciless.dmonkey.GBuffer;

import com.jme3.asset.AssetManager;
import com.jme3.light.Light;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;

/**
 * @author Seth
 */
public abstract class DLight extends Geometry {

	private final Light light;

	public DLight(Light light) {
		this.light = light;
	}

	public abstract void initialize(DeferredSceneProcessor dsp, GBuffer buff, AssetManager assetManager);
	public abstract void render(RenderManager rm, ViewPort vp);
	public abstract void update(float tpf);
	public abstract void clean();
	
	@SuppressWarnings("unchecked")
	public <T extends Light> T getLight() {
		return (T)light;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Light> T getLight(Class<T> type) {
		return (T)light;
	}

	@Override
	public Geometry clone(boolean cloneMaterial) {
		throw new UnsupportedOperationException("You cannot do that. Clone the light and add it to the processor.");
	}

	@Override
	public void runControlRender(RenderManager rm, ViewPort vp) {
		render(rm, vp);
		super.runControlRender(rm, vp);
	}
	
	@Override
	public void updateLogicalState(float tpf) {
		update(tpf);
		super.updateLogicalState(tpf);
	}
	
	

}
