package me.merciless.dmonkey.lights;

import me.merciless.dmonkey.GBuffer;

import com.jme3.asset.AssetManager;
import com.jme3.light.Light;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.shape.Quad;

/**
 * @author kwando & Seth
 * Seth: I'm thinking of making this directional too.. i mean this light's frag will do directional lighting {
 * 	- Directional Color
 *  - Ambient Color (as in forward shading, all ambient lights get added into 1 single color)
 *  - Direction vec3 (we can have a bool or something, if vec3 == null material.set[useDirectionalLight : false])
 * }
 * Note: Is there any use for multiple directional lights? Shouln't directional light act like a sun/moon light?
 */
public class Ambient extends DLight {

	private Material material;
	private ColorRGBA ambientLightColor = new ColorRGBA(0, 0, 0, 0);
	private float intensity;

	public Ambient(Light light) {
		super(light);
		setMesh(new Quad(1,1));
		setName("Ambient Light Volume");
	}

	@Override
	public void initiate(GBuffer buff, AssetManager assetManager, ViewPort vp) {
		material = new Material(assetManager, "DMonkey/AmbientLight.j3md");
		material.setTexture("DiffuseBuffer", buff.getDiffuseBuffer());
		material.setTexture("DepthBuffer", buff.getDepthBuffer());
		material.setTexture("NormalBuffer", buff.getNormalsBuffer());
		RenderState rs = material.getAdditionalRenderState();
		rs.setBlendMode(RenderState.BlendMode.Additive);
		rs.setDepthTest(false);
		rs.setDepthWrite(false);
		
		MatParam mp = material.getParam("LightIntensity");

		intensity = mp == null ? intensity = 0.3f : (float) mp.getValue();
		
		setMaterial(updateAndGetMaterial());
		
//		material.setVector3("LightDirection", new Vector3f(0.7473137f, -0.4404087f, -0.49755645f));
//		material.setColor("DirectionalLightColor", ColorRGBA.DarkGray);
	}

	public void setLightIntensity(float intense) {
		intensity = intense;

		if(material != null)
			material.setFloat("LightIntensity", intensity);
	}
	
	@Override
	public Material updateAndGetMaterial() {
		material.setColor("AmbientColor", ambientLightColor);
		material.setFloat("LightIntensity", intensity);
		return material;
	}

	@Override
	public void clean() {
		setMaterial(null);
		ambientLightColor = null;
		material = null;
	}

	public void addAmbientLight(Light light) {
		ambientLightColor.addLocal(light.getColor());

		if(ambientLightColor.a > 1)
        	ambientLightColor.a = 1.0f;
	}

}
