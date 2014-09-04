package me.merciless.dmonkey3;

import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;

/**
 *
 * @author kwando
 */
public class GBuffer {

  final FrameBuffer fbo;
  final Texture2D normals;
  final Texture2D diffuse;
  final Texture2D depth;

  public GBuffer(int width, int height) {
    this(width, height, Image.Format.RGBA8);
  }

  public GBuffer(int width, int height, Image.Format format) {
    Image.Format.valueOf("RGBA8");
    this.fbo = new FrameBuffer(width, height, 1);

    normals = new Texture2D(width, height, format);
    diffuse = new Texture2D(width, height, format);
    normals.setMagFilter(Texture.MagFilter.Nearest);
    diffuse.setMagFilter(Texture.MagFilter.Bilinear);

    normals.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
    diffuse.setMagFilter(Texture.MagFilter.Nearest);

    normals.setMinFilter(Texture.MinFilter.NearestNoMipMaps);

    depth = new Texture2D(width, height, Image.Format.Depth);
    fbo.setDepthTexture(depth);
    fbo.addColorTexture(normals);
    fbo.addColorTexture(diffuse);
    fbo.setMultiTarget(true);
  }

  public Texture2D getDiffuseBuffer() {
    return diffuse;
  }

  public Texture2D getNormalBuffer() {
    return normals;
  }

  public Texture2D getDepthBuffer() {
    return depth;
  }
}
