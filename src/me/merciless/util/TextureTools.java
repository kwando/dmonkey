/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.merciless.util;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.texture.Texture2D;

/**
 *
 * @author kwando
 */
public class TextureTools {
  public static void setAnistropic(Material material, String texturename, int level){
    MatParam param = material.getParam(texturename);
    Object object = param.getValue();
    if(object instanceof Texture2D){
      Texture2D texture = (Texture2D)object;
      texture.setAnisotropicFilter(level);
    }else{
      System.err.println("tried to set anistropic on a non texture param");
    }
  }
  
  public static void setWrapMode(Material material, String textureName, Texture2D.WrapMode wrapMode){
    MatParam param = material.getParam(textureName);
    Object object = param.getValue();
    if(object instanceof Texture2D){
      Texture2D texture = (Texture2D)object;
      texture.setWrap(wrapMode);
    }else{
      System.err.println("tried to set wrap mode on a non texture");
    }
  }
}
