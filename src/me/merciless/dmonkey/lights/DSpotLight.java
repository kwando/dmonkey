package me.merciless.dmonkey.lights;

import me.merciless.dmonkey.GBuffer;
import me.merciless.dmonkey.LightPositionControl;
import me.merciless.dmonkey.lights.volume.ConeMesh;

import com.jme3.asset.AssetManager;
import com.jme3.light.Light;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;

/**
 * @author Seth
 */
public class DSpotLight extends DLight {

	private Material material;
	
	public DSpotLight(Light light) {
		super(light);
		setName("Spot Light Volume");
		
		SpotLight sp = getLight();
		
		ConeMesh mesh = ConeMesh.fromRangeAndCutoff(sp.getSpotRange(), 80);
		
		setMesh(mesh);
		
	}

	@Override
	public void initiate(GBuffer buff, AssetManager assetManager, ViewPort vp) {
		material = new Material(assetManager, "DMonkey/SpotLight.j3md");
		material.setTexture("DiffuseBuffer", buff.getDiffuseBuffer());
		material.setTexture("DepthBuffer", buff.getDepthBuffer());
		material.setTexture("NormalBuffer", buff.getNormalsBuffer());
		material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Additive);
		material.getAdditionalRenderState().setDepthTest(false);
		material.getAdditionalRenderState().setDepthWrite(false);
		material.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
		addControl(new LightPositionControl(material));
		
		setMaterial(updateAndGetMaterial());
	}

	@Override
	public Material updateAndGetMaterial() {
		SpotLight light = getLight();
		setLocalTranslation(light.getPosition());
		material.setColor("LightColor", light.getColor());
		material.setFloat("CutoffAngle", 80);
		material.setFloat("LightRange", light.getSpotRange());
		material.setVector3("LightDirection", Vector3f.UNIT_Y.mult(-1));
		material.setVector3("LightPosition", getLocalTranslation());
		return material;
	}

	@Override
	public void clean() {
		removeFromParent();
		material = null;
	}

}
