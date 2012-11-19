package me.merciless.dmonkey;

import java.util.ArrayList;

import me.merciless.dmonkey.lights.DLight;
import me.merciless.dmonkey.lights.DPointLight;
import me.merciless.dmonkey.lights.DSpotLight;

import com.jme3.light.Light;
import com.jme3.light.LightList;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * @author Seth
 */
public final class DeferredShadingUtils {

	/**
	 * Will go through every child to get the light lists
	 * 
	 * @param dsp
	 * @param root
	 */
	protected static void scanNode(DeferredSceneProcessor dsp, Spatial root) {
		ArrayList<Light> processed = new ArrayList<Light>(); // We need this here to clean all the lights from the node
		LightList lights = root.getLocalLightList();

		for (Light light : lights) {

			if (getLight(dsp, light) != null) {
				processed.add(light);
				continue;
			}

			// TADA
			addLight(dsp, light, false);
		}

		for (Light light : processed) {
			root.removeLight(light);
		}

		if (root instanceof Node) {
			Node rootNode = (Node) root;
			for (Spatial child : rootNode.getChildren()) {
				if (child.getLocalLightList().size() > 0) {
					scanNode(dsp, child);
				}
			}
		}
	}

	protected static DLight addLight(DeferredSceneProcessor dsp, Light light, boolean check) {

		if (check) {
			DLight l = getLight(dsp, light);
			if (l != null)
				return l;
		}

		switch (light.getType()) {
			case Ambient:
			case Directional:
				dsp.getAmbient().addLight(light);
				return dsp.getAmbient();
			case Point: {
				DPointLight l = new DPointLight(light);
				l.initialize(dsp, dsp.getGBuffer(), dsp.getAssetManager());
				l.addControl(new LightQualityControl(l.getMaterial(), dsp.getLightViewport().getCamera()));
				dsp.getLightNode().attachChild(l);
				return l;
			}
			case Spot: {
				DSpotLight l = new DSpotLight(light);
				l.initialize(dsp, dsp.getGBuffer(), dsp.getAssetManager());
				dsp.getLightNode().attachChild(l);
				return l;
			}
			default:
				System.out.println("Unsuported light type: " + light.getType() + " - " + light.getName());
				break;
		}

		return null;
	}

	protected static DLight getLight(DeferredSceneProcessor dsp, Light light) {
		String id = "DPS-" + light.getType() + "-[" + light.getName() + "/" + light.hashCode() + "]";
		return (DLight) dsp.getLightNode().getChild(id);
	}
}
