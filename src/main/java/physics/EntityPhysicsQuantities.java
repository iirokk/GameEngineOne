package physics;

import org.lwjgl.util.vector.Vector3f;

public class EntityPhysicsQuantities {
    private Vector3f position;
    private Vector3f velocity;
    private final float gravityFactor;

    public EntityPhysicsQuantities(Vector3f position, Vector3f velocity, float gravityFactor) {
        this.position = position;
        this.velocity = velocity;
        this.gravityFactor = gravityFactor;
    }

    public EntityPhysicsQuantities(Vector3f position, Vector3f velocity) {
        this.position = position;
        this.velocity = velocity;
        this.gravityFactor = 1;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public float getGravityFactor() {
        return gravityFactor;
    }
}
