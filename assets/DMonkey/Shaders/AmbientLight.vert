/*
* vertex shader template
*/
attribute vec2 inTexCoord;
void main() { 
    // Vertex transformation 
    gl_Position = vec4(inTexCoord*2.0-1.0,0.0,1.0);
}
