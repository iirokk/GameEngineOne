package particles;


import org.lwjgl.util.vector.Vector3f;
import physics.EntityPhysicsQuantities;
import physics.Gravity;
import renderEngine.DisplayManager;

import java.util.zip.DeflaterInputStream;

public class Particle {

    private EntityPhysicsQuantities physicsQuantities;
    private float lifeLength;
    private float rotation;
    private float scale;

    private float elapsedTime = 0;

    public Particle(Vector3f position, Vector3f velocity, float gravityFactor, float lifeLength, float rotation, float scale) {
        this.physicsQuantities = new EntityPhysicsQuantities(position, velocity, gravityFactor);
        this.lifeLength = lifeLength;
        this.rotation = rotation;
        this.scale = scale;
        ParticleMaster.addParticle(this);
    }

    public Vector3f getPosition() {
        return physicsQuantities.getPosition();
    }

    public float getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    protected boolean update() {
        Gravity.objectPositionVelocityChange(physicsQuantities);
        elapsedTime += DisplayManager.getFrameTimeSeconds();
        return elapsedTime < lifeLength;
    }
}
