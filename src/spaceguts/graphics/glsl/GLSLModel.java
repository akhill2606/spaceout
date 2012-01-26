package spaceguts.graphics.glsl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import spaceguts.util.resources.Textures;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.util.ObjectArrayList;

public class GLSLModel {
	private int vaoHandle, numIndices;
	private CollisionShape collisionShape;
	private Textures texture;
	boolean wireframe = false;
	
	public GLSLModel(CollisionShape collisionShape, int vaoHandle, int numIndices, Textures texture){
		// these all come from the model loader
		this.vaoHandle = vaoHandle;
		this.numIndices = numIndices;
		this.collisionShape = collisionShape;
		this.texture = texture;
	}
	
	public CollisionShape getCollisionShape(){
		return collisionShape;
	}
	
	public void render(){
		GL30.glBindVertexArray(vaoHandle);
		if(wireframe)
			GL11.glDrawArrays(GL11.GL_LINES, 0, numIndices);
		else
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, numIndices);
	}

	public Textures getTexture() {
		return texture;
	}	
}
