#import "DMonkey/Shaders/GBuffer.glsllib"
#import "DMonkey/Shaders/Gamma.glsllib"
uniform vec4 m_AmbientColor;

uniform vec3 g_CameraDirection;
uniform vec2 g_Resolution;
uniform mat4 g_WorldViewProjectionMatrixInverse;

uniform vec3 m_LightPosition;
uniform float m_LightIntensity;

#define EXTRACT_DEPTH(cc)((cc).b + (cc).g / 256.0 + (cc).r / (256.0 * 256.0) + (cc).a / (256.0 * 256.0 * 256.0))

void main() {
	// Compute GBuffer sample position
	vec2 TexCoord = gl_FragCoord.xy / g_Resolution;
	dm_decode(TexCoord);

    vec3 albedo = gamma(GBuffer.albedo, 2.2);
    //gl_FragColor.rgb = 0.05 * albedo;

    float mixValue = dot(GBuffer.normal, vec3(0.0,1.0,0.0))*0.5 + 0.5;
    
    #ifdef COLORIZE_AMBIENT
		gl_FragColor.rgb = m_AmbientColor.rgb * albedo * m_LightIntensity;
    #else
        gl_FragColor.rgb = mix(vec3(0.3,0.3,0.8)*0.5, vec3(0.7,0.7,1), mixValue) * albedo * m_LightIntensity;
    #endif
}

