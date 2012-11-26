package me.merciless.dmonkey;

import java.util.ArrayList;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
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
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.TangentBinormalGenerator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * test
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication {

	private ArrayList<Light> someLights = new ArrayList<Light>();
    private float grassScale = 64;
    private float dirtScale = 16;
    private float rockScale = 128;

  public static void main(String[] args) {
    Logger.getLogger("").setLevel(Level.WARNING);
    Main app = new Main();
    AppSettings settings = new AppSettings(true);
    settings.setWidth(1280);
    settings.setHeight(720);
    settings.setVSync(true);
    //settings.setDepthBits(16);

    app.setSettings(settings);
    app.setShowSettings(false);
    app.start();
  }
  DeferredSceneProcessor dsp;
  SpotLight sp = new SpotLight();
  @Override
  public void simpleInitApp() {
    viewPort.addProcessor(dsp = new DeferredSceneProcessor(this));
    FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
    fpp.addFilter(new BloomFilter(BloomFilter.GlowMode.Scene));
    fpp.addFilter(new FXAAFilter());
    fpp.addFilter(new FXAAFilter());
    viewPort.addProcessor(fpp);

    flyCam.setMoveSpeed(60);

	sp.setColor(ColorRGBA.Red);
	sp.setPosition(new Vector3f(0, 3.9f, 0));
	sp.setDirection(new Vector3f(1, -1, 0).normalizeLocal());
	//sp.setDirection(new Vector3f(0.0077216243f, -0.14402537f, 0.98954386f).normalizeLocal());
	sp.setSpotOuterAngle(20f  * FastMath.DEG_TO_RAD);
	sp.setSpotRange(15f);
	rootNode.addLight(sp);

	Material matTerrain = new Material(assetManager, "DMonkey/Terrain/Terrain.j3md");
    matTerrain.setBoolean("useTriPlanarMapping", false);
    matTerrain.setBoolean("WardIso", false);
    matTerrain.setFloat("Shininess", 20);

    // ALPHA map (for splat textures)
    matTerrain.setTexture("AlphaMap", assetManager.loadTexture("Textures/Terrain/splat/alphamap.png"));

    // GRASS texture
    Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
    grass.setWrap(WrapMode.Repeat);
    matTerrain.setTexture("DiffuseMap", grass);
    matTerrain.setFloat("DiffuseMap_0_scale", grassScale);

    // DIRT texture
    Texture dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
    dirt.setWrap(WrapMode.Repeat);
    matTerrain.setTexture("DiffuseMap_1", dirt);
    matTerrain.setFloat("DiffuseMap_1_scale", dirtScale);

    // ROCK texture
    Texture rock = assetManager.loadTexture("Textures/Terrain/splat/road.jpg");
    rock.setWrap(WrapMode.Repeat);
    matTerrain.setTexture("DiffuseMap_2", rock);
    matTerrain.setFloat("DiffuseMap_2_scale", rockScale);

//    matTerrain.setFloat("DiffuseMap_0_scale", 1f / (float) (512f / grassScale));
//    matTerrain.setFloat("DiffuseMap_1_scale", 1f / (float) (512f / dirtScale));
//    matTerrain.setFloat("DiffuseMap_2_scale", 1f / (float) (512f / rockScale));
//    matTerrain.setFloat("DiffuseMap_3_scale", 1f / (float) (512f / rockScale));
//    matTerrain.setFloat("DiffuseMap_4_scale", 1f / (float) (512f / rockScale));

  Texture normalMap0 = assetManager.loadTexture("Textures/Terrain/splat/grass_normal.jpg");
  normalMap0.setWrap(WrapMode.Repeat);
  Texture normalMap1 = assetManager.loadTexture("Textures/Terrain/splat/dirt_normal.png");
  normalMap1.setWrap(WrapMode.Repeat);
  Texture normalMap2 = assetManager.loadTexture("Textures/Terrain/splat/road_normal.png");
  normalMap2.setWrap(WrapMode.Repeat);
  matTerrain.setTexture("NormalMap", normalMap0);
  matTerrain.setTexture("NormalMap_1", normalMap1);
  matTerrain.setTexture("NormalMap_2", normalMap2);

    // HEIGHTMAP image (for the terrain heightmap)
    Texture heightMapImage = assetManager.loadTexture("Textures/Terrain/splat/mountains512.png");
    AbstractHeightMap heightmap = null;
    try {
        heightmap = new ImageBasedHeightMap(heightMapImage.getImage(), 1.5f);
        heightmap.load();
        heightmap.smooth(0.9f, 1);

    } catch (Exception e) {
        e.printStackTrace();
    }
    
    // CREATE THE TERRAIN
    TerrainQuad terrain = new TerrainQuad("terrain", 65, 513, heightmap.getHeightMap());
    TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
    control.setLodCalculator( new DistanceLodCalculator(65, 2.7f) ); // patch size, and a multiplier
    terrain.addControl(control);
    terrain.setMaterial(matTerrain);
    terrain.setLocalTranslation(0, -100, 0);
    terrain.setLocalScale(2.5f, 0.5f, 2.5f);
    rootNode.attachChild(terrain);
    
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

    AmbientLight al = new AmbientLight();
    al.setColor(ColorRGBA.DarkGray);
    rootNode.addLight(al);
    
    DirectionalLight dl = new DirectionalLight();
    dl.setColor(ColorRGBA.LightGray.addLocal(ColorRGBA.White));
    dl.setDirection(new Vector3f(0, -1, 0).normalizeLocal());
    someLights.add(dl);
    rootNode.addLight(dl);
    
    dl = new DirectionalLight();
    dl.setColor(ColorRGBA.Green);
    dl.setDirection(new Vector3f(1,0,0).normalize());
    //someLights.add(dl);
    //rootNode.addLight(dl);
    
    dl = new DirectionalLight();
    dl.setColor(ColorRGBA.Blue);
    dl.setDirection(new Vector3f(0,0,1).normalize());
    //someLights.add(dl);
    //rootNode.addLight(dl);
    
    DeferredShadingUtils.scanNode(dsp, rootNode);
    
    stateManager.attach(new ScreenshotAppState("E:/Game-Development/Workbench/dmonkey2/"));
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
						if(someLights.isEmpty()) {
							System.out.println("No more lights to remove");
							return;
						}
						Light light = someLights.remove(someLights.size() - 1);

						if(light != null)
							dsp.removeLight(light);
						break;
	
					case KeyInput.KEY_2: {
						Vector3f pos = sp.getPosition();
						pos.y -= 0.1f;
						sp.setPosition(pos);
						break;
					}
					case KeyInput.KEY_3: {
						Vector3f pos = sp.getPosition();
						pos.y += 0.1f;
						sp.setPosition(pos);
						break;
					}
					case KeyInput.KEY_4: {
						sp.setPosition(cam.getLocation());
						sp.setDirection(cam.getDirection());
						break;
					}
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
	    
	    for (Light slight : someLights) {
	    	if(slight instanceof PointLight) {
		    	Vector3f origin = ((PointLight) slight).getPosition();
		    	((PointLight) slight).setPosition(new Vector3f(origin.x, FastMath.sin(time / period * FastMath.TWO_PI) * 1 + 1.2f, origin.z));
	    	}
		}
  }

  @Override
  public void simpleRender(RenderManager rm) {
    //TODO: add render code
  }
}
