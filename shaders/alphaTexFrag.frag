#version 400

in vec2 uv;

out vec4 colour;

uniform sampler2D sampler;

void main(){
    colour = texture(sampler, uv).rgba;
}
