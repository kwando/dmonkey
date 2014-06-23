package me.merciless.dmonkey;

import java.util.ArrayList;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.renderer.Renderer;
import com.jme3.scene.BatchNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.merciless.dmonkey.test.CubesTestScene;
import me.merciless.dmonkey.test.PhysicalLighting;
import me.merciless.dmonkey.test.ShadingTest;
import me.merciless.utils.LetterBox;

/**
 * test
 *
 * @author normenhansen
 */
public class Main extends SimpleApplication {

  private ArrayList<Light> someLights = new ArrayList<Light>();

  public static void main(String[] args) {
    Logger.getLogger("").setLevel(Level.WARNING);
    SimpleApplication app = new PhysicalLighting();
    AppSettings settings = new AppSettings(true);
    settings.setResolution(1280, 720);
    settings.setVSync(true);
    settings.setFullscreen(false);
    settings.setDepthBits(24);
    app.setSettings(settings);
    app.setShowSettings(false);
    app.start();
  }
  DeferredSceneProcessor dsp;

  @Override
  public void simpleInitApp() {
    viewPort.addProcessor(dsp = new DeferredSceneProcessor(this));
    FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
    //fpp.addFilter(new com.jme3.post.filters.BloomFilter(com.jme3.post.filters.BloomFilter.GlowMode.Scene));
    //fpp.addFilter(new FXAAFilter());
    fpp.addFilter(new FXAAFilter());
    viewPort.addProcessor(fpp);

    flyCam.setMoveSpeed(10);

    Material mat = assetManager.loadMaterial("DMonkey/TestMaterial.j3m");
    Spatial geom = assetManager.loadModel("Models/brokenCube.j3o");
    geom.setMaterial(mat);
    Node cubes = new Node();
    
    int side = 10;
    float moveScale = 1.9f;

    for (int i = 0; i < side; i++) {
      for (int j = 0; j < side; j++) {
        geom.setLocalTranslation((i - side / 2) * moveScale, FastMath.nextRandomFloat() * 10, (j - side / 2) * moveScale);
        cubes.attachChild(geom);
        Spatial old = geom;
        geom = geom.clone();
        old.addControl(new RotationControl(new Vector3f(FastMath.nextRandomFloat(), FastMath.nextRandomFloat(), FastMath.nextRandomFloat())));

        PointLight pl = new PointLight();

        pl.setPosition(geom.getLocalTranslation());

        ColorRGBA color = ColorRGBA.randomColor();
        color.a = 10 * 8.6f;
        pl.setColor(color);
        pl.setRadius(8f);
        rootNode.addLight(pl);
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

    stateManager.attach(new DebugControl(dsp));
  }

  @Override
  public void simpleUpdate(float tpf) {
  }
}
