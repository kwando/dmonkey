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

			if (dsp.getLight(light) != null) {
				processed.add(light);
				continue;
			}

			// TADA
			dsp.addLight(light, false);
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
}
