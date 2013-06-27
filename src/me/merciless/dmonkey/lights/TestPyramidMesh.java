/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.merciless.dmonkey.lights;

import com.jme3.app.SimpleApplication;

/**
 *
 * @author kwando
 */
public class TestPyramidMesh extends SimpleApplication {

  public static void main(String[] arg) {
    SimpleApplication app = new TestPyramidMesh();

    app.setShowSettings(false);
    app.start();
  }

  @Override
  public void simpleInitApp() {
    /*flyCam.setMoveSpeed(10);
     Geometry geom = new Geometry("Pyramid", new PyramidMesh(20*FastMath.DEG_TO_RAD, 5));
    
     Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
     mat.setColor("Color", ColorRGBA.Blue);
     geom.setMaterial(mat);
    
     rootNode.attachChild(geom);*/
  }
}
