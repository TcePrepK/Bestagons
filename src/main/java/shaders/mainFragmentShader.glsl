#version 450 core

in vec2 resolution;
in vec3 topLeftCorner;
in vec3 xIncrement;
in vec3 yIncrement;
in vec3 cameraPos;

uniform mat4 oldMVPMatrix;
uniform mat4 oldViewMatrix;

uniform vec2 randVector2D;
uniform vec3 textureScale;
uniform vec3 chunkScale;
uniform float wFactor;
uniform bool resetEverything;
uniform vec3 oldCameraPos;

uniform bool renderingFractal;

layout (binding = 0) uniform sampler3D worldTexture;
layout (binding = 1) uniform sampler2D oldColorAttachment;
layout (binding = 2) uniform sampler2D oldDepthAttachment;
layout (binding = 3) uniform sampler2D oldRayDirAttachment;
layout (binding = 4) uniform sampler2D frameCountAttachment;
layout (binding = 5) uniform sampler2D oldNormalAttachment;
layout (location = 6) uniform sampler2D oldLightAttachment;

const int maxDistance = 500;
const int maxFrameCount = 255;
const bool isPathTracing = true;

layout (location = 0) out vec3 outColor;
layout (location = 1) out float outDepth;
layout (location = 2) out vec3 outRayDir;
layout (location = 3) out float outFrameCount;
layout (location = 4) out vec3 outNormal;
layout (location = 5) out float outLight;

#include /shaders/mainFractals.glsl
#include /shaders/mainRayUtils.glsl

void applyFog(Ray ray, HitRecord record);
int calculatePixelFrame(Ray ray, HitRecord record, vec2 oldScreenPixelPos, int frameCount);

void pathTracing() {
    const vec2 pixelPosition = gl_FragCoord.xy / resolution;
    int frameCount = int(texture(frameCountAttachment, pixelPosition).r * (maxFrameCount + 1.0));

    const vec2 offset = rand2D() / 20.0;
    const vec2 targetPixel = gl_FragCoord.xy + offset;
    const vec3 rayDir = normalize(topLeftCorner + (targetPixel.x * xIncrement) + (targetPixel.y * yIncrement));

    Ray ray = Ray(cameraPos, rayDir, vec3(0), false);
    outRayDir = rayDir;
    HitRecord record = ColorDDA(ray);
    outNormal = record.normal;

    // Reprojection
    vec4 screenPos = oldMVPMatrix * vec4(record.position, 1);
    screenPos /= screenPos.w;
    vec2 oldScreenPixelPos = screenPos.xy * 0.5 + 0.5;
    oldScreenPixelPos.y = 1 - oldScreenPixelPos.y;

    // Calculate frame count
    frameCount = calculatePixelFrame(ray, record, oldScreenPixelPos, frameCount);
    // Calculate frame count

    const vec3 oldColor = texture(oldColorAttachment, oldScreenPixelPos).rgb;
    const vec2 colorWeight = vec2(frameCount / (frameCount + 1.0), 1 / (frameCount + 1.0));
    // Reprojection

    // Calculating outputs
    outColor = (oldColor * colorWeight.x) + (ray.color * colorWeight.y);
    outFrameCount = frameCount / float(maxFrameCount);
    // Calculating outputs

    // Fog
    //    applyFog(ray, record);
    // Fog
}

void primaryTracing() {
    const vec2 pixelPosition = gl_FragCoord.xy / resolution;
    const vec3 rayDir = normalize(topLeftCorner + (gl_FragCoord.x * xIncrement) + (gl_FragCoord.y * yIncrement));

    Ray ray = Ray(cameraPos, rayDir, vec3(0), false);
    ColorDDA(ray);
    outColor = ray.color;

    // Fog
    //    applyFog(ray, record);
    // Fog
}

void main(void) {
    if (isPathTracing) {
        pathTracing();
    } else {
        primaryTracing();
    }
}

int calculatePixelFrame(Ray ray, HitRecord record, vec2 oldScreenPixelPos, int frameCount) {
    const vec3 oldNormal = texture(oldNormalAttachment, oldScreenPixelPos).rgb;
    if (oldNormal == vec3(0)) {
        return 0;
    }

    const float dotNormal = dot(oldNormal, record.normal);
    const float normalWeight = map(dotNormal, -0.5, 1, 0, 1);
    if (normalWeight == 0) {
        return 0;
    }

    const vec3 oldRayDir = texture(oldRayDirAttachment, oldScreenPixelPos).rgb;
    Ray oldRay = Ray(oldCameraPos, oldRayDir, vec3(0), false);
    HitRecord oldRecord = FinderDDA(oldRay, texture(oldDepthAttachment, oldScreenPixelPos).r * maxDistance);

    //    const float threshold = 0.1 * outDepth;
    const float threshold = length(vec2(2) / resolution * outDepth * maxDistance);
    const float dist = length(oldRecord.position - record.position);
    if (dist >= threshold) {
        return 0;
    }

    const float distWeight = map(dist, 0, threshold, 1, 0);
    const float weight = distWeight * normalWeight;

    //    outColor = vec3(weight);

    return int(round(frameCount * weight)) + 1;
}

void applyFog(Ray ray, HitRecord record) {
    const float x = record.distance / maxDistance;
    const float visibility = exp(-pow(x * 1.2, 5.0));
    const vec3 skyColor = getSkyColor(ray.dir);

    outColor = mix(skyColor, outColor, visibility);
}