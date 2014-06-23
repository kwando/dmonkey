#define SPECULAR
#import "DMonkey/Shaders/DM_Light.glsllib"
uniform vec4 m_Color;

uniform float m_LightIntensity;

uniform int m_DirectionalLights;
uniform vec4 m_DirectionalColors[1];
uniform vec3 m_Directions[1];

#define EXTRACT_DEPTH(cc)((cc).b + (cc).g / 256.0 + (cc).r / (256.0 * 256.0) + (cc).a / (256.0 * 256.0 * 256.0))

void main() {

	// Prepare
	prepare();

    vec3 albedo = gamma(GBuffer.albedo, 2.2);

	vec4 dirColors = vec4(0.0);

	for(int i = 0; i < 3; i++) {
		vec3 dir = -(g_ViewMatrix * vec4(m_Directions[i], 0)).xyz;

		Light light = Light(m_DirectionalColors[i], dir, dir);
		vec4 color = light.color * vec4(ComputeLighting(light).rgb, 1);
    	
    	dirColors += color;
	}
    
    vec4 ambient = m_Color;
    
   	gl_FragColor.rgb = ambient.rgb + dirColors.rgb;
    float rim = 1.0-abs(dot(vec3(0.0, 0.0, 1.0), GBuffer.normal));
    rim = pow(rim, 0.9)*1.0;

    
    gl_FragColor.rgb *= albedo;
}

