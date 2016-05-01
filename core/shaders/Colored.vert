#version 120

#ifdef GL_ES
precision mediump float;
#endif

//our attributes
attribute vec2 position;
attribute vec4 color;

//our camera matrix
uniform mat4 projection;

//send the color out to the fragment shader
varying vec4 vColor;

void main() {
    vColor      = color;
    gl_Position = projection * vec4(position.xy, 0.0, 1.0);
}
