#define SPECULAR
#import "DMonkey/Shaders/DM_Light.glsllib"
uniform vec4 m_Color;
uniform float m_LightIntensity;
uniform vec3 m_LightDir;

const vec3 lightDirection = vec3(0.1, 0.3, 0.0);
const float GAMMA = 2.2;

void main() {
    dm_decode();

    vec3 lightDir = normalize((g_ViewMatrix * vec4(m_LightDir, 0.0)).xyz);
    //lightDir = normalize(m_LightDir);
    vec3 albedo = gamma(GBuffer.albedo, GAMMA);

    Light light = Light(vec4(gamma(m_Color.rgb, GAMMA), 0.0), vec3(0.0), lightDir);
    vec4 color = ComputeLighting(light);

    color += vec4(0.05, 0.05, 0.1, 0.0);

    gl_FragColor.rgb = albedo * color.rgb;
    gl_FragColor.rgb = gamma(gl_FragColor.rgb, 1.0 / GAMMA);
}

