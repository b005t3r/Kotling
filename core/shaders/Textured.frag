#version 120

#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D texture;

// color form the vertex shader
varying vec4 vColor;
varying vec2 vTexCoords;

void main() {
    //sample the texture
    vec4 texColor = texture2D(texture, vTexCoords);

    //final color
    gl_FragColor = vColor * texColor;
}
