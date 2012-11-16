package me.merciless.dmonkey;

import java.util.ArrayList;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
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

	private ArrayList<PointLight> someLights = new ArrayList<PointLight>();
	
  public static void main(String[] args) {
    Main app = new Main();
    AppSettings settings = new AppSettings(true);
    settings.setWidth(1280);
    settings.setHeight(720);
    settings.setVSync(true);
    //settings.setDepthBits(16);

    app.setSettings(settings);
    //app.setShowSettings(false);
    app.start();
  }
  DeferredSceneProcessor dsp;
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

        PointLight pl = new PointLight();

		pl.setPosition(geom.getLocalTranslation());

		ColorRGBA color = ColorRGBA.randomColor();
		color.a = 10 * 8.6f;
		pl.setColor(color);
		pl.setRadius(5f);
		rootNode.addLight(pl);
		someLights.add(pl);
      }
    }

    rootNode.attachChild(cubes);
    
    inputManager.addRawInputListener(new RawInputListener() {
		
		@Override
		public void onTouchEvent(TouchEvent evt) { }
		
		@Override
		public void onMouseMotionEvent(MouseMotionEvent evt) { }
		
		@Override
		public void onMouseButtonEvent(MouseButtonEvent evt) { }
		
		@Override
		public void onKeyEvent(KeyInputEvent evt) {
			boolean isPressed = evt.isPressed();
			
			if(!isPressed) {
				switch (evt.getKeyCode()) {
					case KeyInput.KEY_1:
						PointLight light = someLights.remove(someLights.size());

						if(light != null)
							dsp.removeLight(light);
						break;
	
					default:
						break;
				}
			}
		}
		
		@Override
		public void onJoyButtonEvent(JoyButtonEvent evt) { }
		
		@Override
		public void onJoyAxisEvent(JoyAxisEvent evt) { }
		
		@Override
		public void endInput() { }
		
		@Override
		public void beginInput() { }
	});
  }

  private float time;
  private float period = 10;

  @Override
  public void simpleUpdate(float tpf) {
	    time += tpf;
	    if (time > period) {
	      time -= period;
	    }
	    
	    for (PointLight slight : someLights) {
	    	Vector3f origin = slight.getPosition();
	    	slight.setPosition(new Vector3f(origin.x, FastMath.sin(time / period * FastMath.TWO_PI) * 1 + 1.2f, origin.z));
		}
  }

  @Override
  public void simpleRender(RenderManager rm) {
    //TODO: add render code
  }
}
