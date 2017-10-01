#version 400

in vec2 uv;

out vec4 colour;

uniform vec4 textColour;
uniform sampler2D sampler;

void main(){
    if (texture(sampler, uv).rgb == vec3(0, 0, 0)) {
        colour = vec4(1,1,1,0);
    } else {
        colour = textColour;
    }
}
