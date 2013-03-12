/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.merciless.dmonkey.test;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import me.merciless.dmonkey.DeferredSceneProcessor;

/**
 *
 * @author kwando
 */
public class TestTransparent extends SimpleApplication{
  public static void main(String[] arg){
    TestTransparent app = new TestTransparent();
    app.setShowSettings(false);
    AppSettings settings = new AppSettings(true);
    settings.setWidth(1280);
    settings.setHeight(720);
    settings.setVSync(true);
    app.setSettings(settings);
    app.start();
  }
  private DeferredSceneProcessor dsp;
  
  

  @Override
  public void simpleInitApp() {
    flyCam.setMoveSpeed(10);
    viewPort.addProcessor(dsp = new DeferredSceneProcessor(this));
    Spatial spat = assetManager.loadModel("Models/brokenCube.j3o");
    
    rootNode.attachChild(spat);
    
    DirectionalLight dl = new DirectionalLight();
    dl.setDirection(Vector3f.UNIT_XYZ.mult(-1));
    
    rootNode.addLight(dl);
    dsp.addLight(dl, true);
  }
}
