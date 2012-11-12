package me.merciless.dmonkey.lights;

import me.merciless.dmonkey.GBuffer;
import me.merciless.dmonkey.LightQualityControl;
import me.merciless.dmonkey.LightPositionControl;

import com.jme3.asset.AssetManager;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.shape.Sphere;
import com.jme3.shader.VarType;

/**
 * @author kwando & Seth
 */
public final class DPointLight extends DLight {

	private Material material;
	
	public DPointLight(Light light) {
		super(light);
		setName("Point Light Volume");
		setMesh(new Sphere(6, 12, 0.5f));
	}

	@Override
	public void initiate(GBuffer buff, AssetManager assetManager, ViewPort vp) {
	    material = new Material(assetManager, "DMonkey/PointLight.j3md");
	    
	    material.setTexture("DiffuseBuffer", buff.getDiffuseBuffer());
	    material.setTexture("DepthBuffer", buff.getDepthBuffer());
	    material.setTexture("NormalBuffer", buff.getNormalsBuffer());

	    material.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
	    material.getAdditionalRenderState().setDepthTest(true);
	    material.getAdditionalRenderState().setDepthWrite(false);
	    material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Additive);
	    setMaterial(updateAndGetMaterial());

	    addControl(new LightPositionControl(material));
	    addControl(new LightQualityControl(material, vp.getCamera()));
	}
	
	public void setModelScale(float f) {
		setLocalScale(f);
	}

	public void setPosition(Vector3f vec3) {
		getLight(PointLight.class).setPosition(vec3);
		setLocalTranslation(vec3);// Since we got a position control
	}
	
	public void setColor(ColorRGBA vec4) {
		getLight().setColor(vec4);
		material.setColor("LightColor", vec4);
	}
	
	@Override
	public Material updateAndGetMaterial() {
		PointLight light = getLight();

		setModelScale(light.getRadius());// Temp
		
	    setLocalTranslation(getLight(PointLight.class).getPosition());
		material.setVector3("LightPosition", getLocalTranslation());

	    material.setColor("LightColor", light.getColor());
	    material.setFloat("LightRadius", light.getRadius() / 100f);
	    //material.setFloat("LightIntensity", 10f); // Unused? Plus shouln't it be alpha color dependent?

	    // XXX Uh two in one?
	    material.setParam("LightPositions", VarType.Vector3Array, new Vector3f[]{ getLocalTranslation() });
	    return material;
	}

	@Override
	public void clean() { // Bu-Bye
		removeFromParent();
		material = null;
	}
	
}
