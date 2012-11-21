#import "DMonkey/Shaders/DM_Light.glsllib"

// Light parameters
uniform vec3 m_LightPosition;
uniform float m_LightIntensity;
uniform float m_CutoffAngle;
uniform float m_LightRange;
uniform vec3 m_LightDirection;
uniform vec4 m_LightColor;


varying vec3 vsPosition;

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

void main() {

	prepare();
		
    vec3 LightDir = normalize((g_ViewMatrix * vec4(m_LightDirection, 0.0)).xyz);

    Light light = Light(m_LightColor, m_LightPosition, LightDir);
    vec4 color = light.color + ComputeLighting(light);

    // GammaCorrect textures
    vec4 albedo = vec4(gamma(GBuffer.albedo, 2.2), 1.0);

    vec3 LightVector = GBuffer.position - light.position;
    float fallof = clamp(dot(normalize(LightVector), LightDir), 0.0, 1.0);
    
    if(cos(m_CutoffAngle/2.0) > fallof || dot(LightDir, GBuffer.normal) > 0.0){
    	discard;
    }

    // Calculate range fallof
    fallof = dot(LightVector, LightVector) / (m_LightRange * m_LightRange);
    fallof = 1.0 - clamp(fallof, 0.0, 1.0);

    gl_FragColor = color * fallof * albedo;
}

