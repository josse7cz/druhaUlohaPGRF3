#version 330 compatibility
#extension GL_EXT_gpu_shader4: enable
#extension GL_EXT_geometry_shader4: enable
//input geometry
layout(triangles)in;
//output geometry
layout(triangle_strip, max_vertices = 100) out;

//input attribute
layout(location = 1) in vec3 vColor[];
//output attribute


layout(location = 1) out vec3 fColor;
in float vSides[];
uniform float sides;
const float PI=3.14;
uniform mat4 model,view,projection;
uniform mat3 normalMatrix;



///
uniform int uLevel;
uniform float uRadius;
out float gLightIntensity;
const vec3 LIGHTPOS = vec3( 0., 10., 0. );

vec3 V0, V01, V02;


void produceVertex( float s, float t )
{
    vec3 v = V0 + s*V01 + t*V02;
    v = normalize(v);
    vec3 n = v;
    mat4 modelingMatrix=view*model;
    vec3 tnorm = normalize( normalMatrix*n ); // the transformed normal transpose(inverse(model*view)) *
    vec3 normalTrans=transpose(inverse(mat3(view*model)))*tnorm;
   // vec3 tangentTrans=mat3(view*model)*tangent;//tecny vektor

    vec4 ECposition = modelingMatrix * vec4( (uRadius*v), 1. );
    gLightIntensity = abs( dot( normalize(LIGHTPOS - ECposition.xyz), tnorm ) );
    gl_Position = projection * ECposition;
    EmitVertex( );
}


void main() {

    V01 = ( gl_PositionIn[1] - gl_PositionIn[0] ).xyz;
    V02 = ( gl_PositionIn[2] - gl_PositionIn[0] ).xyz;
    V0 = gl_PositionIn[0].xyz;
    int numLayers = 1 << uLevel;
    float dt = 1. / float( numLayers );
    float t_top = 1.;
    for(int it = 0; it < numLayers; it++ )
    {
        float t_bot = t_top - dt;
        float smax_top = 1. - t_top;
        float smax_bot = 1. - t_bot;
        int nums = it + 1;
        float ds_top = smax_top / float( nums - 1 );
        float ds_bot = smax_bot / float( nums );
        float s_top = 0.;
        float s_bot = 0.;
        for( int is = 0; is < nums; is++ )
        {
            produceVertex( s_bot, t_bot );
            produceVertex( s_top, t_top );
            s_top += ds_top;
            s_bot += ds_bot;
            fColor = vColor[1];
        }
        produceVertex( s_bot, t_bot );
        EndPrimitive( );
        t_top = t_bot;
        t_bot -= dt;
    }






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
