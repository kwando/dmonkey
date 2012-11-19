package me.merciless.dmonkey.lights;

import me.merciless.dmonkey.DeferredSceneProcessor;
import me.merciless.dmonkey.GBuffer;

import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.shape.Quad;
import com.jme3.shader.VarType;
import com.jme3.util.SafeArrayList;

/**
 * @author Seth
 */
public final class Ambient extends DLight {

	// The reason this is here is to get the colors
	private final SafeArrayList<Light> lights = new SafeArrayList<Light>(Light.class);
	private int dLightSize;
	
	private final ColorRGBA ambientColor = new ColorRGBA(0,0,0,0);
	
	public Ambient() {
		super(null); // Nothing that needs to be keept track of

		mesh = new Quad(1, 1);
		mesh.setStatic();
		setBoundRefresh();
	}

	@Override
	public void initialize(DeferredSceneProcessor dsp, GBuffer buff, AssetManager assetManager) {
		material = new Material(assetManager, "DMonkey/AmbientLight.j3md");
		
	    material.setTexture("DiffuseBuffer", buff.getDiffuse());
	    material.setTexture("DepthBuffer", buff.getDepth());
	    material.setTexture("NormalBuffer", buff.getNormals());
	    
	    RenderState rs = material.getAdditionalRenderState();
	    rs.setBlendMode(RenderState.BlendMode.Additive);
	    rs.setFaceCullMode(FaceCullMode.Back);
	    rs.setDepthTest(false);
	    rs.setDepthWrite(false);
	}

	@Override
	public void render(RenderManager rm, ViewPort vp) {
		ambientColor.set(0, 0, 0, 0);

		Vector4f[] dlcs = new Vector4f[dLightSize];
		Vector3f[] dlds = new Vector3f[dLightSize];
		
		int position = 0;
		for (Light light : lights) {
			if(light instanceof AmbientLight)
				ambientColor.addLocal(light.getColor());
			else { // lights here can be only 2 types, ambient and directional
				DirectionalLight dl = (DirectionalLight)light;
				
				if(position >= dlcs.length) {
					System.out.println("Way too many direct lights!");
					break;
				}
				
				int pos = position++;
				dlcs[pos] = dl.getColor().toVector4f();

				//Matrix4f viewMatrix = vp.getCamera().getViewMatrix();
				dlds[pos] = dl.getDirection();//viewMatrix.mult(dl.getDirection());
			}
		}
		
		if(ambientColor.a > 1.0f)
			ambientColor.a = 1.0f;
		
		material.setColor("Color", ambientColor);
		
		material.setInt("DirectionalLights", dLightSize);
		material.setParam("DirectionalColors", VarType.Vector4Array, dlcs);
		material.setParam("Directions", VarType.Vector3Array, dlds);
		
		// and done
	    rm.renderGeometry(this);
	}
	
	@Override
	public void update(float tpf) {
	}

	@Override
	public void addLight(Light light) {
		if(!lights.contains(light)) {
			lights.add(light);
			
			if(light instanceof DirectionalLight)
				dLightSize++;
		}
	}

	@Override
	public void removeLight(Light light) {
		if(lights.remove(light) && light instanceof DirectionalLight)
				dLightSize--;
	}

	@Override
	public void clean() { /* always here */ }
	
}
