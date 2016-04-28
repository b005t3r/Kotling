#version 120

//our attributes
attribute vec2 position;
attribute vec4 color;
attribute vec2 texCoords;

//our camera matrix
uniform mat4 projection;

//send the color and texture coords out to the fragment shader
varying vec4 vColor;
varying vec2 vTexCoords;

void main() {
    vColor      = color;
    vTexCoords  = texCoords;
    gl_Position = projection * vec4(position.xy, 0.0, 1.0);
}
