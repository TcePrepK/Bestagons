#version 450 core

in vec3 vertexColor;

out vec3 outColor;

void main(void) {
    outColor = vertexColor;
}