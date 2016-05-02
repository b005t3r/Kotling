#version 120

#ifdef GL_ES
precision mediump float;
#endif

attribute vec2 position;
attribute vec4 color;
attribute vec2 texCoords;

uniform mat4 projection;
uniform vec4 globalColor;

varying vec4 vColor;
varying vec2 vTexCoords;

void main() {
    vColor          = color * globalColor;
    vTexCoords      = texCoords;
    gl_Position     = projection * vec4(position.xy, 0.0, 1.0);
}
