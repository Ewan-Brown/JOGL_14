
#version 430
layout(location = 0) in vec3 position;

uniform mat4 mv_matrix;
uniform mat4 proj_matrix;


void main(void){
    //gl_Position is a 'global' variable that exists in this part of the pipeline, and it refers to the location of this vertex.
    //I'm literally just setting the position of this vertex the position of the vertex that i passed in that was genereated in my circle code.
    //It seems redundant but without it nothing will work (??)
    gl_Position = vec4(position,1.0);
}