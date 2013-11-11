package me.merciless.dmonkey.test;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.VideoRecorderAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.BatchNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.control.LightControl;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import me.merciless.dmonkey.DebugControl;
import me.merciless.dmonkey.DeferredSceneProcessor;
import me.merciless.dmonkey.DeferredShadingUtils;
import me.merciless.util.TextureTools;

/**
 *
 * @author kwando
 */
public class PhysicalLighting extends SimpleApplication {

  private DeferredSceneProcessor dsp;
  private BatchNode cubesNode;
  private AtomicLong collisionCount = new AtomicLong();

  @Override
  public void simpleInitApp() {
    System.out.println(getRenderer().getCaps());
    dsp = new DeferredSceneProcessor(this);
    viewPort.addProcessor(dsp);
    setupPostProcessor();
    BulletAppState bullet = new BulletAppState();
    bullet.setThreadingType(BulletAppState.ThreadingType.PARALLEL);

    VideoRecorderAppState recorder = new VideoRecorderAppState();
    //stateManager.attach(recorder);

    stateManager.attach(bullet);
    bullet.getPhysicsSpace().addCollisionListener(new PhysicsCollisionListener() {
      @Override
      public void collision(PhysicsCollisionEvent event) {
        long collisions = collisionCount.incrementAndGet();
        if (collisions % 1000 == 0) {
          System.out.printf("%dK collisions\n", collisions / 1000);
        }
      }
    });

    //bullet.setDebugEnabled(true);

    PlaneCollisionShape plane = new PlaneCollisionShape(new Plane(Vector3f.UNIT_Y, -10));
    PhysicsRigidBody body = new PhysicsRigidBody(plane);
    body.setMass(0);
    body.setRestitution(1);
    bullet.getPhysicsSpace().add(body);

    PointLight dl = new PointLight();
    ColorRGBA c = new ColorRGBA(0.19136488f, 0.5587857f, 0.60471356f, 1f);
    System.out.println(c);
    dl.setColor(c);
    dl.setPosition(new Vector3f(0, -3, 0));

    dl.setRadius(20);
    rootNode.addLight(dl);


    dl = new PointLight();
    dl.setColor(c);
    dl.setPosition(new Vector3f(3, -15, 3));

    dl.setRadius(20);
    rootNode.addLight(dl);


    PointLight pl = new PointLight();
    pl.setPosition(new Vector3f(0, 3, 0));
    ColorRGBA color = ColorRGBA.Cyan.clone();
    pl.setColor(color);
    pl.setRadius(5f);
    rootNode.addLight(pl);

    for (int i = 0; i < 0; i++) {
      randomizeLight();
    }


    Material mat = assetManager.loadMaterial("DMonkey/TestMaterial.j3m");
    TextureTools.setAnistropic(mat, "DiffuseTex", 8);
    final Spatial model = assetManager.loadModel("Models/brokenCube.j3o");
    model.setMaterial(mat);
    Random random = new Random(7);
    cubesNode = new BatchNode();
    for (int i = 0; i < 100; i++) {
      Vector3f randomPos = new Vector3f(random.nextFloat() * 10, random.nextFloat() * 10, random.nextFloat() * 10);
      model.setLocalTranslation(randomPos.subtractLocal(5, 5, 5));
      Spatial geom = model.clone();
      //geom.addControl(new RotationControl(new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat())));
      cubesNode.attachChild(geom);
      BoxCollisionShape box = new BoxCollisionShape(new Vector3f(.5f, .5f, .5f));
      RigidBodyControl control = new RigidBodyControl(box, 1);
      geom.addControl(control);
      control.setMass(120);
      control.setLinearSleepingThreshold(10f);
      control.setLinearDamping(0.4f);
      bullet.getPhysicsSpace().add(control);
    }
    rootNode.attachChild(cubesNode);
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

    setupControls();
  }

  private void randomizeLight() {
    PointLight pl = new PointLight();
    pl.setPosition(new Vector3f(
            FastMath.nextRandomFloat() * 10 - 5, FastMath.nextRandomFloat() * 8 - 10, FastMath.nextRandomFloat() * 10 - 5));
    ColorRGBA color = ColorRGBA.randomColor();
    pl.setColor(color);
    pl.setRadius(3);
    rootNode.addLight(pl);
  }

  private void setupPostProcessor() {
    FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
    
    BloomFilter bf = new BloomFilter(BloomFilter.GlowMode.Scene);
    fpp.addFilter(bf);
    bf.setExposurePower(10);
    bf.setBloomIntensity(1.2f);
    
    fpp.addFilter(new FXAAFilter());
    fpp.addFilter(new FXAAFilter());
    viewPort.addProcessor(fpp);
  }

  @Override
  public void simpleUpdate(float tpf) {
    super.simpleUpdate(tpf);
    lastFire += tpf;

    if (isFiring) {
      for (int i = 0; i < ballsPerFrame; i++) {
        addCanonBall();
      }
      lastFire = 0;
    }
  }
  private Queue<Geometry> freeBalls = new LinkedList<Geometry>();
  private float lastFire = 0;
  private float ballsPerSec = 300;
  private int maxBalls = 500;
  private int activeBalls = 0;
  private int ballsPerFrame = 10;
  private float maxLife = 3;
  private boolean isFiring = false;
  private CollisionShape shape = new SphereCollisionShape(0.075f);

  private void addCanonBall() {
    if (lastFire < 1f / ballsPerSec || activeBalls >= maxBalls) {
      return;
    }

    activeBalls++;
    Geometry geom;
    if (!freeBalls.isEmpty()) {
      geom = freeBalls.poll();
    } else {
      float size = 0.15f;
      
      Mesh box = new Quad(size, size);
      geom = new Geometry("projectile", box);

      PointLight pointLight = new PointLight();
      pointLight.setColor(ColorRGBA.randomColor());
      pointLight.setRadius(1.2f);

      LightControl lc = new LightControl(pointLight);
      geom.addControl(lc);


      dsp.addLight(pointLight, true);



      Material lightMaterial = new Material(assetManager, "MatDefs/Unshaded.j3md");
      geom.setMaterial(lightMaterial);
      lightMaterial.setColor("Color", ColorRGBA.randomColor());
      lightMaterial.setTexture("LightMap", assetManager.loadTexture("Textures/particletexture.jpg"));
      lightMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Additive);
      lightMaterial.getAdditionalRenderState().setDepthWrite(false);
      BillboardControl billboarder = new BillboardControl();
      billboarder.setAlignment(BillboardControl.Alignment.Camera);
      geom.addControl(billboarder);
    }
    geom.addControl(new AbstractControl() {
      private float time = 0;

      @Override
      protected void controlUpdate(float tpf) {
        time += tpf;
        if (time > maxLife) {
          Spatial spat = spatial;
          freeBalls.add((Geometry) spat);
          spat.removeFromParent();
          spat.removeControl(this);
          RigidBodyControl control = spat.getControl(RigidBodyControl.class);
          if (control != null) {
            stateManager.getState(BulletAppState.class).getPhysicsSpace().remove(control);
            spat.removeControl(control);
          }
          activeBalls--;

          LightControl lc = spat.getControl(LightControl.class);
          if (lc != null) {
            lc.getLight().setColor(ColorRGBA.Black);
          }
        }
      }

      @Override
      protected void controlRender(RenderManager rm, ViewPort vp) {
      }
    });
    float weight = 0.1f;
    RigidBodyControl control = new RigidBodyControl(shape, weight);
    geom.addControl(control);
    stateManager.getState(BulletAppState.class).getPhysicsSpace().add(control);
    control.setPhysicsLocation(cam.getLocation().add(cam.getDirection().mult(2)));
    control.setLinearVelocity(cam.getDirection().mult(20));
    control.setFriction(1f);
    control.setRestitution(0.56f);
    control.setAngularDamping(.67f);
    ColorRGBA rgba = ColorRGBA.randomColor();
    geom.getControl(LightControl.class).getLight().setColor(rgba);
    geom.getMaterial().setColor("Color", rgba);

    rootNode.attachChild(geom);
  }

  private void setupControls() {
    inputManager.addMapping("SHOOT", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
    inputManager.addListener(new ActionListener() {
      @Override
      public void onAction(String name, boolean isPressed, float tpf) {
        isFiring = isPressed;
      }
    }, "SHOOT");



    inputManager.addMapping("BATCH_CUBES", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
    inputManager.addListener(new ActionListener() {
      @Override
      public void onAction(String name, boolean isPressed, float tpf) {
        if (!isPressed) {
          System.out.println("BATCHING CUBES");
          rootNode.breadthFirstTraversal(new SceneGraphVisitorAdapter() {
            @Override
            public void visit(Geometry geom) {
              RigidBodyControl control = geom.getControl(RigidBodyControl.class);
              if (control != null) {
                geom.removeControl(control);
                stateManager.getState(BulletAppState.class).getPhysicsSpace().remove(control);
              }
            }

            @Override
            public void visit(Node geom) {
              RigidBodyControl control = geom.getControl(RigidBodyControl.class);
              if (control != null) {
                geom.removeControl(control);
                stateManager.getState(BulletAppState.class).getPhysicsSpace().remove(control);
              }
            }
          });



          //cubesNode.addControl(control);
          //control.setKinematic(true);
          //stateManager.getState(BulletAppState.class).getPhysicsSpace().add(control);
        }
      }
    }, "BATCH_CUBES");
  }
}
