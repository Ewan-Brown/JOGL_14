
#version 430
out vec4 color;
in vec3 aColor;

uniform mat4 mv_matrix;
uniform mat4 proj_matrix;

void main(void)
{
    //sets the color of this fragment, that's it
    color = vec4(1.0,0.0,0.0,0.0);
}