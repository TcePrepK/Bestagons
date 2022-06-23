#version 450 core

in vec2 position;

uniform int pentagonScale;
uniform ivec2 pentagonPos;
uniform mat4 PVMatrix;

uniform vec3 color;

out vec3 vertexColor;

#define sin60 0.866025;

void main(void) {
    vec2 originalPos = position * pentagonScale * vec2(1, -1);
    const vec2 scalePos = pentagonPos * pentagonScale * vec2(1, -1);

    originalPos.x += scalePos.x * sin60;
    originalPos.x += (pentagonPos.y % 2) * pentagonScale / 2.0 * sin60;
    originalPos.y += (3.0 / 4.0) * scalePos.y;

    vertexColor = color;
    gl_Position = PVMatrix * vec4(originalPos, 0, 1);
}