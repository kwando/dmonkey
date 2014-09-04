/*

 */
package me.merciless.dmonkey3;

import com.jme3.post.SceneProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.texture.FrameBuffer;
import me.merciless.dmonkey3.GBuffer;

/**
 *
 * @author kwando
 */
public class GBufferProcessor implements SceneProcessor {

  private RenderManager rm;
  private ViewPort vp;
  private GBuffer gbuffer;
  private GBufferListener listerner;

  public GBufferProcessor(GBufferListener listener) {
    this.listerner = listener;
  }

  @Override
  public void initialize(RenderManager rm, ViewPort vp) {
    this.rm = rm;
    this.vp = vp;
    Camera cam = vp.getCamera();
    reshape(vp, cam.getWidth(), cam.getHeight());
  }

  @Override
  public void reshape(ViewPort vp, int w, int h) {
    gbuffer = new GBuffer(w, h);
    vp.setOutputFrameBuffer(gbuffer.fbo);
    if(listerner != null){
      listerner.onReshape(gbuffer);
    }
  }

  @Override
  public boolean isInitialized() {
    return rm != null;
  }

  @Override
  public void preFrame(float tpf) {
  }

  @Override
  public void postQueue(RenderQueue rq) {
    rm.setForcedTechnique("GBuffer");
  }

  @Override
  public void postFrame(FrameBuffer out) {
    rm.setForcedTechnique(null);
  }

  @Override
  public void cleanup() {
    gbuffer = null;
    this.vp = null;
    this.rm = null;
  }
}
