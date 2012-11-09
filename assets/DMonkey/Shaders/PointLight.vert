/*
* vertex shader template
*/

uniform mat4 g_WorldViewProjectionMatrix;

attribute vec3 inPosition;
attribute vec3 inNormal;
attribute vec2 inTexCoord;

void main() {
		#ifndef FULLSCREEN
    vec4 pos = vec4(inPosition, 1.0);
    gl_Position = g_WorldViewProjectionMatrix * pos;
		#else
			gl_Position = vec4(inTexCoord*2.0-1.0,0.0,1.0);
		#endif
}
