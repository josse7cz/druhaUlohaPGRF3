#version 430
layout(location = 1) in vec3 inColor; // vstup z predchozi casti retezce
out vec4 outColor; // vystup z fragment shaderu
uniform vec4 uColor;
in float gLightIntensity;

void main() {
	//outColor = vec4(inColor, 1.0);
	outColor=vec4(gLightIntensity*uColor.rgb,1);

} 
