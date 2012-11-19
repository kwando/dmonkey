#import "Common/ShaderLib/Hdr.glsllib"
#import "DMonkey/Shaders/GBuffer.glsllib"
#import "DMonkey/Shaders/Gamma.glsllib"

uniform sampler2D m_LightBuffer;
uniform mat4 m_ProjectionMatrixInverse;
uniform mat4 m_ProjectionMatrix;

varying vec2 TexCoord;

vec3 FilmicMain(vec3 texColor){
  float range = 0.2;
  texColor *= range;
  vec3 x = max(vec3(0.0),texColor-0.004); // Filmic Curve
  vec3 retColor = (x*(6.2*x+0.5))/(x*(6.2*x+1.7)+0.06);
  return retColor;
}

void main() {
    vec4 LBuffer = texture2D(m_LightBuffer, TexCoord);
    gl_FragColor.rgb = LBuffer.rgb;
    //gl_FragColor.rgb = gl_FragColor.rgb / (1.0 + gl_FragColor.rgb);
    //gl_FragColor.rgb = gamma(gl_FragColor.rgb, 1.0/2.2);
    //gl_FragColor.rgb = FilmicMain(gl_FragColor.rgb);
}
