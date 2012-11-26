package me.merciless.dmonkey;

import java.util.ArrayList;

import me.merciless.dmonkey.lights.DLight;
import me.merciless.dmonkey.lights.DPointLight;
import me.merciless.dmonkey.lights.DSpotLight;

import com.jme3.app.Application;
import com.jme3.light.Light;
import com.jme3.light.LightList;
import com.jme3.material.Material;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;

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
	
	public static void showDebug(DeferredSceneProcessor dsp, Application app,  boolean show) {
		
	    Node guiNode = ((Node) app.getGuiViewPort().getScenes().get(0));
	    
	    if(show) {
		    
		    Texture2D[] textures = { dsp.gbuffer.getNormals(), dsp.gbuffer.getDepth(), dsp.gbuffer.getDiffuse() };
		    
		    int vpHeight = app.getGuiViewPort().getCamera().getHeight();
		    int vpWidth = app.getGuiViewPort().getCamera().getWidth();
		    float scale = 4f;
		    for (int i = 0; i < textures.length; i++) {
		      Material material = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		      material.setTexture("ColorMap", textures[i]);
		      Picture pic = new Picture("Normals");
		      pic.setWidth(vpWidth / scale);
		      pic.setHeight(vpHeight / scale);
		      pic.move(vpWidth - (vpWidth / scale), vpHeight - (vpHeight / scale) * (i + 1), -1);
		      pic.setMaterial(material);
		      guiNode.attachChild(pic);
		    }
	    }
	    else
	    	guiNode.detachChildNamed("Normals");

	    guiNode.updateGeometricState();
	}

}
