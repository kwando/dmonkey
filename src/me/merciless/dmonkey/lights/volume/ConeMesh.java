/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.merciless.dmonkey.lights.volume;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author kwando
 */
public class ConeMesh extends Mesh {

  private ConeMesh() {
  }

  private static float radiusAtHeight(float height, float cutoff) {
    return height * FastMath.tan(cutoff / 2);
  }

  public static Mesh volumeFromRangeAndCutoff(float range, float cutoff) {
    if (cutoff < 0 || cutoff > 80) {
      throw new IllegalArgumentException("cutoff should be between 0 and 80");
    }
    cutoff *= FastMath.DEG_TO_RAD;



    ConeMesh cm = new ConeMesh();
    int radialSamples = 24;
    int heightSegments = 4;
    float heightStep = range / (heightSegments - 1);


    float stepSize = FastMath.TWO_PI / radialSamples;
    FloatBuffer positionBuffer = BufferUtils.createFloatBuffer(radialSamples * heightSegments * 3);
    FloatBuffer normalBuffer = BufferUtils.createFloatBuffer(radialSamples * heightSegments * 3);
    Vector3f tmp = new Vector3f();
    for (int h = 0; h < heightSegments; h++) {
      float radius = radiusAtHeight(heightStep * h, cutoff);
      float height = h * heightStep;

      for (int i = 0; i < radialSamples; i++) {
        float cos = FastMath.cos(stepSize * i);
        float sin = FastMath.sin(stepSize * i);


        tmp.x = cos * radius;
        tmp.z = sin * radius;
        tmp.y = -height;

        positionBuffer.put(tmp.x);
        positionBuffer.put(tmp.y);
        positionBuffer.put(tmp.z);

        tmp.set(cos, cutoff / 2, sin);
        tmp.normalizeLocal();
        normalBuffer.put(tmp.x);
        normalBuffer.put(tmp.y);
        normalBuffer.put(tmp.z);
      }
    }
    IntBuffer indexBuffer = BufferUtils.createIntBuffer(radialSamples * (heightSegments - 1) * 6);
    for (int h = 0; h < heightSegments - 1; h++) {
      int offset = h * radialSamples;
      for (int i = 0; i < radialSamples; i++) {
        int a = offset + i;
        int b = offset + (i + 1) % radialSamples;
        int c = offset + radialSamples + i;
        int d = offset + radialSamples + (i + 1) % radialSamples;

        indexBuffer.put(a);
        indexBuffer.put(b);
        indexBuffer.put(c);

        indexBuffer.put(d);
        indexBuffer.put(c);
        indexBuffer.put(b);
      }
    }

    cm.setBuffer(Type.Position, 3, positionBuffer);
    cm.setBuffer(Type.Index, 3, indexBuffer);
    cm.setBuffer(Type.Normal, 3, normalBuffer);

    cm.setMode(Mode.Triangles);
    cm.updateBound();
    return cm;
  }

  public static ConeMesh fromRangeAndCutoff(float range, float cutoff) {
    if (cutoff < 0 || cutoff > 80) {
      throw new IllegalArgumentException("cutoff should be between 0 and 80");
    }
    cutoff *= FastMath.DEG_TO_RAD;

    float radius = range * FastMath.tan(cutoff / 2);

    ConeMesh cm = new ConeMesh();
    int samples = 32;
    float stepSize = FastMath.TWO_PI / samples;
    Vector3f[] positions = new Vector3f[samples + 2];
    for (int i = 0; i < samples; i++) {
      positions[i] = new Vector3f(FastMath.cos(stepSize * i) * radius, -range, FastMath.sin(stepSize * i) * radius);
    }
    positions[samples] = Vector3f.ZERO.add(0, 0, 0);
    positions[samples + 1] = Vector3f.ZERO.add(0, -range, 0);
    int[] index = new int[samples * 3 * 2];
    for (int i = 0; i < samples; i++) {
      int n = 0;
      index[6 * i + n++] = (i + 1) % samples;
      index[6 * i + n++] = i;
      index[6 * i + n++] = samples;


      index[6 * i + n++] = i;
      index[6 * i + n++] = (i + 1) % samples;
      index[6 * i + n++] = samples + 1;
    }

    cm.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(positions));
    cm.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(index));


    cm.setPointSize(3);
    cm.setMode(Mode.Triangles);
    cm.updateBound();
    return cm;
  }
}
