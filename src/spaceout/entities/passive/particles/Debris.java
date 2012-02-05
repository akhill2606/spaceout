package spaceout.entities.passive.particles;

import java.util.Random;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import spaceguts.entities.Entities;
import spaceguts.entities.Entity;
import spaceguts.graphics.render.Render3D;
import spaceguts.graphics.shapes.Circle2D;
import spaceguts.util.Noise;
import spaceguts.util.QuaternionHelper;
import spaceguts.util.resources.Textures;

/**
 * Fancy-looking space debris effect using perlin noise.
 * 
 * @author TranquilMarmot
 * @see Entity
 * 
 */
public class Debris extends Entity {
	/** array to hold all the stars */
	public Particle[] particles;

	/** random number generator */
	Random randy;

	/** the entity that the star field generates stars around */
	public Entity following;

	/** number of stars to have at a time */
	public int numParticles;

	/** how far to draw the stars */
	public float distance;
	
	private static Circle2D circle = new Circle2D(0.85f, 1);

	/**
	 * The random floats between 1.0f and 0.0f are multiplied by this when added
	 * to a random stars location. Making it a lot bigger makes the stars
	 * generate farther apart
	 */
	private final float JUMP_AMOUNT = 150000.0f;

	/**
	 * Create some cool space debris so you know which way you're going
	 * 
	 * @param following
	 *            The entity that the debris is being generated around
	 * @param numParticles
	 *            How many particles to generate
	 * @param distance
	 *            How far out to generate the particles
	 * @param seed
	 *            Seed for the random number generator
	 */
	public Debris(Entity following, int numParticles, float distance, long seed) {
		// initialize variables
		super();
		this.type = "debris";
		this.following = following;
		this.numParticles = numParticles;
		this.distance = distance;

		rotation = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);

		// initialize random number generator
		randy = new Random(seed);

		// set the location of the debris field to the entity that it's
		// following
		this.location.x = following.location.x;
		this.location.y = following.location.y;
		this.location.z = following.location.z;

		// initialize the array of particles
		particles = new Particle[numParticles];

		// each star is generated using the location of the previous star
		float prevX = location.x + randy.nextFloat() * 10.0f;
		float prevY = location.y + randy.nextFloat() * 10.0f;
		float prevZ = location.z - randy.nextFloat() * 100.0f;

		// fill the array
		for (int i = 0; i < numParticles; i++) {
			// generate a random particle and put it into the array
			Particle p = getRandomParticle(prevX, prevY, prevZ);
			particles[i] = p;
			// update the variables that keep track of previous particle
			// location
			prevX = p.location.x;
			prevY = p.location.y;
			prevZ = p.location.z;
		}
	}

	/**
	 * Goes through all the particles owned by this Debris field and checks to
	 * see if any have gone out of range (i.e. the player/camera moved) If a
	 * particle has gone out of range, it is replaced with a new, randomly
	 * generated one
	 */
	public void update() {
		// update the location of the debris field
		this.location.x = following.location.x;
		this.location.y = following.location.y;
		this.location.z = following.location.z;

		// loop through all the particles
		for (int i = 0; i < numParticles; i++) {
			Particle s = particles[i];
			// grab the particle's location
			float tempX = s.location.x;
			float tempY = s.location.y;
			float tempZ = s.location.z;

			// noise value generated by perlin noise used for changing star
			// location
			float noise = (float) Noise.noise((double) s.location.x,
					(double) s.location.y, (double) s.location.z);

			/*
			 * This checks along all three axes to see if the particle has gone
			 * too far (it's location is > or < the location of the debris field
			 * + the distance the field was initialized with) If the particle
			 * has gone too far, it sets the location that the new star is
			 * generated at at the opposite end that it just left. So, if a
			 * particle goes too far on, say, the X axis in the positive
			 * direction (particle x > debris field x + distance) then it is
			 * replaces with a particle generated at the opposite end (debris
			 * field x - distance)
			 */
			if (s.location.x > this.location.x + distance)
				tempX = this.location.x - distance
						+ (noise * (JUMP_AMOUNT / 10));
			else if (s.location.x < this.location.x - distance)
				tempX = this.location.x + distance
						- (noise * (JUMP_AMOUNT / 10));

			if (s.location.z > this.location.z + distance)
				tempZ = this.location.z - distance
						+ (noise * (JUMP_AMOUNT / 10));
			else if (s.location.z < this.location.z - distance)
				tempZ = this.location.z + distance
						- (noise * (JUMP_AMOUNT / 10));

			if (s.location.y > this.location.y + distance)
				tempY = this.location.y - distance
						+ (noise * (JUMP_AMOUNT / 10));
			else if (s.location.y < this.location.y - distance)
				tempY = this.location.y + distance
						- (noise * (JUMP_AMOUNT / 10));

			// if the temporary location variables have changed at all, it means
			// the particle we're checking has gone out of the debris field
			// distance and that a new one needs to replace it
			if (tempX != s.location.x || tempY != s.location.y
					|| tempZ != s.location.z) {
				particles[i].location.set(tempX, tempY, tempZ);
			}
		}
	}

	/**
	 * Generates a random star based on previous star coordinates
	 * 
	 * @param prevX
	 *            X coordinate of previous star
	 * @param prevY
	 *            Y coordinate of previous star
	 * @param prevZ
	 *            Z coordinate of previous star
	 * @return A new random star
	 */
	public Particle getRandomParticle(float prevX, float prevY, float prevZ) {
		float x = prevX;
		float y = prevY;
		float z = prevZ;

		// noise value generated by perlin noise used for changing star location
		float noise = (float) (Noise.noise((double) x, (double) y, (double) z));

		// For each axis, this decides whether it wants to go up or down
		if (randy.nextBoolean())
			x += noise * JUMP_AMOUNT;
		else
			x -= noise * JUMP_AMOUNT;

		if (randy.nextBoolean())
			y += noise * JUMP_AMOUNT;
		else
			y -= noise * JUMP_AMOUNT;

		if (randy.nextBoolean()) {
			if (randy.nextBoolean())
				z += noise * (JUMP_AMOUNT / 2);
			else
				z -= noise * (JUMP_AMOUNT / 2);
		}

		float size = noise * 200.0f;
		Particle ret = new Particle(x, y, z, size);
		return ret;
	}

	@Override
	public void draw() {
		// we don't want lighting for our particles
		//Render3D.program.setUniform("Light.LightEnabled", false);

		Quaternion revQuat = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
		Entities.camera.rotation.negate(revQuat);

		// bind a white texture
		Textures.WHITE.texture().bind();

		// loop through all the particles to draw them
		for (Particle s : particles) {
			s.update();
			// translate to the star
			float transx = this.location.x - s.location.x;
			float transy = this.location.y - s.location.y;
			float transz = this.location.z - s.location.z;
			
			Matrix4f oldModelview = new Matrix4f(Render3D.modelview);{
				// translate and scale the modelview to match the star
				Render3D.modelview.translate(new Vector3f(transx, transy, transz));
				Matrix4f.mul(Render3D.modelview, QuaternionHelper.toMatrix(Entities.camera.rotation), Render3D.modelview);
				Render3D.modelview.scale(new Vector3f(s.size, s.size, s.size));
				Render3D.program.setUniform("ModelViewMatrix", Render3D.modelview);

				// draw the star
				circle.draw();
			}Render3D.modelview = oldModelview;
		}

		// don't forget to re-enable lighting!
		//Render3D.program.setUniform("Light.LightEnabled", true);
	}

	/**
	 * A debris particle.
	 * 
	 */
	private class Particle {
		/** the size of the particle */
		float size;
		/** the location of the particle */
		Vector3f location;

		public Particle(float x, float y, float z, float size) {
			location = new Vector3f(x, y, z);
			this.size = size;
		}
		
		public void update(){
			/*
			float scale = 10.0f;
			if(randy.nextBoolean()){
				if(randy.nextBoolean())
					this.location.x += randy.nextFloat() * scale;
				else
					this.location.x -= randy.nextFloat() * scale;
			}
			
			if(randy.nextBoolean()){
				if(randy.nextBoolean())
					this.location.y += randy.nextFloat() * scale;
				else
					this.location.y -= randy.nextFloat() * scale;
			}
			
			if(randy.nextBoolean()){
				if(randy.nextBoolean())
					this.location.z += randy.nextFloat() * scale;
				else
					this.location.z -= randy.nextFloat() * scale;
			}
			*/
		}
	}

	@Override
	public void cleanup() {

	}
}
