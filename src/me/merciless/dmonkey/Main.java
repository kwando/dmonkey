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

  public static void main(String[] args) {
    Logger.getLogger("").setLevel(Level.WARNING);
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
  SpotLight sp = new SpotLight();

  @Override
  public void simpleInitApp() {
    viewPort.addProcessor(dsp = new DeferredSceneProcessor(this));
    FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
    fpp.addFilter(new com.jme3.post.filters.BloomFilter(com.jme3.post.filters.BloomFilter.GlowMode.Scene));
    fpp.addFilter(new FXAAFilter());
    fpp.addFilter(new FXAAFilter());
    //viewPort.addProcessor(fpp);

    flyCam.setMoveSpeed(10);

    sp.setColor(ColorRGBA.Red);
    sp.setPosition(new Vector3f(0, 3.9f, 0));
    sp.setDirection(new Vector3f(0, -1, 0).normalizeLocal());
    //sp.setDirection(new Vector3f(0.0077216243f, -0.14402537f, 0.98954386f).normalizeLocal());
    sp.setSpotOuterAngle(20f * FastMath.DEG_TO_RAD);
    sp.setSpotRange(15f);
    rootNode.addLight(sp);

    Material mat = assetManager.loadMaterial("DMonkey/TestMaterial.j3m");
    Spatial geom = assetManager.loadModel("Models/brokenCube.j3o");
    geom.setMaterial(mat);
    BatchNode cubes = new BatchNode();
    int side = 10;
    float moveScale = 1.9f;

    for (int i = 0; i < side; i++) {
      for (int j = 0; j < side; j++) {
        geom.setLocalTranslation((i - side / 2) * moveScale, FastMath.nextRandomFloat() * 10, (j - side / 2) * moveScale);
        cubes.attachChild(geom);
        geom = geom.clone();

        PointLight pl = new PointLight();

        pl.setPosition(geom.getLocalTranslation());

        ColorRGBA color = ColorRGBA.randomColor();
        color.a = 10 * 8.6f;
        pl.setColor(color);
        pl.setRadius(5f);
        rootNode.addLight(pl);
//		someLights.add(pl);
      }
    }

    rootNode.attachChild(cubes);

    AmbientLight al = new AmbientLight();
    al.setColor(ColorRGBA.DarkGray);
    rootNode.addLight(al);

    DirectionalLight dl = new DirectionalLight();
    dl.setColor(ColorRGBA.Red);
    dl.setDirection(new Vector3f(1, -1, 0).normalize());
    someLights.add(dl);
    rootNode.addLight(dl);

    dl = new DirectionalLight();
    dl.setColor(ColorRGBA.Green);
    dl.setDirection(new Vector3f(1, 0, 0).normalize());
    someLights.add(dl);
    rootNode.addLight(dl);

    dl = new DirectionalLight();
    dl.setColor(ColorRGBA.Blue);
    dl.setDirection(new Vector3f(0, 0, 1).normalize());
    someLights.add(dl);
    rootNode.addLight(dl);

    DeferredShadingUtils.scanNode(dsp, rootNode);

    stateManager.attach(new ScreenshotAppState("E:/Game-Development/Workbench/dmonkey2/"));
    inputManager.addRawInputListener(new RawInputListener() {
      @Override
      public void onTouchEvent(TouchEvent evt) {
      }

      @Override
      public void onMouseMotionEvent(MouseMotionEvent evt) {
      }

      @Override
      public void onMouseButtonEvent(MouseButtonEvent evt) {
      }

      @Override
      public void onKeyEvent(KeyInputEvent evt) {
        boolean isPressed = evt.isPressed();

        if (!isPressed) {
          switch (evt.getKeyCode()) {
            case KeyInput.KEY_1:
              if (someLights.isEmpty()) {
                System.out.println("No more lights to remove");
                return;
              }
              Light light = someLights.remove(someLights.size() - 1);

              if (light != null) {
                dsp.removeLight(light);
              }
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
      public void onJoyButtonEvent(JoyButtonEvent evt) {
      }

      @Override
      public void onJoyAxisEvent(JoyAxisEvent evt) {
      }

      @Override
      public void endInput() {
      }

      @Override
      public void beginInput() {
      }
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
      if (slight instanceof PointLight) {
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
