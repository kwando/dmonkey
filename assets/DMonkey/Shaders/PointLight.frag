#import "DMonkey/Shaders/GBuffer.glsllib"
#import "DMonkey/Shaders/Gamma.glsllib"

// Global uniforms from JME
uniform mat4 g_WorldViewProjectionMatrixInverse;
uniform vec3 g_CameraDirection;
uniform vec2 g_Resolution;

// Light parameters
uniform vec3 m_LightPosition;
uniform float m_LightIntensity;
uniform float m_LightRadius;
uniform vec4 m_LightColor;

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
		
    
    
    // Get view space camera direction
		//vec3 vsCameraDir = normalize(GBuffer.position);

    vec4 albedo = vec4(GBuffer.albedo, 1.0);
    
    // GammaCorrect textures
    albedo.rgb = gamma(albedo.rgb, 2.2);
    
    
    
    gl_FragColor = perform_lighting(GBuffer.position, m_LightPosition, GBuffer.normal,  albedo.rgb, m_LightColor, m_LightRadius);

    //gl_FragColor += 0.03;
    gl_FragColor *= albedo;
    
    return;
}

