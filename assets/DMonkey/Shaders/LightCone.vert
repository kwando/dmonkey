/*
* vertex shader template
*/

uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;
uniform mat3 g_NormalMatrix;

attribute vec3 inPosition;
attribute vec3 inNormal;
attribute vec2 inTexCoord;

varying vec3 vsPosition;
varying vec3 vsNormal;

void main() {
		#ifndef FULLSCREEN
    vec4 pos = vec4(inPosition, 1.0);
    vsPosition = (g_WorldViewMatrix * pos).xyz;
    vsNormal = g_NormalMatrix * inNormal;
    gl_Position = g_WorldViewProjectionMatrix * pos;
		#else
			gl_Position = vec4(inTexCoord*2.0-1.0,0.0,1.0);
		#endif
}
