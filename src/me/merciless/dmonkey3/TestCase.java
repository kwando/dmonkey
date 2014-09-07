/*

 */
package me.merciless.dmonkey3;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;

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


    Geometry cylinder = new Geometry("Cylinder", new Box(1, 1, 1));
    cylinder.setMaterial(assetManager.loadMaterial("DMonkey/TestMaterial.j3m"));
    cylinder.addControl(new Rotator());
    TangentBinormalGenerator.generate(cylinder.getMesh());

    deferred.getRootNode().attachChild(cylinder);

    Geometry groundPlane = new Geometry("GroundPlane", new Box(10, .1f, 10));
    groundPlane.move(0, -2, 0);
    TangentBinormalGenerator.generate(groundPlane);
    groundPlane.setMaterial(cylinder.getMaterial().clone());
    groundPlane.getMaterial().setVector2("UV1Scale", new Vector2f(8, 8));
    ((Texture)(groundPlane.getMaterial().getTextureParam("DiffuseTex").getValue())).setWrap(Texture.WrapMode.Repeat);
    ((Texture)(groundPlane.getMaterial().getTextureParam("DiffuseTex").getValue())).setAnisotropicFilter(8);
    ((Texture)(groundPlane.getMaterial().getTextureParam("NormalTex").getValue())).setWrap(Texture.WrapMode.Repeat);

    deferred.getRootNode().attachChild(groundPlane);
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
