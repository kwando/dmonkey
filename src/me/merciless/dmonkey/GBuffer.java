/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.merciless.dmonkey;

import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;

/**
 *
 * @author kwando
 */
public class GBuffer {

  protected final FrameBuffer fbo;
  protected final Texture2D normals;
  protected final Texture2D diffuse;
  protected final Texture2D Zbuffer;

  public GBuffer(int width, int height) {
    this(width, height, Image.Format.RGBA8);
  }

  public GBuffer(int width, int height, Image.Format format) {
    this.fbo = new FrameBuffer(width, height, 1);

    normals = new Texture2D(width, height, format);
    diffuse = new Texture2D(width, height, format);
    normals.setMagFilter(Texture.MagFilter.Nearest);
    diffuse.setMagFilter(Texture.MagFilter.Bilinear);

    normals.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
    diffuse.setMagFilter(Texture.MagFilter.Nearest);

    normals.setMinFilter(Texture.MinFilter.NearestNoMipMaps);

    Zbuffer = new Texture2D(width, height, Image.Format.Depth);
    fbo.setDepthTexture(Zbuffer);
    fbo.addColorTexture(normals);
    fbo.addColorTexture(diffuse);
    fbo.setMultiTarget(true);
  }
  
  public Texture2D getDiffuse() {
	return diffuse;
  }
  
  public Texture2D getNormals() {
	return normals;
  }
  
  public Texture2D getDepth() {
	return Zbuffer;
  }
}
