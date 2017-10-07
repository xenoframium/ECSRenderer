#version 400

in vec2 uv;

out vec4 colour;

uniform vec4 foregroundColour;
uniform vec4 backgroundColour;

uniform sampler2D sampler;

void main(){
    if (texture(sampler, uv).rgb == vec3(0, 0, 0)) {
        colour = backgroundColour;
    } else {
        colour = foregroundColour;
    }
}
