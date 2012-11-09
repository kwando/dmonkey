#import "DMonkey/Shaders/GBuffer.glsllib"
#import "DMonkey/Shaders/Gamma.glsllib"

// Global uniforms from JME
uniform mat4 g_WorldViewProjectionMatrixInverse;
uniform vec3 g_CameraDirection;
uniform vec2 g_Resolution;

// Light parameters
uniform vec3 m_LightPosition;
uniform float m_LightIntensity;
uniform float m_CutoffAngle;
uniform vec3 m_LightDirection;
uniform vec4 m_LightColor;

const int num_lights = 1;
varying vec3 vsPosition;
varying vec3 vsNormal;

float linear_fallof(float D, float r, float kl){
  return D / (D + kl * r);
}
float quad_fallof(float D, float r, float kq){
  float D2 = D*D;
  return D2 / (D2 + kq * r * r);
}
float fallof(float D, float r, float kl, float kq){
  return linear_fallof(D, r, kl) * quad_fallof(D, r, kq);
}
vec4 perform_lighting(vec3 Position, vec3 LightPos, vec3 Normal, vec3 Albedo, vec4 LightColor, float LightRadius){
    vec4 color = vec4(0);

    vec3 LightVector = LightPos - Position;
    vec3 LightDir = normalize(LightVector);
    
    float lambert = clamp(dot(GBuffer.normal, LightDir), 0.0, 1.0);
    
    #ifdef SPECULAR
    vec3 vsCameraDir = normalize(GBuffer.position);
    vec3 reflection = reflect(LightDir, GBuffer.normal);
    float specular = clamp(dot(normalize(Position), normalize(reflection)), 0.0, 1.0);
    specular = pow(specular, 120.0)*GBuffer.specular;
    #else
    float specular = 0.0;
    #endif

    
    
    // Compute distance to light
		float Dist = length(LightVector);
    float fallof = fallof(LightRadius, Dist, 0.0, 1.0);
    fallof *= LightColor.a;

    color = LightColor*(lambert + specular) * fallof;
    return color;
}


void main() {
    vec2 TexCoord = gl_FragCoord.xy/g_Resolution;
		
		// Decode gbuffer
		dm_decode(TexCoord);
		
    vec3 LightPos = (g_ViewMatrix * vec4(m_LightPosition, 1.0)).xyz;

    float fade = clamp((vsPosition.z - GBuffer.position.z), 0.0, 1.0);
    float f = (5.0-length(LightPos-vsPosition))/5.0;
    f*= f*f*f*f*0.3;
    fade*= clamp(f,0.0, 1.0);
    gl_FragColor.rgb = vec3(fade)*m_LightColor.rgb*abs(dot(normalize(vsPosition), normalize(vsNormal)));
    //gl_FragColor.rgb = vec3(1.0)*abs(dot(normalize(vsPosition), normalize(vsNormal)));
}

