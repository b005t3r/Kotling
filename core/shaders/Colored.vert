#version 120

#ifdef GL_ES
precision mediump float;
#endif

attribute vec2 position;
attribute vec4 color;

uniform mat4 projection;
uniform vec4 globalColor;

varying vec4 vColor;

void main() {
    vColor          = color * globalColor;
    gl_Position     = projection * vec4(position.xy, 0.0, 1.0);
}
