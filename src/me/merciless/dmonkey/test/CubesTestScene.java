package me.merciless.dmonkey.test;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import java.util.Random;
import me.merciless.dmonkey.DebugControl;
import me.merciless.dmonkey.DeferredSceneProcessor;
import me.merciless.dmonkey.DeferredShadingUtils;
import me.merciless.dmonkey.RotationControl;

/**
 *
 * @author kwando
 */
public class CubesTestScene extends SimpleApplication {

  private DeferredSceneProcessor dsp;

  @Override
  public void simpleInitApp() {
    dsp = new DeferredSceneProcessor(this);
    viewPort.addProcessor(dsp);

    PointLight pl = new PointLight();
    pl.setPosition(new Vector3f(0, 3, 0));
    ColorRGBA color = ColorRGBA.randomColor();
    color.a = 1000;
    pl.setColor(color);
    pl.setRadius(12f);
    dsp.addLight(pl, paused);

    Material mat = assetManager.loadMaterial("DMonkey/TestMaterial.j3m");
    final Spatial model = assetManager.loadModel("Models/brokenCube.j3o");
    model.setMaterial(mat);
    Random random = new Random(7);
    for (int i = 0; i < 40; i++) {
      Vector3f randomPos = new Vector3f(random.nextFloat() * 10, random.nextFloat() * 10, random.nextFloat() * 10);
      model.setLocalTranslation(randomPos.subtractLocal(5, 5, 5));
      Spatial geom = model.clone();
      rootNode.attachChild(geom);
    }

    AmbientLight al = new AmbientLight();
    al.setColor(ColorRGBA.Cyan.mult(.05f));
    rootNode.addLight(al);

    stateManager.attach(new DebugControl(dsp));
    DeferredShadingUtils.scanNode(dsp, rootNode);
  }

  private void placeCube(float x, float y, float z) {
  }
}
