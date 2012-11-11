package me.merciless.dmonkey;

import me.merciless.dmonkey.lights.Ambient;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.BatchNode;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.util.TangentBinormalGenerator;

/**
 * test
 * 
 * @author normenhansen
 */
public class Main extends SimpleApplication {

	DeferredSceneProcessor dsp;

	public static void main(String[] args) {
		Main app = new Main();
		AppSettings settings = new AppSettings(true);
		settings.setWidth(1280);
		settings.setHeight(720);
		settings.setVSync(true);
		// settings.setDepthBits(16);

		app.setSettings(settings);
		// app.setShowSettings(false);
		app.start();
	}

	@Override
	public void simpleInitApp() {
		viewPort.addProcessor(dsp = new DeferredSceneProcessor(this));
		FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
		fpp.addFilter(new BloomFilter(BloomFilter.GlowMode.Scene));
		fpp.addFilter(new FXAAFilter());
		fpp.addFilter(new FXAAFilter());
		viewPort.addProcessor(fpp);

		flyCam.setMoveSpeed(10);

		Material mat = assetManager.loadMaterial("DMonkey/TestMaterial.j3m");
		Spatial geom = assetManager.loadModel("Models/brokenCube.j3o");
		geom.setMaterial(mat);
		TangentBinormalGenerator.generate(geom);
		BatchNode cubes = new BatchNode();
		int side = 20;
		float moveScale = 1.5f;

		for (int i = 0; i < side; i++) {
			for (int j = 0; j < side; j++) {
				geom.setLocalTranslation((i - side / 2) * moveScale, FastMath.nextRandomFloat(), (j - side / 2) * moveScale);
				cubes.attachChild(geom);
				geom = geom.clone();
			}
		}
		rootNode.attachChild(cubes);
		
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				addPointLight(Vector3f.UNIT_Y.add(i * 1.5f, 0, j * 1.5f));
			}
		}

		AmbientLight al = new AmbientLight();
		al.setColor(ColorRGBA.LightGray);
		dsp.addLight(al);

		al = new AmbientLight();
		al.setColor(ColorRGBA.Green);
		
		Ambient dal = dsp.addLight(al, Ambient.class);

		dal.setLightIntensity(.3f);
	}

	private void addPointLight(Vector3f origin) {
		PointLight pl = new PointLight();

		pl.setPosition(origin);

		ColorRGBA color = ColorRGBA.randomColor();
		color.a = 10 * 8.6f;
		pl.setColor(color);
		pl.setRadius(6f);
		dsp.addLight(pl);
	}

	@Override
	public void simpleUpdate(float tpf) {
		// TODO: add update code
	}

	@Override
	public void simpleRender(RenderManager rm) {
		// TODO: add render code
	}
}
