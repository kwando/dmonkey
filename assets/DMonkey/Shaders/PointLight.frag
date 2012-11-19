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

	// Decode gbuffer
	prepare();

    // Compute distance to light
    vec3 LightVector = m_LightPosition - GBuffer.position;
    vec3 LightDir = normalize(LightVector);

	Light light = Light(m_LightColor, m_LightPosition, LightDir);

	vec4 color = light.color * ComputeLighting(light);

	float Dist = length(LightVector);
    float fallof = fallof(m_LightRadius, Dist, 0.0, 1.0);

    gl_FragColor = color * fallof * light.color.a;

    // GammaCorrect textures
    vec3 albedo = gamma(GBuffer.albedo, 2.2);
    gl_FragColor *= vec4(albedo, 1);
}

