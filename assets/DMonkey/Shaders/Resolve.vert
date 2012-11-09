attribute vec3 inPosition;
attribute vec3 inNormal;
attribute vec2 inTexCoord;

varying vec2 TexCoord;

void main() { 
    // Vertex transformation 
    gl_Position = vec4(inPosition.xy*2.0-1.0,0.0,1.0); 
    TexCoord = inTexCoord;
}
