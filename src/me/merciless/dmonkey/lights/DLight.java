package me.merciless.dmonkey.lights;

import me.merciless.dmonkey.GBuffer;

import com.jme3.asset.AssetManager;
import com.jme3.light.Light;
import com.jme3.material.Material;
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

	@SuppressWarnings("unchecked")
	public <T extends Light> T getLight() {
		return (T)light;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Light> T getLight(Class<T> type) {
		return (T)light;
	}
	
	public abstract void initiate(GBuffer buff, AssetManager assetManager, ViewPort vp);
	
	/**
	 * Direct access, might be needed? If not dump it >.>
	 * Also if the processor needs to update light data, this method will be called
	 * @return
	 */
	public abstract Material updateAndGetMaterial();

	public abstract void clean();
}
