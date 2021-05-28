package p01geometryshader;


//import lvl2advanced.p01gui.p01simple.AbstractRenderer;


import lwjglutils.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import transforms.*;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static lwjglutils.ShaderUtils.GEOMETRY_SHADER_SUPPORT_VERSION;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_LINE_STRIP_ADJACENCY;


/**
* 
* @author PGRF FIM UHK
* @version 2.0
* @since 2019-09-02
*/
public class Renderer extends AbstractRenderer{

	OGLBuffers buffers;
	int shaderProgram;
	List<Integer> indexBufferData;
	List<Vec2D> vertexBufferDataPos;
	List<Vec3D> vertexBufferDataCol;
	private int setSides,locTime,viewLocation, projectionLocation, modelLocation,fpLevel,radius;
	private float sides=10, time=0;
	private Camera camera;
	private Mat4PerspRH projection;
	private Mat4 model;
	private Mat4 modelViewMatrix,normalMatrix;

	private double oldMx, oldMy;
	private boolean mousePressed=false;

	
	boolean update = true, mode = false;
	
	private GLFWKeyCallback   keyCallback = new GLFWKeyCallback() {
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
			if (action == GLFW_PRESS || action == GLFW_REPEAT){
				double speed = 0.25;
				switch (key) {
				case GLFW_KEY_R:
					initBuffers();
					update = true;
					break;
				case GLFW_KEY_M:
					mode = !mode;
					break;
					case GLFW_KEY_KP_ADD:
					sides = sides+1;
					break;
					case GLFW_KEY_KP_SUBTRACT:
					sides = sides-1;
					break;
					case GLFW_KEY_W:
						camera = camera.down(speed);
						break;
					case GLFW_KEY_S:
						camera = camera.up(speed);
						break;
					case GLFW_KEY_A:
						camera = camera.right(speed);
						break;
					case GLFW_KEY_D:
						camera = camera.left(speed);
						break;
                    case GLFW_KEY_LEFT_SHIFT:
                        camera = camera.forward(speed);
                        break;
                    case GLFW_KEY_LEFT_CONTROL:
                        camera = camera.backward(speed);
                        break;
					default:


				}
			}
		}
	};
    
    private GLFWWindowSizeCallback wsCallback = new GLFWWindowSizeCallback() {
    	@Override
    	public void invoke(long window, int w, int h) {
            if (w > 0 && h > 0 && 
            		(w != width || h != height)) {
            	width = w;
            	height = h;
            	if (textRenderer != null)
            		textRenderer.resize(width, height);

            }
        }
    };
    
    private GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback () {
    	@Override
		public void invoke(long window, int button, int action, int mods) {
			
			if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS){
				DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
				DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
				glfwGetCursorPos(window, xBuffer, yBuffer);
				double mouseX = (xBuffer.get(0) / (double) width) * 2 - 1;
				double mouseY = ((height - yBuffer.get(0)) / (double) height) * 2 - 1;
				indexBufferData.add(indexBufferData.size());
				vertexBufferDataPos.add(new Vec2D(mouseX, mouseY));
				vertexBufferDataCol.add(new Vec3D(mouseX / 2 + 0.5, mouseY / 2 + 0.5, 1));
				update = true;
			}
			if (button == GLFW_MOUSE_BUTTON_LEFT) {
				double[] xPos = new double[1];
				double[] yPos = new double[1];
				glfwGetCursorPos(window, xPos, yPos);
				oldMx = xPos[0];
				oldMy = yPos[0];
				mousePressed = action == GLFW_PRESS;
			}

		}
	};

	private final GLFWCursorPosCallback cursorPosCallback = new GLFWCursorPosCallback() {
		@Override
		public void invoke(long window, double x, double y) {
			if (mousePressed) {
				camera = camera.addAzimuth(Math.PI / 2 * (oldMx - x) / width);
				camera = camera.addZenith(Math.PI / 2 * (oldMy - y) / height);
				oldMx = x;
				oldMy = y;
			}
		}
	};

	/*
    mouse scroll
     */
	private final GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
		@Override
		public void invoke(long window, double xoffset, double yoffset) {
			camera = camera.forward(xoffset);
			camera = camera.backward(yoffset);
		}
	};


	public GLFWKeyCallback getKeyCallback() {
		return keyCallback;
	}

	@Override
	public GLFWWindowSizeCallback getWsCallback() {
		return wsCallback;
	}

	@Override
	public GLFWMouseButtonCallback getMouseCallback() {
		return mbCallback;
	}

	@Override
	public GLFWCursorPosCallback getCursorCallback() {
		return cursorPosCallback;
	}

	@Override
	public GLFWScrollCallback getScrollCallback() {
		return scrollCallback;
	}

	void initBuffers() {
		indexBufferData = new ArrayList<>();
		vertexBufferDataPos = new ArrayList<>();
		vertexBufferDataCol = new ArrayList<>();
		vertexBufferDataPos.add(new Vec2D(-0.5f, 0.0f));
		vertexBufferDataPos.add(new Vec2D(0.0f, 0.5));
		vertexBufferDataPos.add(new Vec2D(0.0f, -0.5f));
		vertexBufferDataPos.add(new Vec2D(0.5f, 0.0f));
		vertexBufferDataPos.add(new Vec2D(0.7f, 0.5f));
		vertexBufferDataPos.add(new Vec2D(0.9f, -0.7f));



		setSides= glGetUniformLocation(shaderProgram, "sides");
		locTime = glGetUniformLocation(shaderProgram, "time");
		modelLocation = glGetUniformLocation(shaderProgram, "model");
		viewLocation = glGetUniformLocation(shaderProgram, "view");
		projectionLocation = glGetUniformLocation(shaderProgram, "projection");
        fpLevel=glGetUniformLocation(shaderProgram,"fpLevel");
        radius=glGetUniformLocation(shaderProgram,"radius");
     

		camera = new Camera()
				.withPosition(new Vec3D(5, 5, 5))//oddálení, přiblizeni
				.withAzimuth(5 / 4f * Math.PI)
				.withZenith(-1 / 5f * Math.PI);

		model = new Mat4RotY(0.001);
		projection = new Mat4PerspRH(
				Math.PI / 3,
				height / (float) width,
				0.1,
				50
		);

		
		Random r = new Random();
		for(int i = 0; i < vertexBufferDataPos.size(); i++){
			indexBufferData.add(i);
			vertexBufferDataCol.add(new Vec3D(r.nextDouble(),r.nextDouble(),r.nextDouble()));
		}
	}
	
	void updateBuffers() {
		OGLBuffers.Attrib[] attributesPos = { 
				new OGLBuffers.Attrib("inPosition", 2), };
		OGLBuffers.Attrib[] attributesCol = {
				new OGLBuffers.Attrib("inColor", 3)
		};
		OGLBuffers.Attrib[] attributesSides = {
				new OGLBuffers.Attrib("sides", 1)
		};
		
		buffers = new OGLBuffers(ToFloatArray.convert(vertexBufferDataPos), attributesPos,
				ToIntArray.convert(indexBufferData));
		buffers.addVertexBuffer(ToFloatArray.convert(vertexBufferDataCol), attributesCol);



	}
	
	@Override
	public void init() {
		OGLUtils.shaderCheck();
		if (OGLUtils.getVersionGLSL() < GEOMETRY_SHADER_SUPPORT_VERSION){
			System.err.println("Geometry shader is not supported"); 
			System.exit(0);
		}
		
		OGLUtils.printOGLparameters();
		
		glClearColor(0.2f, 0.2f, 0.2f, 1.0f);

		String extensions = glGetString(GL_EXTENSIONS);
		if (extensions.indexOf("GL_ARB_enhanced_layouts") == -1)
			shaderProgram = ShaderUtils.loadProgram("/lvl2advanced/p05pipeline/p01geometryshader/geometry_OlderSM");
		else
			shaderProgram = ShaderUtils.loadProgram("/lvl2advanced/p05pipeline/p01geometryshader/geometry");
		
		initBuffers();
		textRenderer = new OGLTextRenderer(width, height);
		buffers=TriangleFactory.generateTriangle(20, 20);
}
	
	@Override
	public void display() {
		glViewport(0, 0, width, height);
		
		if (update) {
			updateBuffers();
			update = false;
			System.out.println(indexBufferData.size());
		}
		
		if (mode) 
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		else
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

		glUseProgram(shaderProgram);
		glUniform1f(setSides,sides);
		glUniform1f(locTime, time);
		glUniformMatrix4fv(projectionLocation, false, projection.floatArray());
		glUniformMatrix4fv(modelLocation, false, model.floatArray());
		glUniformMatrix4fv(viewLocation, false, camera.getViewMatrix().floatArray());
        glUniform1f(fpLevel,0.f);
        glUniform3f(radius,0.5f,1.0f,5.0f);



		time += 0.1;




		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); 
		
		//buffers.draw(GL_LINE_STRIP_ADJACENCY, shaderProgram,indexBufferData.size());
		buffers.draw(GL_TRIANGLES, shaderProgram);

		String text = new String(this.getClass().getName() + ": [I]nit. [M]ode");
		
		textRenderer.clear();
		textRenderer.addStr2D(3, 20, text);
		textRenderer.addStr2D(width-90, height-3, " (c) PGRF UHK");
		textRenderer.draw();
	}
}