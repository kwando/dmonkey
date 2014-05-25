#import "DMonkey/Shaders/DM_Light.glsllib"

// Point Light parameters
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

void main() {
  float inputGamma = 2.2;
	// Decode gbuffer
	prepare();

    // Compute distance to light
    vec3 LightVector = m_LightPosition - GBuffer.position;
    vec3 LightDir = normalize(LightVector);

	Light light = Light(m_LightColor, m_LightPosition, LightDir);

	vec4 color = light.color * ComputeLighting(light);

	float Dist = length(LightVector);
    float fallof = quad_fallof(m_LightRadius,Dist, 1.0);
    fallof *= 2.0;

    fallof *= clamp((m_LightRadius-Dist)/(m_LightRadius), 0.0, 1.0);
    gl_FragColor = color * fallof;

    // GammaCorrect textures
    vec3 albedo = gamma(GBuffer.albedo, inputGamma);
    gl_FragColor *= vec4(albedo, 1);
}

