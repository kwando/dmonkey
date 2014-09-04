package me.merciless.dmonkey3;

import java.util.ArrayList;
import java.util.List;
import me.merciless.dmonkey3.GBuffer;

/**
 *
 * @author kwando
 */
public class GBufferListenerMultiPlexer implements GBufferListener {

  private List<GBufferListener> listeners;

  public GBufferListenerMultiPlexer() {
    this.listeners = new ArrayList<GBufferListener>();
  }

  public void register(GBufferListener newListener) {
    if (newListener == null) {
      throw new IllegalArgumentException("Cannot register null listener");
    }
    listeners.add(newListener);
  }

  @Override
  public void onReshape(GBuffer gbuffer) {
    for (GBufferListener listener : listeners) {
      listener.onReshape(gbuffer);
    }
  }

  public static GBufferListenerMultiPlexer withListeners(GBufferListener... listeners) {
    GBufferListenerMultiPlexer mp = new GBufferListenerMultiPlexer();
    for (GBufferListener listener : listeners) {
      mp.register(listener);
    }
    return mp;
  }
}
