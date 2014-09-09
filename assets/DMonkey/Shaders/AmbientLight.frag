#define SPECULAR
#define GAMMA_CORRECT
#import "DMonkey/Shaders/DM_Light.glsllib"
uniform vec4 m_Color;
uniform float m_LightIntensity;
uniform vec3 m_ViewLightDir;

const vec3 lightDirection = vec3(0.1, 0.3, 0.0);
const float GAMMA = 2.2;
vec3 FilmicMain(vec3 texColor){
  float range = 0.2;
  texColor *= range;
  vec3 x = max(vec3(0.0),texColor-0.004); // Filmic Curve
  vec3 retColor = (x*(6.2*x+0.5))/(x*(6.2*x+1.7)+0.06);
  return retColor;
}
void main() {
    dm_decode();
    
    vec3 albedo = GBuffer.albedo;
    #ifdef GAMMA_CORRECT
    albedo = gamma(GBuffer.albedo, GAMMA);
    #endif
    

    Light light = Light(vec4(gamma(m_Color.rgb, GAMMA), 0.0), vec3(0.0), m_ViewLightDir);
    vec4 color = ComputeLighting(light);

    color += vec4(0.05, 0.05, 0.1, 0.0);

    gl_FragColor.rgb = albedo * color.rgb * 1.5;

    //gl_FragColor.rgb = FilmicMain(gl_FragColor.rgb);
    #ifdef GAMMA_CORRECT
    gl_FragColor.rgb = gamma(gl_FragColor.rgb, 1.0 / GAMMA);
    #endif
}

