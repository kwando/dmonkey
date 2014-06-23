uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldMatrix;
uniform mat4 g_WorldMatrixInverse;
uniform mat3 g_NormalMatrix;
uniform mat4 g_ViewMatrix;
uniform mat4 g_WorldViewNatrix;

attribute vec3 inPosition;
attribute vec3 inNormal;
attribute vec2 inTexCoord;
attribute vec4 inTangent;

varying vec3 Normal;
varying vec3 Position;
varying vec2 TexCoord;
varying vec3 Binormal;
varying vec3 Tangent;

void main() { 
    vec4 pos = vec4(inPosition, 1.0);
    gl_Position = g_WorldViewProjectionMatrix * pos;
    
    Normal = g_NormalMatrix * inNormal;
    Position = (g_ViewMatrix * pos).xyz;
    TexCoord = inTexCoord;
    #ifdef NORMAL_MAP
    Tangent = normalize(g_NormalMatrix*inTangent.xyz);
    Binormal = cross(Normal, Tangent) * -inTangent.w;
    #endif
}
/*
[2014-06-18 09:16:47] Vlad Ravenholm:     #ifdef HAS_NORMAL
     vec3 wvTangent = normalize(g_NormalMatrix * inTangent.xyz);
     vec3 wvBinormal = cross(wvNormal, wvTangent);
 
  mat3 tbnMat = mat3(wvTangent, wvBinormal * -inTangent.w, wvNormal);
  
     TBN = tbnMat;
    #else
     Normal = wvNormal;
    #endif
[2014-06-18 09:17:17] Vlad Ravenholm: frag:
[2014-06-18 09:17:23] Vlad Ravenholm:   vec3 map = texture2D(m_NormalMap, TexCoord).xyz  * 2.0 - 1.0;
  vec3 normal = TBN * map;
[2014-06-18 09:17:31] Vlad Ravenholm: gl_FragData[1] = vec4(normalize(normal) * 0.5+0.5, 1.0);
*/