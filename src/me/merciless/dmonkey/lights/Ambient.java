package me.merciless.dmonkey.lights;

import me.merciless.dmonkey.GBuffer;

import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.Light;
import com.jme3.light.LightList;
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
	private LightList lightList;
	private ColorRGBA ambientLightColor = new ColorRGBA(0, 0, 0, 0);
	private float intensity;

	public Ambient(Light light, LightList ll) {
		super(light);
		lightList = ll;
		setMesh(new Quad(1, 1));
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
		setMaterial(updateAndGetMaterial());
	}

	public void setLightIntensity(float intense) {
		intensity = intense;

		if(material != null)
			material.setFloat("LightIntensity", intensity);
	}
	
	@Override
	public Material updateAndGetMaterial() {
		material.setColor("AmbientColor", getAmbientColor());
		material.setFloat("LightIntensity", intensity);
		return material;
	}

    private ColorRGBA getAmbientColor() {
        ambientLightColor .set(0, 0, 0, 0);
        for (int j = 0; j < lightList.size(); j++) {
            Light l = lightList.get(j);
            if (l instanceof AmbientLight) {
                ambientLightColor.addLocal(l.getColor());
            }
        }
        
        if(ambientLightColor.a > 1)
        	ambientLightColor.a = 1.0f;

        return ambientLightColor;
    }


	@Override
	public void clean() {
		removeFromParent();
		lightList = null;
	}

}
