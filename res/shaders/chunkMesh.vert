#version 450 core

in vec3 position;
in vec3 color;

uniform int pentagonScale;
uniform ivec2 pentagonPos;
uniform mat4 PVMatrix;

out vec3 vertexColor;

#define sin60 0.866025;

void main(void) {
    vec2 originalPos = position.xy * pentagonScale * vec2(1, -1);
    const vec2 scalePos = pentagonPos * pentagonScale * vec2(1, -1);

    originalPos.x += scalePos.x * sin60;
    originalPos.y += (3.0 / 4.0) * scalePos.y;

    gl_Position = PVMatrix * vec4(originalPos, position.z * pentagonScale, 1.0);
    vertexColor = color;
}