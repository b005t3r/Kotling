#version 120

#ifdef GL_ES
precision mediump float;
#endif

// color form the vertex shader
varying vec4 vColor;

void main() {
	gl_FragColor = vColor;
}
