#type vertex
#version 330 core

layout (location=0) in vec3 aPos;
layout (location=1) in vec4 aColor;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;

void main() {
    fColor = aColor;
    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}

#type fragment
#version 330 core

uniform float uTime;

in vec4 fColor;

out vec4 color;

void main() {
    float noise = 0.5 + 0.5 * sin(dot(fColor.xy * vec2(8.0, 8.0), vec2(12.9898, 78.233)) + uTime * 2.0);
    color = fColor * noise ;
}