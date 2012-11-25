package me.merciless.dmonkey.lights;

import me.merciless.dmonkey.DeferredSceneProcessor;
import me.merciless.dmonkey.GBuffer;

import com.jme3.asset.AssetManager;
import com.jme3.light.Light;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

/**
 * @author Seth
 */
public final class DSpotLight extends DLight<SpotLight> {

	public DSpotLight(SpotLight light, DeferredSceneProcessor dsp) {
		super(light, dsp);
		
		SpotLight sp = getLight();
		mesh = ConeMesh.fromRangeAndCutoff(sp.getSpotRange(), sp.getSpotOuterAngle() * FastMath.RAD_TO_DEG);
		mesh.setStatic();
		setBoundRefresh();
	}

	@Override
	public void initialize(DeferredSceneProcessor dsp, GBuffer buff, AssetManager assetManager) {
		material = new Material(assetManager, "DMonkey/SpotLight.j3md");
		material.setTexture("DiffuseBuffer", buff.getDiffuse());
		material.setTexture("DepthBuffer", buff.getDepth());
		material.setTexture("NormalBuffer", buff.getNormals());
		material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Additive);
		material.getAdditionalRenderState().setDepthTest(false);
		material.getAdditionalRenderState().setDepthWrite(false);
		material.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Front);
	}

	@Override
	public void render(RenderManager rm, ViewPort vp) {
		SpotLight light = getLight();
	    Matrix4f viewMatrix = vp.getCamera().getViewMatrix();
	    material.setVector3("LightPosition", viewMatrix.mult(light.getPosition()));
		material.setColor("LightColor", light.getColor());
		material.setFloat("CutoffAngle", light.getSpotOuterAngle());
		material.setFloat("LightRange", light.getSpotRange());
		material.setVector3("LightDirection", light.getDirection());
	}

	@Override
	public void update(float tpf) {
		SpotLight light = getLight();
		
		setLocalTranslation(light.getPosition());
		getLocalRotation().lookAt(light.getDirection(), Vector3f.UNIT_Y);
		rotate(80,0,0);
	}

	@Override
	public void clean() {
		removeFromParent();
	}

	
}
