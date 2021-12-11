#version 150

#moj_import <fog.glsl>

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float DashOffset;
uniform float DashLength;

in float vertexDistance;
in vec4 vertexColor;
in float lineDistance;

out vec4 fragColor;

void main() {
    if (mod(lineDistance + DashOffset, DashLength * 2.0) < DashLength) {
        discard;
    }
    fragColor = linear_fog(vertexColor * ColorModulator, vertexDistance, FogStart, FogEnd, FogColor);
}