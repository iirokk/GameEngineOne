package physics;

import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;

public class Gravity {
    private static final float acceleration = -100;

    public static float getAcceleration() {
        return acceleration;
    }

    public static float getDeltaVerticalVelocity() {
        return acceleration * DisplayManager.getFrameTimeSeconds();
    }

    public static void objectPositionVelocityChange(EntityPhysicsQuantities physicsQuantities) {
        Vector3f newVelocity = new Vector3f(physicsQuantities.getVelocity());
        newVelocity.y += getDeltaVerticalVelocity() * physicsQuantities.getGravityFactor();

        Vector3f deltaPosition = new Vector3f(newVelocity);

        physicsQuantities.setPosition(Vector3f.add(physicsQuantities.getPosition(), (Vector3f) deltaPosition.scale(DisplayManager.getFrameTimeSeconds()), null));
        physicsQuantities.setVelocity(newVelocity);
    }
}
