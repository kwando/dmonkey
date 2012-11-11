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
    Tangent = inTangent.xyz;
    Binormal = cross(Tangent, Normal);
    #endif
}
