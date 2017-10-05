#version 400

layout(location = 0) in vec3 vertexPosition;
layout(location = 1) in vec2 vertexUV;

out vec2 uv;

uniform mat4 mvp;

void main(){
    gl_Position = mvp * vec4(vertexPosition, 1.0);
    
    uv = vertexUV;
}
