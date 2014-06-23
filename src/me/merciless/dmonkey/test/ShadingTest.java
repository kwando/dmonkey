/*

 */
package me.merciless.dmonkey.test;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import me.merciless.dmonkey.DeferredSceneProcessor;
import me.merciless.dmonkey.DeferredShadingUtils;

/**
 *
 * @author kwando
 */
public class ShadingTest extends SimpleApplication {

  private DeferredSceneProcessor dsp;

  @Override
  public void simpleInitApp() {
    dsp = new DeferredSceneProcessor(this);
    viewPort.addProcessor(dsp);
    
    cam.setLocation(new Vector3f(0.65648174f, 0.793974f, 2.4181988f));
    cam.setRotation(new Quaternion(-0.009597543f, 0.9869187f, -0.064181946f, -0.14758065f));
    cam.setFrustumNear(0.5f);

    Spatial spatial = assetManager.loadModel("Models/test_object/test_object.j3o");

    rootNode.attachChild(spatial);

    DirectionalLight dl = new DirectionalLight();
    dl.setDirection(new Vector3f(1, -2, 1).normalizeLocal());
    
    ColorRGBA bluish = new ColorRGBA(214, 227, 240, 255).multLocal(1f/255);
    ColorRGBA orangeish = new ColorRGBA(240, 238, 202, 255).multLocal(1f/255);
    
    
    dl.setColor(gamma(orangeish.clone(), 1f).multLocal(1));
    rootNode.addLight(dl);


    spatial.move(0, -.5f, 0);
    
    AmbientLight light = new AmbientLight();
    light.setColor(bluish.mult(.0625f));
    rootNode.addLight(light);

    DeferredShadingUtils.scanNode(dsp, rootNode);
  }
  public ColorRGBA gamma(ColorRGBA color, float gamma){
    color.r = FastMath.pow(color.r, gamma);
    color.g = FastMath.pow(color.g, gamma);
    color.b = FastMath.pow(color.b, gamma);
    return color;
  }

  @Override
  public void simpleUpdate(float tpf) {
    cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
  } 
}
