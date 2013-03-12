/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.merciless.dmonkey.test;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
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
    //settings.setVSync(true);
    app.setSettings(settings);
    app.start();
  }
  private DeferredSceneProcessor dsp;
  
  

  @Override
  public void simpleInitApp() {
    flyCam.setMoveSpeed(10);
    viewPort.addProcessor(dsp = new DeferredSceneProcessor(this));
    FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
    fpp.addFilter(new com.jme3.post.filters.BloomFilter(com.jme3.post.filters.BloomFilter.GlowMode.Scene));
    fpp.addFilter(new FXAAFilter());
    fpp.addFilter(new FXAAFilter());
    //viewPort.addProcessor(fpp);
    
    Spatial spat = assetManager.loadModel("Models/brokenCube.j3o");
    rootNode.attachChild(spat);
    
    spat = assetManager.loadModel("Models/transparent.j3o");
    spat.move(.5f, .5f, .5f);
    spat.setQueueBucket(Bucket.Translucent);
    ((Node)spat).depthFirstTraversal(new SceneGraphVisitorAdapter(){

      @Override
      public void visit(Geometry geom) {
        super.visit(geom);
        geom.setQueueBucket(Bucket.Translucent);
      }
    });
    rootNode.attachChild(spat);
    
    DirectionalLight dl = new DirectionalLight();
    dl.setDirection(Vector3f.UNIT_XYZ.mult(-1));
    
    rootNode.addLight(dl);
    dsp.addLight(dl, true);
  }
}
