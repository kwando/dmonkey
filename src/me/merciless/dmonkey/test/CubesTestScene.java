package me.merciless.dmonkey.test;

import com.jme3.app.SimpleApplication;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.shape.Quad;
import java.util.Random;
import me.merciless.dmonkey.DebugControl;
import me.merciless.dmonkey.DeferredSceneProcessor;
import me.merciless.dmonkey.DeferredShadingUtils;
import me.merciless.dmonkey.RotationControl;
import me.merciless.util.TextureTools;

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
    setupPostProcessor();


    PointLight pl = new PointLight();
    pl.setPosition(new Vector3f(0, 3, 0));
    ColorRGBA color = ColorRGBA.Cyan.clone();
    pl.setColor(color);
    pl.setRadius(5f);
    rootNode.addLight(pl);

    for(int i = 0; i < 250; i++){
      randomizeLight();
    }


    Material mat = assetManager.loadMaterial("DMonkey/TestMaterial.j3m");
    TextureTools.setAnistropic(mat, "DiffuseTex", 8);
    final Spatial model = assetManager.loadModel("Models/brokenCube.j3o");
    model.setMaterial(mat);
    Random random = new Random(7);
    for (int i = 0; i < 210; i++) {
      Vector3f randomPos = new Vector3f(random.nextFloat() * 10, random.nextFloat() * 10, random.nextFloat() * 10);
      model.setLocalTranslation(randomPos.subtractLocal(5, 5, 5));
      Spatial geom = model.clone();
      geom.addControl(new RotationControl(new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat())));
      rootNode.attachChild(geom);
    }
    /*
    Spatial geom = model.clone();
    geom.scale(2);
    
    geom.setLocalTranslation(Vector3f.ZERO);
    geom.setMaterial(assetManager.loadMaterial("Materials/Transparent.j3m"));
    rootNode.attachChild(geom);

   /* AmbientLight al = new AmbientLight();
    al.setColor(ColorRGBA.Cyan.mult(.15f));
    rootNode.addLight(al);


    DirectionalLight dl = new DirectionalLight();
    al.setColor(ColorRGBA.Blue.mult(.3f));
    dl.setDirection(Vector3f.UNIT_XYZ.mult(-1));
    rootNode.addLight(dl);
    * */

    stateManager.attach(new DebugControl(dsp));
    DeferredShadingUtils.scanNode(dsp, rootNode);


  }

  private void placeCube(float x, float y, float z) {
  }

  private void randomizeLight() {
    PointLight pl = new PointLight();
    pl.setPosition(new Vector3f(
            FastMath.nextRandomFloat() * 10 - 5, FastMath.nextRandomFloat() * 10 - 5, FastMath.nextRandomFloat() * 10 - 5));
    ColorRGBA color = ColorRGBA.randomColor();
    pl.setColor(color);
    pl.setRadius(2);
    rootNode.addLight(pl);


    if(true){
      return;
    }

    // Setup a small floating quad!
    float size = .2f;
    Geometry geom = new Geometry("LightQuad", new Quad(size, size));
    geom.setLocalTranslation(pl.getPosition());
    geom.move(-size / 2, -size / 2, 0);
    Material lightMaterial = new Material(assetManager, "MatDefs/Unshaded.j3md");
    geom.setMaterial(lightMaterial);
    lightMaterial.setColor("Color", color);
    lightMaterial.setTexture("LightMap", assetManager.loadTexture("Textures/particletexture.jpg"));
    lightMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Additive);
    lightMaterial.getAdditionalRenderState().setDepthWrite(false);
    BillboardControl billboarder = new BillboardControl();
    billboarder.setAlignment(BillboardControl.Alignment.Camera);
    Node node = new Node();
    node.addControl(billboarder);
    node.attachChild(geom);
    rootNode.attachChild(node);
  }

  private void setupPostProcessor() {
    FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
    //fpp.addFilter(new com.jme3.post.filters.BloomFilter(com.jme3.post.filters.BloomFilter.GlowMode.Scene));
    fpp.addFilter(new FXAAFilter());
    fpp.addFilter(new FXAAFilter());
    viewPort.addProcessor(fpp);
  }
}
