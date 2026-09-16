#version 150

in vec4 Position;

uniform mat4 InverseTransformMatrix;
uniform vec2 OutSize;

out vec4 near_4;
out vec4 far_4;
out vec2 texCoord;

void main() {
    float x = -1.0;
    float y = -1.0;
    if (Position.x > 0.001) x = 1.0;
    if (Position.y > 0.001) y = 1.0;

    gl_Position = vec4(x, y, 0.2, 1.0);
    texCoord = Position.xy / OutSize;
    
    vec2 pos = gl_Position.xy/gl_Position.w;
    near_4 = InverseTransformMatrix * (vec4(pos, -1.0, 1.0));       
    far_4 = InverseTransformMatrix * (vec4(pos, +1.0, 1.0));
}
