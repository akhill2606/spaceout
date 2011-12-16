package entities;

import graphics.model.Model;
import graphics.model.ModelLoader;
import graphics.render.Render3D;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Quaternion;

import util.manager.TextureManager;

/**
 * Skybox to make it seem like there's stars everywhere. Follows an Entity
 * around, so the end of it can never be reached.
 * 
 * @author TranquilMarmot
 * 
 */
public class Skybox extends Entity {
	// where to look for the skybox model
	private static final String MODEL_PATH = "res/models/";

	public float skyboxSize = Render3D.drawDistance * 0.8f;

	// the model to use for the skybox
	private Model model;

	// the skybox's center will always be on the entity that it is following
	public Entity following;

	/**
	 * Skybox constructor
	 * 
	 * @param following
	 *            The entity this skybox is following
	 */
	public Skybox(Entity following) {
		super();
		this.type = "skybox";

		this.following = following;

		rotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);

		model = ModelLoader.loadObjFile(MODEL_PATH + "skybox.obj", skyboxSize,
				TextureManager.STARS);
	}

	@Override
	public void update() {
		// keep the skybox centered on what it's following
		this.location.x = following.location.x;
		this.location.y = following.location.y;
		this.location.z = following.location.z;
	}

	@Override
	public void draw() {
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glDisable(GL11.GL_LIGHTING);
		model.getTexture().bind();
		GL11.glCallList(model.getCallList());
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	public void cleanup() {
	}

}
