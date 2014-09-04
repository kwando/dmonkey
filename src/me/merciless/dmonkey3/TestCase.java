/*

 */
package me.merciless.dmonkey3;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.system.AppSettings;
import com.jme3.util.TangentBinormalGenerator;
import me.merciless.dmonkey.test.PhysicalLighting;

/**
 *
 * @author kwando
 */
public class TestCase extends SimpleApplication {

  private DeferredRenderState deferred;
  private Material material;

  @Override
  public void simpleInitApp() {
    stateManager.attach(deferred = new DeferredRenderState());

    material = assetManager.loadMaterial("TestLightMaterial.j3m");
    Geometry geom = new Geometry("BoxNode", new Box(1, 1, 1));
    geom.setMaterial(material);
    geom.addControl(new Rotator());

    rootNode.attachChild(geom);
    DirectionalLight light = new DirectionalLight();
    light.setColor(ColorRGBA.White);
    light.setDirection(Vector3f.UNIT_XYZ.clone().mult(-1));

    rootNode.addLight(light);

    AmbientLight al = new AmbientLight();
    al.setColor(ColorRGBA.White.mult(.2f));
    rootNode.addLight(al);
    viewPort.setBackgroundColor(ColorRGBA.Pink.mult(0.2f));
    viewPort.setClearFlags(false, false, false);
    renderManager.removeMainView(viewPort);


    Geometry cylinder = new Geometry("Cylinder", new Cylinder(3, 8, .5f, 3));
    cylinder.setMaterial(assetManager.loadMaterial("DMonkey/TestMaterial.j3m"));
    cylinder.addControl(new Rotator());
    TangentBinormalGenerator.generate(cylinder.getMesh());

    deferred.getRootNode().attachChild(cylinder);
  }

  public static void main(String[] arg) {
    SimpleApplication app = new TestCase();
    app = new TestCase();
    AppSettings settings = new AppSettings(true);
    settings.setResolution(1280, 720);
    settings.setVSync(true);
    settings.setFullscreen(false);
    settings.setDepthBits(24);
    app.setSettings(settings);
    app.setShowSettings(false);
    app.start();
  }
}
