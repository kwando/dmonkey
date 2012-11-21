package me.merciless.dmonkey.lights;

import com.jme3.math.FastMath;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author kwando
 */
public class PyramidMesh extends Mesh {

  private float outerAngleInrad;
  private float range;

  public PyramidMesh(float outerAngleInrad, float range) {
    this.outerAngleInrad = outerAngleInrad;
    this.range = range;
    createMesh();
  }

  private void createMesh() {
    float halfSide = FastMath.atan(outerAngleInrad / 1.8f) * range;

    FloatBuffer pos = BufferUtils.createFloatBuffer(5 * 3);
    IntBuffer index = BufferUtils.createIntBuffer(6 * 3);
    pos.put(0);
    pos.put(0);
    pos.put(0);

    pos.put(-halfSide);
    pos.put(-range);
    pos.put(-halfSide);

    pos.put(-halfSide);
    pos.put(-range);
    pos.put(+halfSide);

    pos.put(+halfSide);
    pos.put(-range);
    pos.put(+halfSide);

    pos.put(+halfSide);
    pos.put(-range);
    pos.put(-halfSide);


    index.put(0);
    index.put(1);
    index.put(2);

    index.put(0);
    index.put(2);
    index.put(3);

    index.put(0);
    index.put(3);
    index.put(4);

    index.put(0);
    index.put(4);
    index.put(1);

    index.put(2);
    index.put(1);
    index.put(3);

    index.put(3);
    index.put(1);
    index.put(4);



    setBuffer(Type.Position, 3, pos);
    setBuffer(Type.Index, 3, index);
    setMode(Mode.Triangles);
    updateBound();
  }
}
