uniform mat4 g_ViewMatrix;
uniform mat3 g_NormalMatrix;
uniform sampler2D m_DiffuseTex;
uniform sampler2D m_NormalTex;
uniform sampler2D m_SpecularTex;
uniform float m_SpecularAmount;
uniform vec2 m_UV1Scale;
uniform vec2 g_FrustumNearFar;
uniform float g_Time;

varying vec3 Normal;
varying vec3 Position;
varying vec2 TexCoord;
varying vec3 Binormal;
varying vec3 Tangent;

void main() {
    vec3 normal = Normal;

    vec2 uv1 = TexCoord * m_UV1Scale;
    #ifdef NORMAL_MAP
    vec3 tsNormal = normalize(texture2D(m_NormalTex, uv1).xyz * 2.0 - 1.0);
	
    normal = normalize(Tangent * tsNormal.x + Binormal * tsNormal.y + Normal * tsNormal.z);
    vec3 vsNormal = (g_ViewMatrix * vec4(normal, 0.0)).xyz;
		mat3 TBN = mat3(normalize(Tangent), normalize(Binormal), normalize(Normal));
    #endif
	
    float specular = texture2D(m_SpecularTex, uv1).r;
    specular = m_SpecularAmount;
    gl_FragData[0] = vec4(normalize(normal)*0.5+0.5, specular);

    gl_FragData[1] = texture2D(m_DiffuseTex, uv1);
}