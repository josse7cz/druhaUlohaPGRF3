#version 330 compatibility
#extension GL_EXT_gpu_shader4: enable
#extension GL_EXT_geometry_shader4: enable
//input geometry
//layout(lines_adjacency) in;
layout(triangles)in;
//output geometry
layout(triangle_strip, max_vertices = 146) out;
//layout(points)out;
//layout(line_strip, max_vertices = 110) out;
//input attribute
layout(location = 1) in vec3 vColor[];
//output attribute
layout(location = 1) out vec3 fColor;
in float vSides[];
uniform float sides;
const float PI=3.14;
uniform mat4 model,view,projection;




void main() {

    //kruh
    for (int i = 0; i <= vSides[0]; i++) {
        // Angle between each side in radians
        float ang = PI * 2.0 / vSides[0] * i;
        fColor = vColor[1];
        // Offset from center of point (0.3 to accomodate for aspect ratio)
        vec4 offset = vec4(cos(ang) * 0.3, -sin(ang) * 0.4, 0.0, 0.0);
        gl_Position = gl_in[0].gl_Position + offset;


        EmitVertex();
    }
    EndPrimitive();
    //kruh

}
