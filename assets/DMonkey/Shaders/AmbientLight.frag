#define SPECULAR
#import "DMonkey/Shaders/DM_Light.glsllib"
uniform vec4 m_Color;

uniform float m_LightIntensity;

const vec3 lightDirection = vec3(1.0, 0.3, 0.0);
const float GAMMA = 2.2;

void main() {
    dm_decode();

    vec3 lightDir = normalize(lightDirection);
    
    vec3 albedo = gamma(GBuffer.albedo, GAMMA);

    float lambert = clamp(dot(lightDir, GBuffer.normal), 0.0, 1.0);
    
    vec3 hemi = mix(vec3(0.2, 0.2, 0.3),  vec3(0.9, 0.9, 1.0), dot(GBuffer.normal, vec3(0.0, 1.0, 0.0)));
    
    gl_FragColor.rgb = (lambert * 4.0 + hemi) * albedo * 0.1;

    gl_FragColor.rgb = gamma(gl_FragColor.rgb, 1.0/ GAMMA);
}

