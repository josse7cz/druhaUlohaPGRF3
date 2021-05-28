#version 430
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
uniform float time;
int uLevel= 9;

const float uRadius=0.30;

vec3 V0, V01, V02;
float PI= 3.1415926;


void
ProduceVertex( float s, float t )
{
    vec3 v = V0 + s*V01 + t*V02;
    v = normalize(v);
    vec3 n = v;
    vec3 tnorm = normalize( n ); // the transformed normal
    vec4 ECposition = vec4( (uRadius*v), 1. );
    gl_Position =  ECposition;
    EmitVertex( );

}



vec4 explode(vec4 position, vec3 normal)
{
    float magnitude = 2.0;
    vec3 direction = normal * ((sin(time) + 1.0) / 2.0) * magnitude;
    return position + vec4(direction, 0.0);
}


vec3 getNormal()
{
    vec3 a = vec3(gl_in[0].gl_Position) - vec3(gl_in[1].gl_Position);
    vec3 b = vec3(gl_in[2].gl_Position) - vec3(gl_in[1].gl_Position);
    return normalize(cross(a, b));
}


void main() {
//pokus
//    V01 = ( gl_PositionIn[1] - gl_PositionIn[0] ).xyz;
//    V02 = ( gl_PositionIn[2] - gl_PositionIn[0] ).xyz;
//    V0 = gl_PositionIn[0].xyz;
//    float numLayers = sides;
//    float dt = 1./ uLevel*sides;
//    float t_top = 1.;
//
//
//    for( int it = 0; it < numLayers; it++ )
//    {
//        float t_bot = t_top - dt;
//        float smax_top = 1. - t_top;
//        float smax_bot = 1. - t_bot;
//        int nums = it + 1;
//        float ds_top = smax_top / float( nums - 1 );
//        float ds_bot = smax_bot / float( nums );
//        fColor = vColor[1];
//        float s_top = 0.;
//        float s_bot = 0.;
//
//        for( int is = 0; is < nums; is++ )
//        {
//            ProduceVertex( s_bot, t_bot );
//            ProduceVertex( s_top, t_top );
//            fColor = vColor[1];
//            s_top += ds_top;
//            s_bot += ds_bot;
//            fColor = vColor[1];
//            EmitVertex();
//        }
//        ProduceVertex( s_bot, t_bot );
//        fColor = vColor[0];
//        EndPrimitive( );
//        t_top = t_bot;
//        t_bot -= dt;
//    }
//pokus






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


    //	// input geometry: gl_in[0]..gl_in[3] line adjacency
    //
    //	//directions
    //	vec2 dir1= normalize(gl_in[1].gl_Position.xy - gl_in[0].gl_Position.xy);
    //	vec2 dir2= normalize(gl_in[2].gl_Position.xy - gl_in[1].gl_Position.xy);
    //	vec2 dir3= normalize(gl_in[3].gl_Position.xy - gl_in[2].gl_Position.xy);
    //
    //	//normal vectors
    //	dir1= normalize(vec2(-1.0, 1.0)*dir1.yx);
    //	dir2= normalize(vec2(-1.0, 1.0)*dir2.yx);
    //	dir3= normalize(vec2(-1.0, 1.0)*dir3.yx);
    //
    //	//vectors of junction
    //	vec2 d1= normalize(dir1 + dir2);
    //	vec2 d2= normalize(dir2 + dir3);
    //
    //	//width of new bold line
    //	float k1 = 0.05/abs(dot(d1,dir2));
    //	float k2 = 0.05/abs(dot(d2,dir2));
    //
    //first triangle of the first trianglestrip
    //	fColor = vColor[1];
    //	gl_Position = gl_in[1].gl_Position + vec4(k1 * d1.xy, .0, .0);
    //    EmitVertex();
    //	gl_Position = gl_in[1].gl_Position - vec4(k1 * d1.xy, .0, .0);
    //    EmitVertex();
    //    fColor = vColor[2];
    //	gl_Position = gl_in[2].gl_Position + vec4(k2 * d2.xy, .0, .0);
    //    EmitVertex();
    //
    //	EndPrimitive();
    //
    //    //first triangle of the second trianglestrip
    //	fColor = vColor[1];
    //	gl_Position = gl_in[1].gl_Position - vec4(k1 * d1.xy, .0, .0);
    //    EmitVertex();
    //    fColor = vColor[2];
    //	gl_Position = gl_in[2].gl_Position + vec4(k2 * d2.xy, .0, .0);
    //    EmitVertex();
    //	gl_Position = gl_in[2].gl_Position - vec4(k2 * d2.xy, .0, .0);
    //    EmitVertex();
    //
    //	EndPrimitive();
}
