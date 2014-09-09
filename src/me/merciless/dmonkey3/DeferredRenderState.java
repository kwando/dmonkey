package me.merciless.dmonkey3;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.ColorRGBA;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;

/**
 *
 * @author kwando
 */
public class DeferredRenderState extends AbstractAppState {

  private ViewPort deferredViewPort;
  private GBufferProcessor df;
  private Application app;
  private Node node;
  private GBufferListenerMultiPlexer listener;

  public DeferredRenderState(){
    super();
    this.node = new Node("DeferredRootNode");
    this.listener = GBufferListenerMultiPlexer.withListeners();
  }
  public DeferredRenderState(GBufferListener listener) {
    this();
    this.listener.register(listener);
  }

  @Override
  public void initialize(AppStateManager stateManager, Application app) {
    super.initialize(stateManager, app);
    this.app = app;
    this.df = new GBufferProcessor(listener);
    
    RenderManager renderManager = app.getRenderManager();
    deferredViewPort = renderManager.createPreView("DeferredViewPort", app.getCamera());
    deferredViewPort.addProcessor(df);
    deferredViewPort.attachScene(node);
    deferredViewPort.setClearFlags(true, true, true);
    deferredViewPort.setBackgroundColor(ColorRGBA.BlackNoAlpha);
    
    LightRenderer lightRenderer = LightRenderer.initialize(app.getAssetManager());
    listener.register(lightRenderer);
    deferredViewPort.addProcessor(lightRenderer);
  }

  public Node getRootNode() {
    return node;
  }

  @Override
  public void update(float tpf) {
    super.update(tpf);
    node.updateLogicalState(tpf);
  }

  @Override
  public void render(RenderManager rm) {
    super.render(rm);
    node.updateGeometricState();
  }

  @Override
  public void cleanup() {
    super.cleanup();
    app.getRenderManager().removePreView(deferredViewPort);
    deferredViewPort.detachScene(node);
    deferredViewPort.removeProcessor(df);
    deferredViewPort = null;
    app = null;
  }
}
